package org.cdc.dev.utils.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mcreator.generator.GeneratorUtils;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.references.ReferencesFinder;
import net.mcreator.workspace.resources.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.dev.sections.DevUtilsSection;
import org.cdc.dev.utils.FileUtils;
import org.cdc.interfaces.IMCreator;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.cdc.dev.utils.manager.ElementManager.exportElement;

public class WorkspaceManager {

	private static final Logger LOGGER = LogManager.getLogger("WorkspaceManager");

	public static void removeSrc(IMCreator mcreator) {
		try {
			int option = JOptionPane.showConfirmDialog(mcreator.getOrigin(), "Are you sure?", "Confirm",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				org.apache.commons.io.FileUtils.forceDelete(
						GeneratorUtils.getSourceRoot(mcreator.getWorkspace(), mcreator.getGeneratorConfiguration()));
			}
		} catch (IOException e) {
			LOGGER.info(e);
		}
	}

	public static void removeModifiersFolder(IMCreator mcreator) {
		try {
			int option = JOptionPane.showConfirmDialog(mcreator.getOrigin(), "Are you sure?", "Confirm",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				org.apache.commons.io.FileUtils.forceDelete(
						new File(mcreator.getWorkspace().getWorkspaceFolder(), "modifiers"));
			}
		} catch (IOException e) {
			LOGGER.info(e);
		}
	}

	public static void removeDataPack(IMCreator mcreator) {
		try {
			org.apache.commons.io.FileUtils.forceDelete(
					GeneratorUtils.getModDataRoot(mcreator.getWorkspace(), mcreator.getGeneratorConfiguration()));
		} catch (Exception e) {
			LOGGER.info(e);
		}
	}

	public static void removeWholeDataPack(IMCreator mcreator) {
		try {
			int option = JOptionPane.showConfirmDialog(mcreator.getOrigin(), "Are you sure?", "Confirm",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				org.apache.commons.io.FileUtils.forceDelete(
						GeneratorUtils.getModDataRoot(mcreator.getWorkspace(), mcreator.getGeneratorConfiguration())
								.getParentFile());
			}
		} catch (Exception e) {
			LOGGER.info(e);
		}
	}

	public static void removeAllJsonFileFromAssets(IMCreator mcreator) {
		removeJsonFiles(GeneratorUtils.getModAssetsRoot(mcreator.getWorkspace(), mcreator.getGeneratorConfiguration()));
	}

	private static void removeJsonFiles(File file) {
		for (File listFile : Objects.requireNonNull(file.listFiles())) {
			if (listFile.isDirectory()) {
				removeJsonFiles(listFile);
			} else if (listFile.getName().endsWith(".json")) {
				listFile.delete();
			}
		}
	}

	public static void removeAssets(IMCreator mcreator) {
		try {
			int option = JOptionPane.showConfirmDialog(mcreator.getOrigin(), "Are you sure?", "Confirm",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				org.apache.commons.io.FileUtils.forceDelete(
						GeneratorUtils.getModAssetsRoot(mcreator.getWorkspace(), mcreator.getGeneratorConfiguration()));
			}
		} catch (Exception e) {
			LOGGER.info(e);
		}
	}

	public static void removeWholeAssets(IMCreator mcreator) {
		try {
			int option = JOptionPane.showConfirmDialog(mcreator.getOrigin(), "Are you sure?", "Confirm",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				org.apache.commons.io.FileUtils.forceDelete(
						GeneratorUtils.getModAssetsRoot(mcreator.getWorkspace(), mcreator.getGeneratorConfiguration())
								.getParentFile());
			}
		} catch (Exception e) {
			LOGGER.info(e);
		}
	}

	public static void openWorkspaceDefinition(IMCreator mcreator, File workspaceFile) {
		mcreator.getTabs().addTab(new MCreatorTabs.Tab(new CodeEditorView(mcreator.getOrigin(), workspaceFile)));
	}

