package org.cdc.dev.utils;

import net.mcreator.element.GeneratableElement;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.workspace.MCreatorLoadedEvent;
import net.mcreator.ui.component.ConsolePane;
import net.mcreator.ui.gradle.GradleConsole;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PluginMain extends JavaPlugin {

	private static final Logger LOGGER = LogManager.getLogger(PluginMain.class);
	private JMenu resultsInspect;
	private JMenu editWorkspaceDefinition;

	private JMenuItem extractElement;

	public PluginMain(Plugin plugin) {
		super(plugin);

		this.addListener(MCreatorLoadedEvent.class, event -> {
			final var mcreator = new MCreatorImpl(event.getMCreator());
			final var workspace = mcreator.getWorkspace();
			final var mainMenuBar = mcreator.getMainMenuBar();

			JMenu devUtils = new JMenu("DevUtils");
			mainMenuBar.add(devUtils);

			JMenu consoleOperation = L10N.menu("devutils.consoleoperation.name");
			devUtils.add(consoleOperation);

			JMenuItem editPermission = new JMenuItem("Editable permission");
			editPermission.addActionListener(a -> {
				SwingUtilities.invokeLater(() -> {
					try {
						var consolePane = GradleConsole.class.getDeclaredField("pan");
						consolePane.setAccessible(true);
						ConsolePane consolePane1 = (ConsolePane) consolePane.get(mcreator.getGradleConsole());
						consolePane1.clearConsole();
						consolePane1.setEditable(!consolePane1.isEditable());
					} catch (NoSuchFieldException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				});
			});
			consoleOperation.add(editPermission);

			JMenuItem analyseErrorReport = new JMenuItem("Analyse the crash report");
			analyseErrorReport.addActionListener(a -> {
				SwingUtilities.invokeLater(() -> {
					String log;
					try {
						var consolePane = GradleConsole.class.getDeclaredField("pan");
						consolePane.setAccessible(true);
						ConsolePane consolePane1 = (ConsolePane) consolePane.get(mcreator.getGradleConsole());
						if (consolePane1.isEditable()) {
							log = HTMLUtils.html2text(consolePane1.getText());
						} else {
							log = mcreator.getGradleConsole().getConsoleText();
						}
					} catch (NoSuchFieldException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
					var results = CrashReportAnalyzer.analyze(log);
					updateErrorResults(results);
					Set<String> keyWords = CrashReportAnalyzer.findKeywordsFromCrashReport(log);
					final var keyWords1 = keyWords.toString();
					LOGGER.info(keyWords1);
					var key = new JMenuItem("key_words");
					key.addActionListener(b -> {
						JOptionPane.showMessageDialog(null, keyWords1);
					});
					resultsInspect.add(key);
					JOptionPane.showMessageDialog(null,
							results.stream().map(CrashReportAnalyzer.Result::getRule).toList());
				});
			});
			consoleOperation.add(analyseErrorReport);

			resultsInspect = new JMenu("Inspect results");
			consoleOperation.add(resultsInspect);

			//workspace operation
			JMenu workspaceOperation = L10N.menu("devutils.workspaceoperation.name");
			devUtils.add(workspaceOperation);

			JMenuItem removeSrc = new JMenuItem(L10N.t("devutils.workspaceoperation.rmsrc.name"));
			removeSrc.addActionListener(a -> WorkspaceManager.removeSrc(mcreator));
			workspaceOperation.add(removeSrc);

			workspaceOperation.addSeparator();

			JMenuItem removeDataFolder = new JMenuItem(L10N.t("devutils.workspaceoperation.rmdf.name"));
			removeDataFolder.addActionListener(a -> WorkspaceManager.removeDataPack(mcreator));
			workspaceOperation.add(removeDataFolder);

			JMenuItem removeWholeDataFolder = new JMenuItem(L10N.t("devutils.workspaceoperation.rmwdf.name"));
			removeWholeDataFolder.addActionListener(a -> WorkspaceManager.removeWholeDataPack(mcreator));
			workspaceOperation.add(removeWholeDataFolder);

			workspaceOperation.addSeparator();

			JMenuItem removeAssetsFolder = new JMenuItem(L10N.t("devutils.workspaceoperation.rmaf.name"));
			removeAssetsFolder.addActionListener(a -> WorkspaceManager.removeAssets(mcreator));
			workspaceOperation.add(removeAssetsFolder);

			JMenuItem removeAssetsJsonFiles = new JMenuItem(L10N.t("devutils.workspaceoperation.rmajf.name"));
			removeAssetsJsonFiles.addActionListener(a -> WorkspaceManager.removeAllJsonFileFromAssets(mcreator));
			workspaceOperation.add(removeAssetsJsonFiles);

			JMenuItem removeWholeAssetsFolder = new JMenuItem(L10N.t("devutils.workspaceoperation.rmwaf.name"));
			removeWholeAssetsFolder.addActionListener(a -> WorkspaceManager.removeWholeAssets(mcreator));
			workspaceOperation.add(removeWholeAssetsFolder);

			workspaceOperation.addSeparator();

			editWorkspaceDefinition = new JMenu(L10N.t("devutils.workspaceoperation.ewd.name"));
			editWorkspaceDefinition.addMouseListener(new MouseAdapter() {
				@Override public void mouseEntered(MouseEvent e) {
					updateWorkspaceSelector(mcreator);
				}
			});
			workspaceOperation.add(editWorkspaceDefinition);

			var exportWorkspaceReport = new JMenuItem("Export workspace snapshot");
			exportWorkspaceReport.addActionListener(a->{
				try {
					var con = new StringSelection(WorkspaceManager.exportSnapshot(workspace).toString());
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(con,con);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
			workspaceOperation.add(exportWorkspaceReport);

			//element operation
			JMenu elementOperation = L10N.menu("devutils.elementoperation.name");
			devUtils.add(elementOperation);

			JMenuItem editCurrentElementFile = new JMenuItem(L10N.t("devutils.elementoperation.ecef.name"));
			editCurrentElementFile.addActionListener(a -> {
				var element = getProbablyElement(mcreator);
				if (element != null) {
					ElementManager.openElementDefinition(mcreator, element.getModElement());
				}
			});
			elementOperation.add(editCurrentElementFile);

			JMenuItem reloadElements = new JMenuItem(L10N.t("devutils.elementoperation.re.name"));
			reloadElements.addActionListener(a -> {
				mcreator.getModElementManager().invalidateCache();
			});
			elementOperation.add(reloadElements);

			extractElement = new JMenu("Extract element field");
			extractElement.addMouseListener(new MouseAdapter() {
				@Override public void mouseEntered(MouseEvent e) {
					SwingUtilities.invokeLater(() -> {
						updateExtractElement(mcreator);
					});
				}
			});
			elementOperation.add(extractElement);
		});
	}

	String currentElement = "";
	Iterator<Field> fields;

	private void updateExtractElement(IMCreator mcreator) {
		extractElement.removeAll();
		var generatable = getProbablyElement(mcreator);
		if (generatable != null) {
			var cls = generatable.getClass();
			LOGGER.info("Update element fields");
			int count = 0;
			if (!currentElement.equals(generatable.getModElement().getName()) || !fields.hasNext()) {
				var fieldList = getFields(cls, generatable);
				fields = fieldList.iterator();
				currentElement = generatable.getModElement().getName();
			}
			while (fields.hasNext()) {
				var field = fields.next();
				count++;
				var menu = new JMenuItem(field.getName() + " - " + field.getType().getName());
				menu.addActionListener(a -> {
					try {
						var value = ElementManager.toReadableString(field.get(generatable));
						if (value != null) {
							var content = new StringSelection(value.toString());
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents(content, content);
						}
						mcreator.getStatusBar().setMessage("Extracted " + value);
					} catch (IllegalAccessException ignored) {
					}
				});
				extractElement.add(menu);
				if (count == 30) {
					break;
				}
			}
		}
	}

	ArrayList<Field> fieldList = null;

	private ArrayList<Field> getFields(Class<? extends GeneratableElement> cls, GeneratableElement generatableElement) {
		if (currentElement.equals(generatableElement.getModElement().getName())) {
			return fieldList;
		}
		fieldList = new ArrayList<>(List.of(cls.getFields()));
		fieldList.sort((o1, o2) -> {
			int leftValue = 0;
			if (o1.getType() == String.class) {
				leftValue -= 5;
			} else if (o1.getType().isPrimitive()) {
				leftValue -= 6;
			} else {
				try {
					o1.getType().getMethod("toString");
					leftValue -= 1;
				} catch (Exception ignored) {
				}
			}
			int rightValue = 0;

			if (o2.getType() == String.class) {
				rightValue -= 5;
			} else if (o2.getType().isPrimitive()) {
				rightValue -= 6;
			} else {
				try {
					o2.getType().getMethod("toString");
					rightValue -= 1;
				} catch (Exception ignored) {
				}
			}
			return leftValue - rightValue;
		});
		return fieldList;
	}

	private GeneratableElement getProbablyElement(IMCreator mcreator) {
		var content = mcreator.getTabs().getCurrentTab().getContent();
		if (content instanceof ModElementGUI<?> modElementGUI) {
			return modElementGUI.getElementFromGUI();
		} else if (content instanceof WorkspacePanel panel) {
			if (panel.list.getSelectedValue() instanceof ModElement modElement) {
				return modElement.getGeneratableElement();
			}
		}
		return null;
	}

	private void updateWorkspaceSelector(IMCreator mcreator) {
		editWorkspaceDefinition.removeAll();
		mcreator.getApplication().getWorkspaceSelector().getRecentWorkspaces().getList().forEach(a -> {
			JMenuItem menuItem = new JMenuItem(a.getName());
			menuItem.addActionListener(b -> {
				WorkspaceManager.openWorkspaceDefinition(mcreator, a.getPath());
			});
			editWorkspaceDefinition.add(menuItem);
		});
	}

	private void updateErrorResults(Set<CrashReportAnalyzer.Result> results) {
		resultsInspect.removeAll();
		for (CrashReportAnalyzer.Result result : results) {
			JMenuItem menuItem = new JMenuItem(result.getRule().name());
			menuItem.addActionListener(b -> {
				JOptionPane.showMessageDialog(null, result.getMatcher().group());
			});
			resultsInspect.add(menuItem);
		}
	}

}
