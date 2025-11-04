package org.liquid.convenient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.types.CustomElement;
import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.java.ImportFormat;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.workspace.MCreatorLoadedEvent;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.IElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liquid.convenient.utils.JsonUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.liquid.convenient.TransferAPI.*;
import static org.liquid.convenient.utils.JsonUtils.GSON;

public class TransferMain extends JavaPlugin {

	public static final Logger LOG = LogManager.getLogger("TransferMain");

	private static final int PREVIEW_LENGTH = 20;

	private JMenu transfer;

	public TransferMain(Plugin plugin) {
		super(plugin);
		initializeMenu();
	}

	private void initializeMenu() {
		this.addListener(MCreatorLoadedEvent.class, event -> {
			MCreator mcreator = event.getMCreator();

			//keep same code style with 2025.3+
			if (true) {
				JMenuBar bar = mcreator.getMainMenuBar();

				transfer = new JMenu(L10N.t("common.menubar.transfer"));

				// Copy operations
				transfer.add(buildShallowCopyMenu(mcreator));
				transfer.add(buildSelectToReplace(mcreator));
				transfer.addSeparator();
				transfer.add(buildDeepCopyMenu(mcreator));
				transfer.add(buildUnpackMenu(mcreator));
				transfer.addSeparator();

				// Comment and language operations
				transfer.add(buildLanguageMenu(mcreator, false));
				transfer.add(buildLanguageMenu(mcreator, true));

				mcreator.getMCreatorTabs().addTabShownListener(tab -> {
					var workspaceTab = tab == mcreator.workspaceTab;
					transfer.setVisible(workspaceTab);
				});

				bar.add(transfer);
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
			if (mcreator.getMCreatorTabs().getCurrentTab() == mcreator.workspaceTab) {
				var workspacePanel = mcreator.mv;
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
						showError(workspacePanel, L10N.t("dialog.error.invalidelement"));
						return;
					}

					processElementReplacement(mcreator, element, JsonUtils.unmap(elements.get(0).getAsJsonObject()));
				} catch (Exception ex) {
					handleProcessingError(workspacePanel, ex);
				}
			} else {
				showError(mcreator.workspaceTab.getContent(), L10N.t("dialog.error.workspacetab"));
			}
		});

		return menuItem;
	}

	private JMenuItem buildUnpackMenu(MCreator mcreator) {
		JMenuItem menuItem = new JMenuItem(L10N.t("mainbar.menu.pastetocreate"));
		menuItem.setToolTipText(L10N.t("mainbar.menu.pastetocreate.tooltip"));

		menuItem.addActionListener(e -> {
			if (mcreator.getMCreatorTabs().getCurrentTab() == mcreator.workspaceTab) {
				var workspacePanel = mcreator.mv;
				try {
					String input = getBase64Input();
					if (input == null)
						return;

					JsonArray elements = parseInputToJsonArray(input);
					if (elements == null) {
						showError(workspacePanel, L10N.t("dialog.error.invalidelement"));
						return;
					}

					processMultipleElementsCreation(mcreator, elements, false);
					showPreview(workspacePanel, elements.toString());
				} catch (Exception ex) {
					handleProcessingError(workspacePanel, ex);
				}
			} else {
				showError(mcreator.workspaceTab.getContent(), L10N.t("dialog.error.workspacetab"));
			}
		});

		return menuItem;
	}

	private JMenuItem buildShallowCopyMenu(MCreator mcreator) {
		JMenuItem menuItem = new JMenuItem(L10N.t("mainbar.menu.copyselected"));
		menuItem.setToolTipText(L10N.t("mainbar.menu.copyselected.tooltip"));

		menuItem.addActionListener(e -> {
			if (mcreator.getMCreatorTabs().getCurrentTab() == mcreator.workspaceTab) {
				var workspacePanel = mcreator.mv;
				ModElement element = getSelectedModElement(workspacePanel);
				if (element == null) {
					showError(workspacePanel, L10N.t("common.tip.notselected"));
					return;
				}

				try {
					String json = mcreator.getWorkspace().getModElementManager()
							.generatableElementToJSON(element.getGeneratableElement());

					if (json == null || element.getGeneratableElement() instanceof CustomElement) {
						showError(workspacePanel, L10N.t("dialog.error.invalidelement"));
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
			} else {
				showError(mcreator.workspaceTab.getContent(), L10N.t("dialog.error.workspacetab"));
			}
		});

		return menuItem;
	}

	private JMenuItem buildDeepCopyMenu(MCreator mcreator) {
		JMenuItem menuItem = new JMenuItem(L10N.t("mainbar.menu.copyselectedmultiple"));
		menuItem.setToolTipText(L10N.t("mainbar.menu.copyselectedmultiple.tooltip"));

		menuItem.addActionListener(e -> {
			if (mcreator.getMCreatorTabs().getCurrentTab() == mcreator.workspaceTab) {
				var workspacePanel = mcreator.mv;
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
			} else {
				showError(mcreator.workspaceTab.getContent(), L10N.t("dialog.error.workspacetab"));
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
		return JOptionPane.showInputDialog(L10N.t("dialog.info.inputbase64"));
	}

	private JsonArray parseInputToJsonArray(String input) throws IOException {
		byte[] raw = Base64.getDecoder().decode(input.getBytes(StandardCharsets.UTF_8));
		String json = JsonUtils.decompress(raw);

		try {
			return GSON.fromJson(json, JsonArray.class);
		} catch (JsonSyntaxException e) {
			JsonArray array = new JsonArray();
			array.add(GSON.fromJson(json, JsonObject.class));
			return array;
		}
	}

	public static void processElementReplacement(MCreator mcreator, ModElement element, JsonObject object) {
		var manager = element.getModElementManager();
		GeneratableElement generatableElement = createGeneratableElement(manager, element, object);

		if (generatableElement == null) {
			showError(mcreator, L10N.t("dialog.error.invalidelement"));
			LOG.warn("generatableElement == null");
			return;
		}

		if (object.has("code")) {
			processCodeElement(mcreator, element, generatableElement, object.get("code").getAsString());
		}

		generatableElement.setModElement(element);
		manager.storeModElement(generatableElement);
	}

	private JsonArray createJsonForElements(MCreator mcreator, List<IElement> elements) throws IOException {
		JsonArray jsonElements = new JsonArray();

		for (IElement element : elements) {
			if (element instanceof ModElement modElement) {
				jsonElements.add(createElementJson(mcreator, modElement));
			} else if (element instanceof FolderElement folderElement) {
				for (ModElement element1 : mcreator.getWorkspace().getModElements()) {
					if (folderElement.getPath().equals(element1.getFolderPath())) {
						jsonElements.add(createElementJson(mcreator, element1));
					}
				}
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

		jsonObject.add(JsonUtils.CONTENT, JsonUtils.map(elementContent));
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
			preview.append(StringUtils.abbreviateString(message, PREVIEW_LENGTH) + "......");
		}
		JOptionPane.showMessageDialog(parent, preview.toString());
	}

	public static void showError(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
		LOG.error("[ErrorMessageShow] {}", message);
	}

	private void handleProcessingError(Component parent, Exception ex) {
		showError(parent, "Processing error: " + ex.getMessage());
		LOG.error("Processing error", ex);
	}
}