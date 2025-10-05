package org.cdc.dev.utils.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ReferencesFinder;
import org.cdc.interfaces.IMCreator;

import java.io.File;
import java.util.List;

public class ElementManager {
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

//		var defi = new Gson().fromJson(modElement.getWorkspace().getModElementManager()
//				.generatableElementToJSON(modElement.getGeneratableElement()), JsonObject.class);
//		defi.remove("_type");
//		element.add("definition", defi);
		var required = new JsonArray();
		for (ModElement searched : ReferencesFinder.searchModElementUsages(modElement.getWorkspace(),
				modElement)) {
			required.add(searched.getName());
		}
		element.add("required",required);

		try {
			element.addProperty("compile",modElement.doesCompile());
		} catch (NoSuchMethodError ignored){

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
			return fileList.stream().map(e -> new File(modElement.getWorkspaceFolder(), e.toString().replace("/", File.separator)))
					.filter(modElement.getFolderManager()::isFileInWorkspace).toList();
		return List.of();
	}
}
