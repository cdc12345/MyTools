package org.cdc.dev.utils;

import net.mcreator.element.GeneratableElement;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.plugin.DynamicURLClassLoader;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.PreGeneratorsLoadingEvent;
import net.mcreator.plugin.events.ui.ModElementGUIEvent;
import net.mcreator.plugin.events.workspace.MCreatorLoadedEvent;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.component.ConsolePane;
import net.mcreator.ui.gradle.GradleConsole;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.dev.Constants;
import org.cdc.dev.sections.DevUtilsSection;
import org.cdc.dev.utils.manager.ElementManager;
import org.cdc.dev.utils.manager.WorkspaceManager;
import org.cdc.interfaces.IMCreator;
import org.cdc.interfaces.MCreatorImpl;
import org.jackhuang.hmcl.game.CrashReportAnalyzer;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class PluginMain extends JavaPlugin {

	private static final Logger LOGGER = LogManager.getLogger(PluginMain.class);
	private static final int MAX_FIELD_DISPLAY = 30;

	private JMenu resultsInspect;
	private JMenu editWorkspaceDefinition;
	private JMenu extractElement;

	private String currentElement = "";
	private Iterator<Field> fields;
	private List<Field> fieldList = Collections.emptyList();

	public PluginMain(Plugin plugin) throws IOException, URISyntaxException {
		super(plugin);
		initializePlugin();
	}

	private void initializePlugin() throws IOException, URISyntaxException {
		loadJavaParserDependency();
		registerEventListeners();
	}

	private void loadJavaParserDependency() throws IOException, URISyntaxException {
		var javaParserPath = Constants.getCacheFile().toPath().resolve("javaparser.jar");
		if (Files.notExists(javaParserPath)) {
			Files.copy(new URI(Constants.JAVAPARSER_URL).toURL().openStream(), javaParserPath);
		}

		if (getClass().getClassLoader() instanceof DynamicURLClassLoader dynamicURLClassLoader) {
			dynamicURLClassLoader.addURL(javaParserPath.toUri().toURL());
		}
	}

	private void registerEventListeners() {
		// Pre-generators loading event
		this.addListener(PreGeneratorsLoadingEvent.class, event -> {
			DevUtilsSection.getInstance();
			PreferencesManager.initNonCore();
		});

		// Mod element saving event
		this.addListener(ModElementGUIEvent.WhenSaving.class, event -> {
			applyElementPatches(event.getModElementGUI());
		});

		// MCreator loaded event
		this.addListener(MCreatorLoadedEvent.class, event -> {
			initializeUI(new MCreatorImpl(event.getMCreator()));
		});
	}

	private void applyElementPatches(ModElementGUI<?> modElementGUI) {
		SwingUtilities.invokeLater(() -> {
			try {
				var element = modElementGUI.getModElement().getGeneratableElement();
				if (element != null && ElementManager.applyPatchesToElement(element)) {
					LOGGER.info("Modifier for {} applied", element.getModElement().getName());
					modElementGUI.getMCreator().getStatusBar().setPersistentMessage("Modifier applied");
				}
			} catch (TemplateGeneratorException e) {
				LOGGER.error("Failed to apply patches to element", e);
				JOptionPane.showMessageDialog(null, "Failed to apply modifier: " + e.getMessage());
			}
		});
	}

	private void initializeUI(IMCreator mcreator) {
		JMenu devUtilsMenu = createDevUtilsMenu(mcreator);
		mcreator.getMainMenuBar().add(devUtilsMenu);
	}

	private JMenu createDevUtilsMenu(IMCreator mcreator) {
		JMenu devUtils = new JMenu("DevUtils");

		// Console operations
		devUtils.add(createConsoleOperationsMenu(mcreator));

		// Workspace operations
		devUtils.add(createWorkspaceOperationsMenu(mcreator));

		// Element operations
		devUtils.add(createElementOperationsMenu(mcreator));

		return devUtils;
	}

	private JMenu createConsoleOperationsMenu(IMCreator mcreator) {
		JMenu consoleMenu = L10N.menu("devutils.consoleoperation.name");

		// Editable permission toggle
		JMenuItem editPermission = new JMenuItem("Editable permission");
		editPermission.addActionListener(e -> toggleConsoleEditable(mcreator));
		consoleMenu.add(editPermission);

		// Crash report analysis
		JMenuItem analyseErrorReport = new JMenuItem("Analyse the crash report");
		analyseErrorReport.addActionListener(e -> analyzeCrashReport(mcreator));
		consoleMenu.add(analyseErrorReport);

		// Results inspection
		resultsInspect = new JMenu("Inspect results");
		consoleMenu.add(resultsInspect);

		return consoleMenu;
	}

	private JMenu createWorkspaceOperationsMenu(IMCreator mcreator) {
		JMenu workspaceMenu = L10N.menu("devutils.workspaceoperation.name");

		// Source removal operations
		addWorkspaceRemovalOperations(workspaceMenu, mcreator);

		// Workspace definition editing
		editWorkspaceDefinition = new JMenu(L10N.t("devutils.workspaceoperation.ewd.name"));
		editWorkspaceDefinition.addMouseListener(createWorkspaceDefinitionMouseListener(mcreator));
		workspaceMenu.add(editWorkspaceDefinition);

		// Workspace snapshot export
		JMenuItem exportWorkspaceReport = new JMenuItem(L10N.t("devutils.workspaceoperation.ews.name"));
		exportWorkspaceReport.addActionListener(e -> exportWorkspaceSnapshot(mcreator));
		workspaceMenu.add(exportWorkspaceReport);

		return workspaceMenu;
	}

	private JMenu createElementOperationsMenu(IMCreator mcreator) {
		JMenu elementMenu = L10N.menu("devutils.elementoperation.name");

		// Edit current element
		JMenuItem editCurrentElementFile = new JMenuItem(L10N.t("devutils.elementoperation.ecef.name"));
		editCurrentElementFile.addActionListener(e -> openCurrentElementDefinition(mcreator));
		elementMenu.add(editCurrentElementFile);

		// Reload elements
		JMenuItem reloadElements = new JMenuItem(L10N.t("devutils.elementoperation.re.name"));
		reloadElements.addActionListener(e -> mcreator.getModElementManager().invalidateCache());
		elementMenu.add(reloadElements);

		// Field extraction
		extractElement = new JMenu("Extract element field");
		extractElement.addMouseListener(createExtractElementMouseListener(mcreator));
		elementMenu.add(extractElement);

		// Modifier generation
		JMenuItem generateModifier = new JMenuItem("Generate modifier");
		generateModifier.addActionListener(e -> generateElementModifier(mcreator));
		elementMenu.add(generateModifier);

		return elementMenu;
	}

	private void addWorkspaceRemovalOperations(JMenu workspaceMenu, IMCreator mcreator) {
		// Source removal
		JMenuItem removeSrc = new JMenuItem(L10N.t("devutils.workspaceoperation.rmsrc.name"));
		removeSrc.addActionListener(e -> WorkspaceManager.removeSrc(mcreator));
		workspaceMenu.add(removeSrc);
		workspaceMenu.addSeparator();

		// Data pack removal
		JMenuItem removeDataFolder = new JMenuItem(L10N.t("devutils.workspaceoperation.rmdf.name"));
		removeDataFolder.addActionListener(e -> WorkspaceManager.removeDataPack(mcreator));
		workspaceMenu.add(removeDataFolder);

		JMenuItem removeWholeDataFolder = new JMenuItem(L10N.t("devutils.workspaceoperation.rmwdf.name"));
		removeWholeDataFolder.addActionListener(e -> WorkspaceManager.removeWholeDataPack(mcreator));
		workspaceMenu.add(removeWholeDataFolder);
		workspaceMenu.addSeparator();

		// Assets removal
		JMenuItem removeAssetsFolder = new JMenuItem(L10N.t("devutils.workspaceoperation.rmaf.name"));
		removeAssetsFolder.addActionListener(e -> WorkspaceManager.removeAssets(mcreator));
		workspaceMenu.add(removeAssetsFolder);

		JMenuItem removeAssetsJsonFiles = new JMenuItem(L10N.t("devutils.workspaceoperation.rmajf.name"));
		removeAssetsJsonFiles.addActionListener(e -> WorkspaceManager.removeAllJsonFileFromAssets(mcreator));
		workspaceMenu.add(removeAssetsJsonFiles);

		JMenuItem removeWholeAssetsFolder = new JMenuItem(L10N.t("devutils.workspaceoperation.rmwaf.name"));
		removeWholeAssetsFolder.addActionListener(e -> WorkspaceManager.removeWholeAssets(mcreator));
		workspaceMenu.add(removeWholeAssetsFolder);
		workspaceMenu.addSeparator();
	}

	// Console operation methods
	private void toggleConsoleEditable(IMCreator mcreator) {
		SwingUtilities.invokeLater(() -> {
			try {
				Field consolePaneField = GradleConsole.class.getDeclaredField("pan");
				consolePaneField.setAccessible(true);
				ConsolePane consolePane = (ConsolePane) consolePaneField.get(mcreator.getGradleConsole());
				consolePane.clearConsole();
				consolePane.setEditable(!consolePane.isEditable());
			} catch (Exception e) {
				LOGGER.error("Failed to toggle console editable", e);
			}
		});
	}

	private void analyzeCrashReport(IMCreator mcreator) {
		SwingUtilities.invokeLater(() -> {
			try {
				String log = getConsoleLog(mcreator);
				analyzeAndDisplayResults(log);
			} catch (Exception e) {
				LOGGER.error("Failed to analyze crash report", e);
				JOptionPane.showMessageDialog(null, "Analysis failed: " + e.getMessage());
			}
		});
	}

	private String getConsoleLog(IMCreator mcreator) throws Exception {
		Field consolePaneField = GradleConsole.class.getDeclaredField("pan");
		consolePaneField.setAccessible(true);
		ConsolePane consolePane = (ConsolePane) consolePaneField.get(mcreator.getGradleConsole());

		return consolePane.isEditable() ?
				HTMLUtils.html2text(consolePane.getText()) :
				mcreator.getGradleConsole().getConsoleText();
	}

	private void analyzeAndDisplayResults(String log) {
		var results = CrashReportAnalyzer.analyze(log);
		var keyWords = CrashReportAnalyzer.findKeywordsFromCrashReport(log);

		LOGGER.info("Found keywords: {}", keyWords);
		updateErrorResults(results, keyWords);

		var resultRules = results.stream()
				.map(result -> result.getRule().name())
				.toList();
		JOptionPane.showMessageDialog(null, resultRules);
	}

	// Element operation methods
	private void openCurrentElementDefinition(IMCreator mcreator) {
		var element = getCurrentGeneratableElement(mcreator);
		if (element != null) {
			ElementManager.openElementDefinition(mcreator, element.getModElement());
		}
	}

	private void generateElementModifier(IMCreator mcreator) {
		try {
			var element = getCurrentGeneratableElement(mcreator);
			if (element == null) {
				JOptionPane.showMessageDialog(mcreator.getOrigin(), "No element selected");
				return;
			}

			ElementManager.createModifiersThroughElement(element);
			JOptionPane.showMessageDialog(mcreator.getOrigin(),
					"Modifier for element " + element.getModElement().getName() + " created");
		} catch (TemplateGeneratorException e) {
			LOGGER.error("Failed to generate modifier", e);
			JOptionPane.showMessageDialog(null, "Failed to generate modifier: " + e.getMessage());
		}
	}

	// Field extraction methods
	private MouseAdapter createExtractElementMouseListener(IMCreator mcreator) {
		return new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				SwingUtilities.invokeLater(() -> updateExtractElement(mcreator));
			}
		};
	}

	private void updateExtractElement(IMCreator mcreator) {
		extractElement.removeAll();
		var generatable = getCurrentGeneratableElement(mcreator);

		if (generatable != null) {
			LOGGER.info("Updating element fields for: {}", generatable.getModElement().getName());
			initializeOrUpdateFields(generatable);
			addFieldMenuItems(generatable, mcreator);
		}
	}

	private void initializeOrUpdateFields(GeneratableElement generatable) {
		String elementName = generatable.getModElement().getName();
		if (!currentElement.equals(elementName) || !fields.hasNext()) {
			fieldList = getSortedFields(generatable.getClass(), generatable);
			fields = fieldList.iterator();
			currentElement = elementName;
		}
	}

	private void addFieldMenuItems(GeneratableElement generatable, IMCreator mcreator) {
		int count = 0;
		while (fields.hasNext() && count < MAX_FIELD_DISPLAY) {
			Field field = fields.next();
			addFieldMenuItem(field, generatable, mcreator);
			count++;
		}
	}

	private void addFieldMenuItem(Field field, GeneratableElement generatable, IMCreator mcreator) {
		var menu = new JMenuItem(field.getName() + " - " + field.getType().getSimpleName());
		menu.addActionListener(e -> extractFieldValue(field, generatable, mcreator));
		extractElement.add(menu);
	}

	private void extractFieldValue(Field field, GeneratableElement generatable, IMCreator mcreator) {
		try {
			Object value = field.get(generatable);
			String stringValue = ElementManager.toReadableString(value);

			if (stringValue != null) {
				var content = new StringSelection(stringValue);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(content, content);
				mcreator.getStatusBar().setMessage("Extracted: " + stringValue);
			}
		} catch (IllegalAccessException e) {
			LOGGER.warn("Cannot access field: {}", field.getName(), e);
		}
	}

	private List<Field> getSortedFields(Class<? extends GeneratableElement> cls, GeneratableElement element) {
		if (currentElement.equals(element.getModElement().getName()) && !fieldList.isEmpty()) {
			return fieldList;
		}

		List<Field> fields = new ArrayList<>(List.of(cls.getFields()));
		fields.sort(this::compareFieldsByPriority);
		return fields;
	}

	private int compareFieldsByPriority(Field f1, Field f2) {
		return Integer.compare(getFieldPriority(f1), getFieldPriority(f2));
	}

	private int getFieldPriority(Field field) {
		int priority = 0;
		Class<?> type = field.getType();

		if (type == String.class) {
			priority -= 5;
		} else if (type.isPrimitive()) {
			priority -= 6;
		} else if (hasToStringMethod(type)) {
			priority -= 1;
		}

		return priority;
	}

	private boolean hasToStringMethod(Class<?> type) {
		try {
			type.getMethod("toString");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Workspace definition methods
	private MouseAdapter createWorkspaceDefinitionMouseListener(IMCreator mcreator) {
		return new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				updateWorkspaceSelector(mcreator);
			}
		};
	}

	private void updateWorkspaceSelector(IMCreator mcreator) {
		editWorkspaceDefinition.removeAll();
		mcreator.getApplication().getWorkspaceSelector().getRecentWorkspaces().getList().forEach(workspace -> {
			JMenuItem menuItem = new JMenuItem(workspace.getName());
			menuItem.addActionListener(e ->
					WorkspaceManager.openWorkspaceDefinition(mcreator, workspace.getPath()));
			editWorkspaceDefinition.add(menuItem);
		});
	}

	// Results display methods
	private void updateErrorResults(Set<CrashReportAnalyzer.Result> results, Set<String> keyWords) {
		resultsInspect.removeAll();

		// Add keyword menu item
		var keywordsItem = new JMenuItem("key_words");
		keywordsItem.addActionListener(e ->
				JOptionPane.showMessageDialog(null, keyWords.toString()));
		resultsInspect.add(keywordsItem);

		// Add result menu items
		for (CrashReportAnalyzer.Result result : results) {
			JMenuItem menuItem = new JMenuItem(result.getRule().name());
			menuItem.addActionListener(e ->
					JOptionPane.showMessageDialog(null, result.getMatcher().group()));
			resultsInspect.add(menuItem);
		}
	}

	// Utility methods
	private GeneratableElement getCurrentGeneratableElement(IMCreator mcreator) {
		var content = mcreator.getTabs().getCurrentTab().getContent();

		if (content instanceof ModElementGUI<?> modElementGUI) {
			return modElementGUI.getElementFromGUI();
		} else if (content instanceof WorkspacePanel panel && panel.list.getSelectedValue() instanceof ModElement modElement) {
			return modElement.getGeneratableElement();
		}

		return null;
	}

	private void exportWorkspaceSnapshot(IMCreator mcreator) {
		try {
			var snapshot = WorkspaceManager.exportSnapshot(mcreator);
			var content = new StringSelection(snapshot.toString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(content, content);
			JOptionPane.showMessageDialog(mcreator.getOrigin(), "Snapshot exported to clipboard");
		} catch (IOException e) {
			LOGGER.error("Failed to export workspace snapshot", e);
			JOptionPane.showMessageDialog(mcreator.getOrigin(), "Export failed: " + e.getMessage());
		}
	}
}