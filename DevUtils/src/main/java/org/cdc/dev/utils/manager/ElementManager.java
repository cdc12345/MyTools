package org.cdc.dev.utils.manager;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.io.FileIO;
import net.mcreator.java.CodeCleanup;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ReferencesFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.dev.utils.FileUtils;
import org.cdc.interfaces.IMCreator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ElementManager {
	private static final Logger LOGGER = LogManager.getLogger(ElementManager.class);

	public static void openElementDefinition(IMCreator mCreator, ModElement modElement) {
		File genFile = new File(mCreator.getFolderManager().getModElementsDir(), modElement.getName() + ".mod.json");
		mCreator.getTabs().addTab(new MCreatorTabs.Tab(new CodeEditorView(mCreator.getOrigin(), genFile)));
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

	public static void createModifiersThroughElement(GeneratableElement generatable) throws TemplateGeneratorException {
		File parentModifier = FileUtils.getModifiersPath(generatable.getModElement().getWorkspace());
		var gen = generatable.getModElement().getGenerator().generateElement(generatable, false, false);
		var parserConfiguration = new ParserConfiguration();
		parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
		var sourceParser = new JavaParser(parserConfiguration);
		var templateParser = new JavaParser(parserConfiguration);
		gen.forEach(a -> {
			if (Files.getFileExtension(a.getFile().getName()).equals("java")) {
				try {
					var className = Files.getNameWithoutExtension(a.getFile().getName());
					var imports = sourceParser.parse(a.getFile()).getResult().orElseThrow();
					var sourceClass = imports.getClassByName(className).orElseThrow();
					var modifier = new File(parentModifier, sourceClass.getFullyQualifiedName().get());
					var templateClass = templateParser.parse(a.contents()).getResult().orElseThrow()
							.getClassByName(className).orElseThrow();
					var modifierContent = new JsonObject();
					var constructors = new JsonArray();
					AtomicBoolean modifierFlag = new AtomicBoolean(false);
					sourceClass.getConstructors().forEach(constructorDeclaration -> {
						var targetConstructor = templateClass.getConstructorByParameterTypes(
								constructorDeclaration.getParameters().stream()
										.map(typeParameter -> typeParameter.getType().asString()).toArray(String[]::new));
						if (targetConstructor.isPresent()) {
							if (!targetConstructor.get().getBody().equals(constructorDeclaration.getBody())) {
								var constructor = new JsonObject();
								modifierFlag.set(true);
								constructor.addProperty("signature", constructorDeclaration.getSignature().asString());
								constructor.addProperty("originalBody",
										Objects.toString(targetConstructor.get().getBody()));
								constructor.addProperty("body", Objects.toString(constructorDeclaration));
								constructors.add(constructor);
							}
						} else {
							var constructor = new JsonObject();
							modifierFlag.set(true);
							constructor.addProperty("signature", constructorDeclaration.getSignature().asString());
							constructor.addProperty("body", Objects.toString(constructorDeclaration.toString()));
							constructors.add(constructor);
						}
					});
					var methods = new JsonObject();
					sourceClass.getMethods().forEach(b -> {
						AtomicReference<MethodDeclaration> result = new AtomicReference<>();
						if (templateClass.getMethodsByName(b.getNameAsString()).stream()
								.noneMatch(methodDeclaration -> {
									if (methodDeclaration.getSignature().equals(b.getSignature())) {
										result.set(methodDeclaration);
										return true;
									}
									return false;
								})) {
							modifierFlag.set(true);
							var method = new JsonObject();
							method.addProperty("signature", b.getSignature().asString());
							method.addProperty("body", b.toString());
							methods.add(b.getNameAsString(), method);
						} else {
							var method = new JsonObject();
							var re = result.get();
							if (!re.getBody().equals(b.getBody())) {
								modifierFlag.set(true);
								method.addProperty("signature", b.getSignature().asString());
								method.addProperty("originalBody", Objects.toString(re.getBody().get()));
								method.addProperty("body", Objects.toString(b.getBody().get()));
								methods.add(b.getNameAsString(), method);
							}
						}
					});
					if (modifierFlag.get()) {
						var importsJsonArray = new JsonArray();
						for (ImportDeclaration anImport : imports.getImports()) {
							importsJsonArray.add(anImport.getNameAsString());
						}
						modifierContent.add("imports", importsJsonArray);
						LOGGER.info("Saved to {}", modifier.getPath());
						modifierContent.add("methods", methods);
						modifierContent.add("constructors",constructors);
						java.nio.file.Files.copy(
								new ByteArrayInputStream(modifierContent.toString().getBytes(StandardCharsets.UTF_8)),
								modifier.toPath(), StandardCopyOption.REPLACE_EXISTING);

					} else {
						modifier.delete();
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public static boolean applyPatchesToElement(GeneratableElement generatable) throws TemplateGeneratorException {
		File parentModifier = FileUtils.getModifiersPath(generatable.getModElement().getWorkspace());
		var gen = generatable.getModElement().getGenerator().generateElement(generatable, false, false);
		var parserConfiguration = new ParserConfiguration();
		parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
		StaticJavaParser.setConfiguration(parserConfiguration);
		var sourceParser = new JavaParser(parserConfiguration);
		AtomicBoolean modifierApplied = new AtomicBoolean(false);
		gen.forEach(a -> {
			if (Files.getFileExtension(a.getFile().getName()).equals("java")) {
				try {
					var className = Files.getNameWithoutExtension(a.getFile().getName());
					var resul = sourceParser.parse(a.getFile()).getResult().orElseThrow();
					var sourceClass = resul.getClassByName(className).orElseThrow();
					var modifier = new File(parentModifier, sourceClass.getFullyQualifiedName().get());
					if (modifier.exists()) {
						modifierApplied.set(true);
						var modifierContent = new Gson().fromJson(java.nio.file.Files.readString(modifier.toPath()),
								JsonObject.class);
						var imports = modifierContent.getAsJsonArray("imports");
						imports.forEach(anImport -> {
							resul.addImport(anImport.getAsString());
						});
						try {
							var constructors = modifierContent.getAsJsonArray("constructors");
							for (JsonElement constructor : constructors) {
								var jsonObject = constructor.getAsJsonObject();
								if (jsonObject.has("originalBody")) {
									boolean checkFlag = false;
									for (ConstructorDeclaration generatedConstructor : sourceClass.getConstructors()) {
										if (generatedConstructor.getSignature().asString()
												.equals(jsonObject.get("signature").getAsString())) {
											checkFlag = true;
											if (Objects.toString(generatedConstructor.getBody())
													.equals(jsonObject.get("originalBody").getAsString())) {
												var cons = sourceParser.parseBodyDeclaration(
																jsonObject.get("body").getAsString()).getResult().orElseThrow()
														.asConstructorDeclaration();
												generatedConstructor.setBody(cons.getBody());
											} else {
												generatedConstructor.setBlockComment("""
														Missing Body:
														
														""" + jsonObject.get("body").getAsString());
											}
										}
									}
									if (!checkFlag) {
										sourceClass.addOrphanComment(new JavadocComment(
												"Missing constructor:" + jsonObject.get("signature").getAsString()
														+ jsonObject.get("body").getAsString()));
									}
								} else {
									ConstructorDeclaration constructorDeclaration = StaticJavaParser.parseBodyDeclaration(
													jsonObject.get("body").getAsString()).toConstructorDeclaration()
											.orElseThrow();
									sourceClass.addMember(constructorDeclaration);
								}
							}
						} catch (NoSuchElementException | ParseProblemException e) {
							LOGGER.error(e);
						}
						try {
							var methods = modifierContent.getAsJsonObject("methods");
							methods.entrySet().forEach(b -> {
								var jsonObject = b.getValue().getAsJsonObject();
								if (jsonObject.has("originalBody")) {
									boolean checkFlag = false;
									for (MethodDeclaration generatedMethod : sourceClass.getMethodsByName(b.getKey())) {
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
														
														""" + jsonObject.get("body").getAsString());
											}
										}
									}
									if (!checkFlag) {
										sourceClass.addOrphanComment(new JavadocComment(
												"Missing method:" + jsonObject.get("signature").getAsString()
														+ jsonObject.get("body").getAsString()));
									}
								} else {
									MethodDeclaration methodDeclaration = sourceParser.parseMethodDeclaration(
											jsonObject.get("body").getAsString()).getResult().orElseThrow();
									sourceClass.addMember(methodDeclaration);
								}
							});
						} catch (NoSuchElementException | ParseProblemException e){
							LOGGER.error(e);
						}
						String code1 = resul.toString();
						var code = new CodeCleanup().reformatTheCodeAndOrganiseImports(
								generatable.getModElement().getWorkspace(), code1);
						FileIO.writeStringToFile(code, a.getFile());
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		return modifierApplied.get();
	}
}
