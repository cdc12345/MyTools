package org.cdc.dev.utils.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mcreator.generator.GeneratorUtils;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ReferencesFinder;
import net.mcreator.workspace.resources.Model;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.dev.utils.ModelUtils;
import org.cdc.interfaces.IMCreator;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import static org.cdc.dev.utils.manager.ElementManager.exportElement;

public class WorkspaceManager {

	private static final Logger LOGGER = LogManager.getLogger("WorkspaceManager");

	public static void removeSrc(IMCreator mcreator) {
		try {
			int option = JOptionPane.showConfirmDialog(mcreator.getOrigin(), "Are you sure?", "Confirm",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				FileUtils.forceDelete(
						GeneratorUtils.getSourceRoot(mcreator.getWorkspace(), mcreator.getGeneratorConfiguration()));
			}
		} catch (IOException e) {
			LOGGER.info(e);
		}
	}

	public static void removeDataPack(IMCreator mcreator) {
		try {
			FileUtils.forceDelete(
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
				FileUtils.forceDelete(
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
				FileUtils.forceDelete(
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
				FileUtils.forceDelete(
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

	public static JsonObject exportSnapshot(Workspace workspace) throws IOException {
		JsonObject report = new JsonObject();
		report.addProperty("name",workspace.getWorkspaceSettings().getModName());
		report.addProperty("generatorName", workspace.getGeneratorConfiguration().getGeneratorName());
		JsonArray depends = new JsonArray();
		workspace.getWorkspaceSettings().getMCreatorDependencies().forEach(depends::add);
		report.add("dependencies",depends);
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
				modelInfo.addProperty("mapping", ModelUtils.getJavaModelMappings(model.getFile().toPath()));
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
		return report;
	}
}
