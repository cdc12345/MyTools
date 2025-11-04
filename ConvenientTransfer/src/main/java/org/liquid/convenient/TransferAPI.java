package org.liquid.convenient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.java.CodeCleanup;
import net.mcreator.plugin.MCREvent;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.impl.workspace.RegenerateCodeAction;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.ModElementManager;
import org.liquid.convenient.events.PasteElementEvent;
import org.liquid.convenient.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.liquid.convenient.TransferMain.showError;

public class TransferAPI {

	private static final Logger LOG = LoggerFactory.getLogger(TransferAPI.class);

	public static void processMultipleElementsCreation(MCreator mcreator, JsonArray jsonElements, boolean regenerate) {
		var manager = mcreator.getModElementManager();

		for (JsonElement jsonElement : jsonElements) {
			JsonObject object = JsonUtils.unmap(jsonElement.getAsJsonObject());
			MCREvent.event(new PasteElementEvent(mcreator, object));

			JsonObject elementJson = JsonUtils.unmap(new Gson().fromJson(
					object.has("content") ? object.get("content").getAsString() : JsonUtils.getContent(object),
					JsonObject.class));

			if (!JsonUtils.isDeepCopyData(object)) {
				LOG.warn("{}: {}", L10N.t("dialog.error.invalidelement"), object);
				continue;
			}

			// Compatible with old version
			String name = object.has("name") ?
					object.get("name").getAsString() :
					object.get(JsonUtils.NAME).getAsString();
			String type = object.has("type") ?
					object.get("type").getAsString() :
					elementJson.get("_type").getAsString();

			var type1 = ModElementTypeLoader.getModElementType(type);
			ModElement modElement = new ModElement(mcreator.getWorkspace(), name, type1);

			GeneratableElement generatableElement = manager.fromJSONtoGeneratableElement(elementJson.toString(),
					modElement);

			if (object.has("code") || object.has(JsonUtils.CODE)) {
				generatableElement =
						generatableElement != null ? generatableElement : modElement.getGeneratableElement();
				processCodeElementForCreation(mcreator, modElement, generatableElement, object.has("code") ?
						object.get("code").getAsString() :
						object.get(JsonUtils.CODE).getAsString());
			}

			if (generatableElement == null) {
				showError(mcreator, L10N.t("dialog.error.invalidelement"));
				return;
			}

			if (mcreator.getContentPane() instanceof WorkspacePanel workspacePanel) {
				modElement.setParentFolder(workspacePanel.currentFolder);
			}

			if (mcreator.getWorkspace().getModElements().stream()
					.anyMatch(modElement1 -> modElement1.getName().equals(name))) {
				showError(mcreator, name + " existed, ignored.");
			} else {
				mcreator.getWorkspace().addModElement(modElement);
				generatableElement.setModElement(modElement);
				manager.storeModElement(generatableElement);

				if (true) {
					mcreator.mv
							.editCurrentlySelectedModElement(modElement, mcreator.mv.list, 0, 0);
				}
			}

		}

		mcreator.mv.updateMods();
		if (regenerate)
			RegenerateCodeAction.regenerateCode(mcreator, false, false);
	}

	static void processCodeElementForCreation(MCreator mcreator, ModElement modElement,
			GeneratableElement generatableElement, String code) {
		modElement.setCodeLock(true);
		List<File> modElementFiles = mcreator.getGenerator().getModElementGeneratorTemplatesList(generatableElement)
				.stream().map(GeneratorTemplate::getFile).toList();

		CodeCleanup codeCleanup = new CodeCleanup();
		code = codeCleanup.reformatTheCodeAndOrganiseImports(mcreator.getWorkspace(), code);
		File modElementFile = modElementFiles.getFirst();

		try {
			Files.createDirectories(modElementFile.getParentFile().toPath());
			Files.writeString(modElementFile.toPath(), code, StandardCharsets.UTF_8);
		} catch (IOException ex) {
			LOG.error("Failed to create code file", ex);
		}
	}

	static GeneratableElement createGeneratableElement(ModElementManager manager, ModElement element,
			JsonObject object) {
		if (JsonUtils.isDeepCopyData(object)) {
			JsonObject content = JsonUtils.unmap(object.get("content").getAsJsonObject());
			return manager.fromJSONtoGeneratableElement(content.toString(), element);
		}
		return manager.fromJSONtoGeneratableElement(object.toString(), element);
	}

	static void processCodeElement(MCreator mcreator, ModElement element, GeneratableElement generatableElement,
			String code) {
		element.setCodeLock(true);
		List<File> modElementFiles = mcreator.getGenerator().getModElementGeneratorTemplatesList(generatableElement)
				.stream().map(GeneratorTemplate::getFile).toList();

		CodeCleanup codeCleanup = new CodeCleanup();
		code = codeCleanup.reformatTheCodeAndOrganiseImports(mcreator.getWorkspace(), code);

		try {
			Files.writeString(modElementFiles.getFirst().toPath(), code, StandardCharsets.UTF_8,
					StandardOpenOption.WRITE);
		} catch (IOException ex) {
			LOG.error("Failed to write code file", ex);
		}
	}
}