	public static JsonObject exportSnapshot(IMCreator mCreator) throws IOException {
		Workspace workspace = mCreator.getWorkspace();
		JsonObject report = new JsonObject();
		report.addProperty("name", workspace.getWorkspaceSettings().getModName());
		report.addProperty("generatorName", workspace.getGeneratorConfiguration().getGeneratorName());
		JsonArray depends = new JsonArray();
		workspace.getWorkspaceSettings().getMCreatorDependencies().forEach(depends::add);
		report.add("dependencies", depends);

		var plugins = new JsonArray();
		for (Plugin plugin : PluginLoader.INSTANCE.getPlugins()) {
			plugins.add(exportPlugin(plugin));
		}
		report.add("plugins", plugins);

		var elements = new JsonArray();
		for (ModElement modElement : workspace.getModElements()) {
			elements.add(exportElement(modElement));
		}
		report.add("elements", elements);
		var libraries = new JsonArray();
		if (workspace.getGenerator().getProjectJarManager() != null) {
			List<LibraryInfo> libraryInfos = workspace.getGenerator().getProjectJarManager().getClassFileSources();
			for (LibraryInfo libraryInfo : libraryInfos) {
				File libraryFile = new File(libraryInfo.getLocationAsString());
				if (libraryFile.isFile()) {
					var lib = new JsonObject();
					lib.addProperty("path", libraryFile.getPath());
					lib.addProperty("exist", libraryFile.exists());
					libraries.add(lib);
				}
			}
		}
		report.add("externalLibraries", libraries);
		var models = new JsonArray();
		for (Model model : Model.getModels(workspace)) {
			var modelInfo = new JsonObject();
			modelInfo.addProperty("path", model.getFile().getPath());
			if (model.getType() == Model.Type.JAVA) {
				modelInfo.addProperty("mapping", FileUtils.getJavaModelMappings(model.getFile().toPath()));
			}
			if (model.getFile().getPath().endsWith(".json")) {
				JsonObject jsonObject = new Gson().fromJson(Files.newBufferedReader(model.getFile().toPath()),
						JsonObject.class);
				if (jsonObject.has("format_version"))
					modelInfo.add("format_version", jsonObject.get("format_version"));
				if (jsonObject.has("parent"))
					modelInfo.add("parent", jsonObject.get("parent"));
			}
			var required = new JsonArray();
			for (ModElement usage : ReferencesFinder.searchModelUsages(workspace, model)) {
				required.add(usage.getName());
			}
			modelInfo.add("required", required);
			models.add(modelInfo);
		}
		report.add("models", models);

		var variables = new JsonArray();
		for (VariableElement element : workspace.getVariableElements()) {
			var varInfo = new JsonObject();
			varInfo.addProperty("name", element.getName());
			varInfo.addProperty("type", element.getTypeString());
			varInfo.addProperty("scope", element.getScope().toString());
			var required = new JsonArray();
			for (ModElement usage : ReferencesFinder.searchGlobalVariableUsages(workspace, element.getName())) {
				required.add(usage.getName());
			}
			varInfo.add("required", required);
			variables.add(varInfo);
		}
		report.add("variables", variables);

		var sounds = new JsonArray();
		for (SoundElement element : workspace.getSoundElements()) {
			var soundInfo = new JsonObject();
			soundInfo.addProperty("javaName", element.getJavaName());
			soundInfo.addProperty("category", element.getCategory());
			var required = new JsonArray();
			for (ModElement usage : ReferencesFinder.searchSoundUsages(workspace, element)) {
				required.add(usage.getName());
			}
			soundInfo.add("required", required);
			sounds.add(soundInfo);
		}
		report.add("sounds", sounds);

		return report;
	}

	private static JsonObject exportPlugin(Plugin plugin) throws IOException {
		JsonObject pluginInfo = new JsonObject();
		pluginInfo.addProperty("id", plugin.getID());
		pluginInfo.addProperty("path", plugin.getFile().getPath());
		//		pluginInfo.addProperty("builtIn", plugin.isBuiltin());
		pluginInfo.addProperty("version", plugin.getPluginVersion());
		pluginInfo.addProperty("weight", plugin.getWeight());
		pluginInfo.addProperty("sha-1", FileUtils.getFileSha1(plugin.getFile()));

		if (!plugin.getFile().getName().startsWith("mcreator-") && plugin.getFile().isFile()) {
			var languageSupport = new JsonArray();
			var sensitives = new JsonArray();
			var common = new JsonArray();
			try (ZipFile zipFile1 = new ZipFile(plugin.getFile())) {
				var iterator = zipFile1.entries();
				while (iterator.hasMoreElements()) {
					var entry = iterator.nextElement();
					if (entry.isDirectory() || entry.getName().endsWith("plugin.json") || entry.getName()
							.startsWith("META-INF") || entry.getName().endsWith("LICENSE")) {
						continue;
					}
					var name = entry.getName();
					if (name.startsWith("lang/")) {
						languageSupport.add(name);
						continue;
					}
					if (DevUtilsSection.getInstance().getExportPluginsSensitives().get()) {
						var set = findEntryInAllPlugins(name);
						if (set.size() > 1) {
							JsonObject action = new JsonObject();
							action.addProperty("action", "overwrite " + entry.getName());
							action.addProperty("relationPlugins",
									set.stream().map(ZipEntry::getComment).collect(Collectors.joining(", ")));
							if (name.contains("/mappings/") || name.startsWith("datalists/")) {
								common.add(action);
							} else {
								sensitives.add(action);
							}
						}
					}
				}
			} catch (IOException ignored) {

			}
			pluginInfo.add("sensitives", sensitives);
			pluginInfo.add("common", common);
			pluginInfo.add("languageSupport", languageSupport);
		}
		return pluginInfo;
	}

	private static final HashMap<String, Set<ZipEntry>> cache = new HashMap<>();

	private static Set<ZipEntry> findEntryInAllPlugins(String name) {
		if (cache.containsKey(name)) {
			return cache.get(name);
		}
		LOGGER.info(name);
		var set = new HashSet<ZipEntry>();
		PluginLoader.INSTANCE.getPlugins().forEach(a -> {
			ZipFile zipFile;
			try {
				zipFile = new ZipFile(a.getFile());
				var entry = zipFile.getEntry(name);
				if (entry != null) {
					set.add(entry);
					entry.setComment(zipFile.getName());
				}
				zipFile.close();
			} catch (IOException ignored) {

			}
		});
		cache.put(name, set);
		return set;
	}

	public static void syncLocalLanguageFiles(Workspace workspace, String condition) {
		String configurationFile = (String) workspace.getGeneratorConfiguration().getLanguageFileSpecification()
				.get("langfile_name");
		for (Map.Entry<String, LinkedHashMap<String, String>> entry : workspace.getLanguageMap()
				.entrySet()) {
			var fileName = FileUtils.getLanguageFile(workspace, entry.getKey(), configurationFile);
			if (condition == null || condition.contains(fileName)) {
				if (fileName.endsWith(".json")) {
					try {
						LOGGER.info(fileName);
						JsonObject jsonObject = new Gson().fromJson(Files.readString(
										Path.of(workspace.getGenerator().getLangFilesRoot().getPath(), fileName)),
								JsonObject.class);
						jsonObject.entrySet().forEach(entry1 -> {
							entry.getValue().put(entry1.getKey(), entry1.getValue().getAsString());
						});
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
}
