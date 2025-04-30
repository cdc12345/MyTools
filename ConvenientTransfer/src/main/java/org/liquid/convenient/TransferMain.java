package org.liquid.convenient;

import com.google.gson.*;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.element.types.CustomElement;
import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.java.CodeCleanup;
import net.mcreator.java.ImportFormat;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.workspace.MCreatorLoadedEvent;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.variants.modmaker.ModMaker;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.workspace.elements.IElement;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.ModElementManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liquid.convenient.render.TilesModListRender;
import org.liquid.convenient.utils.JsonUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class TransferMain extends JavaPlugin {

	public static final Logger LOG = LogManager.getLogger("TransferMain");
	private static final Gson GSON = new Gson();
	private static final int PREVIEW_LENGTH = 20;

	private JsonObject descMap = new JsonObject();

	public TransferMain(Plugin plugin) {
		super(plugin);
		initializeMenu();
		initializeComments();
	}

	private void initializeMenu() {
		this.addListener(MCreatorLoadedEvent.class, event -> {
			MCreator mcreator = event.getMCreator();
			if (mcreator instanceof ModMaker) {
				JMenuBar bar = mcreator.getMainMenuBar();

				JMenu transfer = new JMenu("Transfer");

				// Copy operations
				transfer.add(buildShallowCopyMenu(mcreator));
				transfer.add(buildSelectToReplace(mcreator));
				transfer.addSeparator();
				transfer.add(buildDeepCopyMenu(mcreator));
				transfer.add(buildUnpackMenu(mcreator));
				transfer.addSeparator();

				// Comment and language operations
				transfer.add(buildAddCommentMenu(mcreator));
				transfer.add(buildLanguageMenu(mcreator, false));
				transfer.add(buildLanguageMenu(mcreator, true));

				bar.add(transfer);
			}
		});
	}

	private void initializeComments() {
		this.addListener(MCreatorLoadedEvent.class, event -> {
			MCreator mcreator = event.getMCreator();
			Path commentsPath = new File(mcreator.getWorkspaceFolder(), "comments.json").toPath();

			try (Reader reader = Files.newBufferedReader(commentsPath)) {
				this.descMap = GSON.fromJson(reader, JsonObject.class);
			} catch (IOException e) {
				this.descMap = new JsonObject();
			}

			if (mcreator.workspaceTab.getContent() instanceof WorkspacePanel workspacePanel) {
				workspacePanel.list.addListSelectionListener(e -> TilesModListRender.updateRenderer(mcreator, this));
				TilesModListRender.updateRenderer(mcreator, this);
			}
		});
	}

	private JMenuItem buildLanguageMenu(MCreator mcreator, boolean specificLang) {
		String key = specificLang ? "mainbar.menu.loadjsontospecificlang" : "mainbar.menu.fromjsontolangall";
		JMenuItem menuItem = new JMenuItem(L10N.t(key));

		menuItem.addActionListener(e -> {
			String langKey = specificLang ? JOptionPane.showInputDialog("Input Lang") : null;
			String jsonInput = JOptionPane.showInputDialog("Input Json Lang");

			if (jsonInput == null || (specificLang && langKey == null)) {
				return;
			}

			try {
				JsonObject lang = GSON.fromJson(jsonInput, JsonObject.class);
				Map<String, String> targetMap = specificLang ?
						mcreator.getWorkspace().getLanguageMap().get(langKey) :
						mcreator.getWorkspace().getLanguageMap().get("en_us");

				lang.entrySet().forEach(entry -> targetMap.put(entry.getKey(), entry.getValue().getAsString()));

				if (specificLang) {
					LOG.info("Updated language: {}", langKey);
				}
			} catch (JsonSyntaxException ex) {
				showError(mcreator, "Invalid JSON format");
				LOG.error("Failed to parse language JSON", ex);
			}
		});

		return menuItem;
	}

	private JMenuItem buildSelectToReplace(MCreator mcreator) {
		JMenuItem menuItem = new JMenuItem(L10N.t("mainbar.menu.pastereplace"));

		menuItem.addActionListener(e -> {
			if (!(mcreator.workspaceTab.getContent() instanceof WorkspacePanel workspacePanel)) {
				return;
			}

			ModElement element = getSelectedModElement(workspacePanel);
			if (element == null) {
				showError(mcreator, L10N.t("common.tip.notselected"));
				return;
			}

			try {
				String input = getBase64Input();
				if (input == null)
					return;

				JsonArray elements = parseInputToJsonArray(input);
				if (elements == null || elements.isEmpty()) {
					showError(workspacePanel, "Invalid Element");
					return;
				}

				processElementReplacement(mcreator, element, JsonUtils.unmap(elements.get(0).getAsJsonObject()));
			} catch (Exception ex) {
				handleProcessingError(workspacePanel, ex);
			}
		});

		return menuItem;
	}

	private JMenuItem buildUnpackMenu(MCreator mcreator) {
		JMenuItem menuItem = new JMenuItem(L10N.t("mainbar.menu.pastetocreate"));
		menuItem.setToolTipText(L10N.t("mainbar.menu.pastetocreate.tooltip"));

		menuItem.addActionListener(e -> {
			if (!(mcreator.workspaceTab.getContent() instanceof WorkspacePanel workspacePanel)) {
				return;
			}

			try {
				String input = getBase64Input();
				if (input == null)
					return;

				JsonArray elements = parseInputToJsonArray(input);
				if (elements == null) {
					showError(workspacePanel, "Invalid Element");
					return;
				}

				processMultipleElementsCreation(mcreator, elements);
				showPreview(workspacePanel, elements.toString());
			} catch (Exception ex) {
				handleProcessingError(workspacePanel, ex);
			}
		});

		return menuItem;
	}

	private JMenuItem buildShallowCopyMenu(MCreator mcreator) {
		JMenuItem menuItem = new JMenuItem(L10N.t("mainbar.menu.copyselected"));
		menuItem.setToolTipText(L10N.t("mainbar.menu.copyselected.tooltip"));

		menuItem.addActionListener(e -> {
			if (!(mcreator.workspaceTab.getContent() instanceof WorkspacePanel workspacePanel)) {
				return;
			}

			ModElement element = getSelectedModElement(workspacePanel);
			if (element == null) {
				showError(workspacePanel, L10N.t("common.tip.notselected"));
				return;
			}

			try {
				String json = mcreator.getWorkspace().getModElementManager()
						.generatableElementToJSON(element.getGeneratableElement());

				if (json == null || element.getGeneratableElement() instanceof CustomElement) {
					showError(workspacePanel, "Invalid Element");
					return;
				}

				JsonArray result = new JsonArray();
				var preview = GSON.fromJson(json, JsonObject.class);
				result.add(JsonUtils.map(preview));

				showPreview(workspacePanel, element.getName(), preview.toString());
				copyToClipboard(compressToBase64(result.toString()));
				LOG.info("{}:{}", element.getName(), result);
			} catch (Exception ex) {
				handleProcessingError(workspacePanel, ex);
			}
		});

		return menuItem;
	}

	private JMenuItem buildDeepCopyMenu(MCreator mcreator) {
		JMenuItem menuItem = new JMenuItem(L10N.t("mainbar.menu.copyselectedmultiple"));
		menuItem.setToolTipText(L10N.t("mainbar.menu.copyselectedmultiple.tooltip"));

		menuItem.addActionListener(e -> {
			if (!(mcreator.workspaceTab.getContent() instanceof WorkspacePanel workspacePanel)) {
				return;
			}

			List<IElement> elements = workspacePanel.list.getSelectedValuesList();
			if (elements == null || elements.isEmpty()) {
				showError(mcreator, L10N.t("common.tip.notselected"));
				return;
			}

			try {
				JsonArray jsonElements = createJsonForElements(mcreator, elements);
				showPreview(workspacePanel, jsonElements.toString());
				copyToClipboard(compressToBase64(jsonElements.toString()));
				LOG.info(jsonElements.toString());
			} catch (Exception ex) {
				handleProcessingError(workspacePanel, ex);
			}
		});

		return menuItem;
	}

	private JMenuItem buildAddCommentMenu(MCreator mcreator) {
		JMenuItem menuItem = new JMenuItem(L10N.t("mainbar.menu.addcomment"));

		menuItem.addActionListener(e -> {
			if (!(mcreator.workspaceTab.getContent() instanceof WorkspacePanel workspacePanel)) {
				return;
			}

			IElement element = workspacePanel.list.getSelectedValue();
			if (!(element instanceof ModElement modElement)) {
				return;
			}

			String comment = JOptionPane.showInputDialog("Input your comment");
			if (comment == null || comment.trim().isEmpty()) {
				return;
			}

			descMap.add(modElement.getName(), new JsonPrimitive(comment));
			workspacePanel.list.setCellRenderer(new TilesModListRender(this));

			try {
				Files.writeString(new File(mcreator.getWorkspaceFolder(), "comments.json").toPath(), descMap.toString(),
						StandardCharsets.UTF_8, StandardOpenOption.WRITE);
			} catch (IOException ex) {
				LOG.error("Failed to save comments", ex);
				throw new RuntimeException(ex);
			}
		});

		return menuItem;
	}

	// Helper methods
	private ModElement getSelectedModElement(WorkspacePanel panel) {
		Object selected = panel.list.getSelectedValue();
		return selected instanceof ModElement ? (ModElement) selected : null;
	}

	private String getBase64Input() {
		return JOptionPane.showInputDialog("Input your element base64");
	}

	private JsonArray parseInputToJsonArray(String input) throws IOException {
		byte[] raw = Base64.getDecoder().decode(input.getBytes(StandardCharsets.UTF_8));
		String json = JsonUtils.uncompress(raw);

		try {
			return GSON.fromJson(json, JsonArray.class);
		} catch (JsonSyntaxException e) {
			JsonArray array = new JsonArray();
			array.add(GSON.fromJson(json, JsonObject.class));
			return array;
		}
	}

	private void processElementReplacement(MCreator mcreator, ModElement element, JsonObject object) {
		var manager = element.getModElementManager();
		GeneratableElement generatableElement = createGeneratableElement(manager, element, object);

		if (generatableElement == null) {
			showError(mcreator, "Invalid Element");
			LOG.warn("generatableElement == null");
			return;
		}

		if (object.has("code")) {
			processCodeElement(mcreator, element, generatableElement, object.get("code").getAsString());
		}

		generatableElement.setModElement(element);
		manager.storeModElement(generatableElement);
	}

	private GeneratableElement createGeneratableElement(ModElementManager manager, ModElement element,
			JsonObject object) {
		if (JsonUtils.isRegularPack(object)) {
			JsonObject content = JsonUtils.unmap(object.get("content").getAsJsonObject());
			return manager.fromJSONtoGeneratableElement(content.toString(), element);
		}
		return manager.fromJSONtoGeneratableElement(object.toString(), element);
	}

	private void processCodeElement(MCreator mcreator, ModElement element, GeneratableElement generatableElement,
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

	private void processMultipleElementsCreation(MCreator mcreator, JsonArray jsonElements) {
		var manager = mcreator.getModElementManager();

		for (JsonElement jsonElement : jsonElements) {
			JsonObject object = JsonUtils.unmap(jsonElement.getAsJsonObject());
			JsonObject elementJson = JsonUtils.unmap(
					GSON.fromJson(object.has("content")?object.get("content").getAsString():object.get(JsonUtils.CONTENT).getAsString(), JsonObject.class));

			if (!JsonUtils.isRegularPack(object)) {
				LOG.warn("Invalid Element: {}", object);
				continue;
			}

			String name = object.has("name")?object.get("name").getAsString():object.get(JsonUtils.NAME).getAsString();
			String type = object.has("type") ?
					object.get("type").getAsString() :
					elementJson.get("_type").getAsString();

			var type1 = ModElementTypeLoader.getModElementType(type);
			ModElement modElement = new ModElement(mcreator.getWorkspace(), name,
					type1);

			GeneratableElement generatableElement = manager.fromJSONtoGeneratableElement(elementJson.toString(),
					modElement);

			if (object.has("code") || object.has(JsonUtils.CODE)) {
				generatableElement =
						generatableElement != null ? generatableElement : modElement.getGeneratableElement();
				processCodeElementForCreation(mcreator, modElement, generatableElement, object.has("code")?
						object.get("code").getAsString():object.get(JsonUtils.CODE).getAsString());
			}

			if (generatableElement == null) {
				showError(mcreator, "Invalid Element");
				return;
			}

			mcreator.getWorkspace().addModElement(modElement);
			generatableElement.setModElement(modElement);
			manager.storeModElement(generatableElement);

			if (mcreator instanceof ModMaker modMaker){
				modMaker.getWorkspacePanel().editCurrentlySelectedModElement(modElement,modMaker.getWorkspacePanel().list,0,0);
			}
		}

		mcreator.reloadWorkspaceTabContents();
//		RegenerateCodeAction.regenerateCode(mcreator, false, false);


	}

	private void processCodeElementForCreation(MCreator mcreator, ModElement modElement,
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

	private JsonArray createJsonForElements(MCreator mcreator, List<IElement> elements) throws IOException {
		JsonArray jsonElements = new JsonArray();

		for (IElement element : elements) {
			if (element instanceof ModElement modElement) {
				jsonElements.add(createElementJson(mcreator, modElement));
			}
		}

		return jsonElements;
	}

	private JsonObject createElementJson(MCreator mcreator, ModElement element) throws IOException {
		JsonObject jsonObject = new JsonObject();
		JsonObject elementContent = GSON.fromJson(mcreator.getWorkspace().getModElementManager()
				.generatableElementToJSON(element.getGeneratableElement()), JsonObject.class);

		jsonObject.add(JsonUtils.NAME, new JsonPrimitive(element.getName()));

		String type = element.getType().getRegistryName();
		if (!elementContent.has("_type")) {
			jsonObject.add("type", new JsonPrimitive(type));
		}

		if (List.of("code", "mixin").contains(type)) {
			List<File> modElementFiles = mcreator.getGenerator()
					.getModElementGeneratorTemplatesList(element.getGeneratableElement()).stream()
					.map(GeneratorTemplate::getFile).toList();

			String code = Files.readString(modElementFiles.getFirst().toPath());
			jsonObject.add(JsonUtils.CODE, new JsonPrimitive(ImportFormat.removeImports(code, "")));
		}

		jsonObject.add(JsonUtils.CONTENT, new JsonPrimitive(JsonUtils.map(elementContent).toString()));
		return JsonUtils.map(jsonObject);
	}

	private String compressToBase64(String data) throws IOException {
		return Base64.getEncoder().encodeToString(JsonUtils.compress(data, JsonUtils.GZIP_ENCODE_UTF_8));
	}

	private void copyToClipboard(String data) {
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(data), null);
	}

	private void showPreview(Component parent, String... messages) {
		StringBuilder preview = new StringBuilder();
		for (String message : messages) {
			if (!preview.isEmpty()) {
				preview.append(": ");
			}
			preview.append(message.length() > PREVIEW_LENGTH ? message.substring(0, PREVIEW_LENGTH) + "...." : message);
		}
		JOptionPane.showMessageDialog(parent, preview.toString());
	}

	private void showError(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private void handleProcessingError(Component parent, Exception ex) {
		showError(parent, "Processing error: " + ex.getMessage());
		LOG.error("Processing error", ex);
	}

	public String getOrDefault(String key, String defaultValue) {
		return descMap.has(key) ? descMap.get(key).getAsString() : defaultValue;
	}
}