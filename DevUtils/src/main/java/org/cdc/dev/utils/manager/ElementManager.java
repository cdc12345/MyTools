package org.cdc.dev.utils.manager;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.google.common.io.Files;
import com.google.gson.*;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.generator.GeneratorFile;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.io.FileIO;
import net.mcreator.java.CodeCleanup;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.util.DesktopUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ReferencesFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.dev.sections.DevUtilsSection;
import org.cdc.dev.utils.FileUtils;
import org.cdc.interfaces.GeneratorImpl;
import org.cdc.interfaces.IGenerator;
import org.cdc.interfaces.IMCreator;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ElementManager {
	private static final Logger LOGGER = LogManager.getLogger(ElementManager.class);

	public static void openElementTypeDefinition(IMCreator mcreator, ModElement modElement) {
		var map = mcreator.getGeneratorConfiguration().getDefinitionsProvider()
				.getModElementDefinition(modElement.getType());
		try {
			File cacheFile = File.createTempFile("temp", ".json");
			var code = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(map);
			Files.write(code.getBytes(), cacheFile);
			mcreator.getTabs().addTab(new MCreatorTabs.Tab(
					new CodeEditorView(mcreator.getOrigin(), code, "Type" + modElement.getTypeString() + ".json",
							cacheFile, true)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void openElementDefinition(IMCreator mCreator, ModElement modElement) {
		File genFile = new File(mCreator.getFolderManager().getModElementsDir(), modElement.getName() + ".mod.json");
		mCreator.getTabs().addTab(new MCreatorTabs.Tab(new CodeEditorView(mCreator.getOrigin(), genFile)));
		DesktopUtils.openSafe(genFile, true);
	}

	public static String toReadableString(Object object) {
		if (object instanceof Procedure procedure) {
			return new Gson().toJson(procedure);
		}
		return object.toString();
	}

	static JsonObject exportElement(ModElement modElement) {
		JsonObject element = new JsonObject();
		element.addProperty("name", modElement.getName());
		element.addProperty("registryName", modElement.getRegistryName());
		element.addProperty("type", modElement.getTypeString());
		element.addProperty("parentFolder", modElement.getFolderPath());

		//		var defi = new Gson().fromJson(modElement.getWorkspace().getModElementManager()
		//				.generatableElementToJSON(modElement.getGeneratableElement()), JsonObject.class);
		//		defi.remove("_type");
		//		element.add("definition", defi);
		var required = new JsonArray();
		for (ModElement searched : ReferencesFinder.searchModElementUsages(modElement.getWorkspace(), modElement)) {
			required.add(searched.getName());
		}
		element.add("required", required);

		try {
			element.addProperty("compile", modElement.doesCompile());
		} catch (NoSuchMethodError ignored) {

		}
		try {
			JsonObject generatedFilesExist = new JsonObject();
			for (File file : getAssociatedFiles(modElement)) {
				generatedFilesExist.addProperty(file.getPath(), file.exists() ? "existed" : "missing");
			}
			element.add("generatedFiles", generatedFilesExist);
		} catch (Throwable ignored) {
		}
		return element;
	}

	public static List<File> getAssociatedFiles(ModElement modElement) {
		if (modElement.getMetadata("files") instanceof List<?> fileList)
			// filter by files in workspace (e.g. so one can not create .mcreator file that would delete files on computer when opened)
			return fileList.stream()
					.map(e -> new File(modElement.getWorkspaceFolder(), e.toString().replace("/", File.separator)))
					.filter(modElement.getFolderManager()::isFileInWorkspace).toList();
		return List.of();
	}

	public static void createModifiers(Workspace workspace, @Nullable GeneratableElement generatable,
			List<String> limit) throws TemplateGeneratorException {
		if (generatable != null && generatable.getModElement().getType() == ModElementType.CODE && generatable.getModElement().isCodeLocked()) {
			return;
		}
		File parentModifier = FileUtils.getModifiersPath(workspace);
		var gen = new ArrayList<GeneratorFile>();
		if (generatable != null) {
			gen.addAll(generatable.getModElement().getGenerator().generateElement(generatable, false, false));
		}
		gen.addAll(generateBase(new GeneratorImpl(workspace.getGenerator()), null));
		var parserConfiguration = new ParserConfiguration();
		parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
		var sourceParser = new JavaParser(parserConfiguration);
		var templateParser = new JavaParser(parserConfiguration);
		var caches = new JsonArray();
		gen.forEach(a -> {
			if (Files.getFileExtension(a.getFile().getName()).equals("java") && (limit.isEmpty() || limit.contains(
					a.getFile().getName()))) {
				try {
					var source = sourceParser.parse(a.getFile()).getResult().orElseThrow();
					for (TypeDeclaration<?> sourceClass : source.getTypes()) {
						var modifier = new File(parentModifier, sourceClass.getFullyQualifiedName().orElseThrow());
						var templateClass = templateParser.parse(a.contents()).getResult()
								.orElseThrow(() -> new NoSuchElementException("No Result")).getTypes().stream()
								.filter(typeDeclaration -> typeDeclaration.getFullyQualifiedName()
										.equals(sourceClass.getFullyQualifiedName())).findFirst().orElseThrow();
						var modifierContent = new JsonObject();
						AtomicBoolean modifierFlag = new AtomicBoolean(false);
						var extendsTypes = new JsonArray();
						var implementationTypes = new JsonArray();
						if (sourceClass instanceof ClassOrInterfaceDeclaration _cls
								&& templateClass instanceof ClassOrInterfaceDeclaration _tem) {
							if (!_cls.getExtendedTypes().isEmpty() || !_cls.getImplementedTypes().isEmpty()) {

								for (ClassOrInterfaceType extendedType : _cls.getExtendedTypes()) {
									if (!_tem.getExtendedTypes().contains(extendedType)) {
										modifierFlag.set(true);
										extendsTypes.add(extendedType.toString());
									}
								}
								for (ClassOrInterfaceType implementedType : _cls.getImplementedTypes()) {
									if (!_tem.getImplementedTypes().contains(implementedType)) {
										modifierFlag.set(true);
										implementationTypes.add(implementedType.toString());
									}
								}
							}

						}
						modifierContent.add("extends", extendsTypes);
						modifierContent.add("implements", implementationTypes);
						var fields = new JsonArray();

						for (FieldDeclaration field : sourceClass.getFields()) {
							var fieldDeclaration1 = templateClass.getFieldByName(
									field.getVariable(0).getNameAsString());
							var fieldInfo = new JsonObject();
							if (fieldDeclaration1.isPresent()) {
								var fieldDeclaration = fieldDeclaration1.get();
								if (!Objects.equals(fieldDeclaration, field)) {
									fieldInfo.addProperty("name", field.getVariable(0).getNameAsString());
									fieldInfo.addProperty("index", sourceClass.getMembers().indexOf(field));
									fieldInfo.addProperty("originalBody", fieldDeclaration.toString());
									fieldInfo.addProperty("body", field.toString());
								}
							} else {
								fieldInfo.addProperty("name", field.getVariable(0).getNameAsString());
								fieldInfo.addProperty("body", field.toString());
							}
							if (!fieldInfo.isEmpty())
								fields.add(fieldInfo);
						}

						modifierContent.add("fields", fields);
						// Constructor detect
						var constructors = new JsonArray();
						sourceClass.getConstructors().forEach(sourceConstructor -> {
							var targetConstructor = templateClass.getConstructorByParameterTypes(
									sourceConstructor.getParameters().stream()
											.map(typeParameter -> typeParameter.getType().asString())
											.toArray(String[]::new));
							if (targetConstructor.isPresent()) {
								if (!targetConstructor.get().getBody().equals(sourceConstructor.getBody())) {
									var constructor = new JsonObject();
									modifierFlag.set(true);
									constructor.addProperty("signature", sourceConstructor.getSignature().asString());
									constructor.addProperty("originalBody",
											Objects.toString(targetConstructor.get().getBody()));
									var body = sourceConstructor.getBody();
									if (!body.isEmpty()) {
										var head = body.getStatement(0);
										head.getComment().ifPresent(comment -> {
											if (comment.getContent().equalsIgnoreCase("Head")) {
												if (head.isExpressionStmt() && head.asExpressionStmt().getExpression()
														.isMethodCallExpr() && head.asExpressionStmt().getExpression()
														.asMethodCallExpr().getScope().isEmpty())
													constructor.addProperty("head", head.toString());
											}
										});
										var tail = body.getStatements().getLast().orElse(new EmptyStmt());
										tail.getComment().ifPresent(comment -> {
											if (comment.getContent().equalsIgnoreCase("Tail")) {
												if (tail.isExpressionStmt() && tail.asExpressionStmt().getExpression()
														.isMethodCallExpr() && tail.asExpressionStmt().getExpression()
														.asMethodCallExpr().getScope().isEmpty())
													constructor.addProperty("tail", tail.toString());
											}
										});
									}
									constructor.addProperty("body", Objects.toString(sourceConstructor));
									constructors.add(constructor);
								}
							} else {
								var constructor = new JsonObject();
								modifierFlag.set(true);
								constructor.addProperty("signature", sourceConstructor.getSignature().asString());
								constructor.addProperty("body", Objects.toString(sourceConstructor.toString()));
								constructors.add(constructor);
							}
						});
						var methods = new JsonObject();
						sourceClass.getMethods().forEach(sourceMethod -> {
							AtomicReference<MethodDeclaration> result = new AtomicReference<>();
							if (templateClass.getMethodsByName(sourceMethod.getNameAsString()).stream()
									.noneMatch(methodDeclaration -> {
										if (methodDeclaration.getSignature().equals(sourceMethod.getSignature())) {
											result.set(methodDeclaration);
											return true;
										}
										return false;
									})) {
								modifierFlag.set(true);
								var method = new JsonObject();
								method.addProperty("signature", sourceMethod.getSignature().asString());
								method.addProperty("body", sourceMethod.toString());
								methods.add(sourceMethod.getNameAsString(), method);
							} else {
								var method = new JsonObject();
								var generatedMethod = result.get();
								if (!generatedMethod.getBody().equals(sourceMethod.getBody())) {
									modifierFlag.set(true);
									method.addProperty("signature", sourceMethod.getSignature().asString());
									method.addProperty("originalBody",
											Objects.toString(generatedMethod.getBody().orElseThrow()));
									var statements = sourceMethod.getBody()
											.orElseThrow(() -> new NoSuchElementException("No Body")).getStatements()
											.stream().filter(statement -> !statement.isReturnStmt()).toList();
									if (!statements.isEmpty()) {
										var head = statements.getFirst();
										head.getComment().ifPresent(comment -> {
											if (comment.getContent().equalsIgnoreCase("Head")) {
												if (head.isExpressionStmt() && head.asExpressionStmt().getExpression()
														.isMethodCallExpr() && head.asExpressionStmt().getExpression()
														.asMethodCallExpr().getScope()
														.isEmpty()/*ensure that it is a method call like method()*/) {
													method.addProperty("head", head.toString());
												}
											}
										});

										var tail = statements.getLast();

										tail.getComment().ifPresent(comment -> {
											if (comment.getContent().equalsIgnoreCase("Tail")) {
												if (tail.isExpressionStmt() && tail.asExpressionStmt().getExpression()
														.isMethodCallExpr() && tail.asExpressionStmt().getExpression()
														.asMethodCallExpr().getScope().isEmpty())
													method.addProperty("tail", tail.toString());
											}
										});
									}

									method.addProperty("body", Objects.toString(sourceMethod.getBody().orElseThrow()));
									methods.add(sourceMethod.getNameAsString(), method);
								}
							}
						});
						if (modifierFlag.get()) {
							var importsJsonArray = new JsonArray();
							for (ImportDeclaration anImport : source.getImports()) {
								importsJsonArray.add(anImport.getNameAsString());
							}
							modifierContent.add("imports", importsJsonArray);
							LOGGER.info("Saved to {}", modifier.getPath());
							modifierContent.add("methods", methods);
							modifierContent.add("constructors", constructors);
							java.nio.file.Files.copy(new ByteArrayInputStream(
											modifierContent.toString().getBytes(StandardCharsets.UTF_8)), modifier.toPath(),
									StandardCopyOption.REPLACE_EXISTING);
							caches.add(a.source().getTemplateDefinition().get("template").toString());
						} else {
							modifier.delete();
						}
					}
				} catch (Exception e) {
					LOGGER.info("Error {} in {}, content {}", e, a.getFile(), a.contents());
					e.printStackTrace();
				}
			}
		});
		try {
			Files.write(caches.toString().getBytes(), new File(parentModifier, "caches"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean applyPatchesToElement(Workspace workspace, @Nullable GeneratableElement generatable)
			throws TemplateGeneratorException {
		File parentModifier = FileUtils.getModifiersPath(workspace);
		if (Objects.requireNonNullElse(parentModifier.listFiles(), new File[0]).length == 0) {
			return false;
		}
		var gen = new ArrayList<GeneratorFile>();
		if (generatable != null) {
			gen.addAll(generatable.getModElement().getGenerator().generateElement(generatable, false, false));
		} else {
			var cache = new File(parentModifier, "caches");
			var cachJsonArray = new JsonArray();
			if (cache.isFile()) {
				cachJsonArray = new Gson().fromJson(FileIO.readFileToString(cache), JsonArray.class);
			}
			gen.addAll(generateBase(new GeneratorImpl(workspace.getGenerator()), cachJsonArray));
		}
		var parserConfiguration = new ParserConfiguration();
		parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
		StaticJavaParser.setConfiguration(parserConfiguration);
		var sourceParser = new JavaParser(parserConfiguration);
		AtomicBoolean modifierApplied = new AtomicBoolean(false);
		gen.forEach(generatorFile -> {

			if (Files.getFileExtension(generatorFile.getFile().getName()).equals("java")) {
				try {
					var parse = sourceParser.parse(generatorFile.getFile());
					var resul = parse.getResult()
							.orElseThrow(() -> new NoSuchElementException(parse.getProblems().toString()));
					for (TypeDeclaration<?> sourceClass : resul.getTypes()) {
						var modifier = new File(parentModifier, sourceClass.getFullyQualifiedName()
								.orElseThrow(() -> new NoSuchElementException("No full quilified name")));
						if (modifier.exists()) {
							modifierApplied.set(true);
							var modifierContent = new Gson().fromJson(java.nio.file.Files.readString(modifier.toPath()),
									JsonObject.class);
							LOGGER.info("Applied {}", modifier.getPath());
							var imports = modifierContent.getAsJsonArray("imports");
							imports.forEach(anImport -> {
								resul.addImport(anImport.getAsString());
							});
							//apply extends
							if (sourceClass instanceof ClassOrInterfaceDeclaration _cls) {
								var extended = modifierContent.getAsJsonArray("extends");
								if (!extended.isEmpty()) {
									_cls.getExtendedTypes().clear();
									extended.forEach(a -> {
										_cls.addExtendedType(a.getAsString());
									});
								}
								var implemented = modifierContent.getAsJsonArray("implements");
								var implementedClasses = _cls.getImplementedTypes().stream().map(Node::toString)
										.collect(Collectors.toSet());
								for (JsonElement jsonElement : implemented) {
									if (!implementedClasses.contains(jsonElement.getAsString())) {
										_cls.addImplementedType(jsonElement.getAsString());
									}
								}
							}
							// apply fields
							var fields = modifierContent.getAsJsonArray("fields");
							for (JsonElement field : fields) {
								var fieldInfo = field.getAsJsonObject();
								var oldField = sourceClass.getFieldByName(fieldInfo.get("name").getAsString());
								var body = fieldInfo.get("body").getAsString();
								if (fieldInfo.has("originalBody")) {
									if (oldField.isPresent()) {
										if (oldField.get().toString()
												.equals(fieldInfo.get("originalBody").getAsString())) {
											sourceClass.remove(oldField.get());
											var newField = StaticJavaParser.parseBodyDeclaration(body);

											sourceClass.getMembers()
													.add(Math.clamp(fieldInfo.get("index").getAsInt(), 0,
															sourceClass.getMembers().size() - 1), newField);
										} else {
											oldField.get().setBlockComment("""
													Missing Body:
													%s
													""".formatted(body));
										}
									} else {
										sourceClass.addOrphanComment(new BlockComment("""
												Missing Field:
												%s
												""".formatted(body)));
									}
								} else {
									if (oldField.isEmpty()) {
										var newField = StaticJavaParser.parseBodyDeclaration(body);
										sourceClass.addMember(newField);
									} else {
										sourceClass.addOrphanComment(new BlockComment("""
												Missing Field:
												%s
												""".formatted(body)));
									}
								}
							}
							// apply constructors
							try {
								var constructors = modifierContent.getAsJsonArray("constructors");
								for (JsonElement constructor : constructors) {
									var jsonObject = constructor.getAsJsonObject();
									var bodystring = jsonObject.get("body").getAsString();
									if (jsonObject.has("originalBody")) {
										boolean checkFlag = false;
										for (ConstructorDeclaration generatedConstructor : sourceClass.getConstructors()) {
											if (generatedConstructor.getSignature().asString()
													.equals(jsonObject.get("signature").getAsString())) {
												checkFlag = true;
												if (Objects.toString(generatedConstructor.getBody())
														.equals(jsonObject.get("originalBody").getAsString())) {
													var cons = sourceParser.parseBodyDeclaration(bodystring).getResult()
															.orElseThrow().asConstructorDeclaration();
													generatedConstructor.setBody(cons.getBody());
												} else {
													generatedConstructor.setBlockComment("""
															Missing Body:
															
															""" + jsonObject.get("body").getAsString());
													var body = generatedConstructor.getBody();
													if (jsonObject.has("head")) {
														var sta = StaticJavaParser.parseStatement(
																jsonObject.get("head").getAsString());
														body.addStatement(0, sta);
														sta.setLineComment("Head");
													}
													if (jsonObject.has("tail")) {
														var sta = StaticJavaParser.parseStatement(
																jsonObject.get("tail").getAsString());
														body.addStatement(sta);
														sta.setLineComment("Tail");
													}
												}
											}
										}
										if (!checkFlag) {
											sourceClass.addOrphanComment(new JavadocComment(
													"Missing constructor: %s %s".formatted(
															jsonObject.get("signature").getAsString(), bodystring)));
										}
									} else {
										ConstructorDeclaration constructorDeclaration = StaticJavaParser.parseBodyDeclaration(
												bodystring).toConstructorDeclaration().orElseThrow();
										if (sourceClass.getConstructorByParameterTypes(
														constructorDeclaration.getParameters().stream()
																.map(NodeWithType::getTypeAsString).toArray(String[]::new))
												.isEmpty()) {
											sourceClass.addMember(constructorDeclaration);
										} else {
											sourceClass.addOrphanComment(new BlockComment("""
													Missing constructor
													%s
													""".formatted(bodystring)));
										}
									}
								}
							} catch (NoSuchElementException | ParseProblemException e) {
								LOGGER.error(e);
								e.printStackTrace();
							}
							// apply methods
							try {
								var methods = modifierContent.getAsJsonObject("methods");
								methods.entrySet().forEach(b -> {
									var jsonObject = b.getValue().getAsJsonObject();
									if (jsonObject.has("originalBody")) {
										boolean checkFlag = false;
										for (MethodDeclaration generatedMethod : sourceClass.getMethodsByName(
												b.getKey())) {
											if (generatedMethod.getSignature().asString()
													.equals(jsonObject.get("signature").getAsString())) {
												checkFlag = true;
												if (Objects.toString(generatedMethod.getBody().get())
														.equals(jsonObject.get("originalBody").getAsString())) {

													generatedMethod.setBody(StaticJavaParser.parseBlock(
															jsonObject.get("body").getAsString()));
												} else {
													generatedMethod.setBlockComment("""
															Missing Body:
															%s
															""".formatted(jsonObject.get("body").getAsString()));
													var body = generatedMethod.getBody()
															.orElseThrow(() -> new NoSuchElementException("No body"));
													if (jsonObject.has("head")) {
														var sta = StaticJavaParser.parseStatement(
																jsonObject.get("head").getAsString());
														body.addStatement(0, sta);
														sta.setLineComment("Head");
													}
													if (jsonObject.has("tail")) {
														var sta = StaticJavaParser.parseStatement(
																jsonObject.get("tail").getAsString());
														body.addStatement(sta);
														sta.setLineComment("Tail");
													}

												}
											}
										}
										if (!checkFlag) {
											sourceClass.addOrphanComment(new JavadocComment(
													"Missing method:" + jsonObject.get("signature").getAsString()
															+ jsonObject.get("body").getAsString()));
										}
									} else {
										var bodystring = jsonObject.get("body").getAsString();
										MethodDeclaration methodDeclaration = sourceParser.parseMethodDeclaration(
												bodystring).getResult().orElseThrow();
										if (sourceClass.getMethodsBySignature(methodDeclaration.getNameAsString(),
												methodDeclaration.getSignature().getParameterTypes().stream()
														.map(Type::asString).toArray(String[]::new)).isEmpty()) {
											sourceClass.addMember(methodDeclaration);
										} else {
											sourceClass.addOrphanComment(new BlockComment("""
													Missing constructor
													""" + bodystring));
										}
									}
								});
							} catch (NoSuchElementException | ParseProblemException e) {
								LOGGER.error(e);
								e.printStackTrace();
							}
							String code1 = resul.toString();
							var code = new CodeCleanup().reformatTheCodeAndOrganiseImports(workspace, code1);
							FileIO.writeStringToFile(code, generatorFile.getFile());
						}
					}

				} catch (Exception e) {
					LOGGER.info("Error {} in {}", e, generatorFile);
				}
			}
		});
		return modifierApplied.get();
	}

	private static List<GeneratorFile> generateBase(IGenerator generator, JsonArray cache) {
		if (!DevUtilsSection.getInstance().getRecordBase().get()) {
			return new ArrayList<>();
		}
		TemplateGenerator templateGenerator = generator.getTemplateGeneratorFromName("templates");

		return generator.getModBaseGeneratorTemplatesList().stream().map(generatorTemplate -> {
			try {
				String templateName = (String) generatorTemplate.getTemplateDefinition().get("template");
				//only support java
				if (templateName.endsWith("java.ftl")) {
					if (cache != null) {
						if (!cache.contains(new JsonPrimitive(templateName))) {
							return null;
						}
					}
					String code = templateGenerator.generateBaseFromTemplate(templateName,
							generatorTemplate.getDataModel(),
							(String) generatorTemplate.getTemplateDefinition().get("variables"));
					return generatorTemplate.toGeneratorFile(code);
				} else {
					return null;
				}
			} catch (TemplateGeneratorException e) {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}
}
