package org.cdc.toolbox;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import net.mcreator.generator.LocalizationUtils;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.PreGeneratorsLoadingEvent;
import net.mcreator.plugin.events.ui.ModElementGUIEvent;
import net.mcreator.preferences.PreferencesEntry;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.StringEntry;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.toolbox.uitls.AttachUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MyToolBoxMain extends JavaPlugin {

	private static final Logger LOG = LogManager.getLogger("cdc's toolbox");
	private static final PreferencesEntry<Boolean> enableRedefine = new BooleanEntry("enableRedefine", false);
	private static final PreferencesEntry<String> redefineMethod = new StringEntry("redefineMethods", "Redefine",
			"Redefine", "Retransfer", "Vanilla");

	public MyToolBoxMain(Plugin plugin) {
		super(plugin);

		addListener(PreGeneratorsLoadingEvent.class, event -> {
			PreferencesManager.PREFERENCES.gradle.addPluginEntry("mytoolbox", enableRedefine);
			PreferencesManager.PREFERENCES.gradle.addPluginEntry("mytoolbox", redefineMethod);
			PreferencesManager.initNonCore();
		});

		addListener(ModElementGUIEvent.WhenSaving.class, event -> {
			SwingUtilities.invokeLater(() -> {

				CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
					var modElementGUI = event.getModElementGUI();
					var mcreator = event.getMCreator();
					Set<String> extensions = getExtensions(mcreator, modElementGUI);
					boolean needJava;
					needJava = extensions.contains("java");
					extensions.remove("java");
					boolean isDirty = isDirty(mcreator.getWorkspace());
					boolean needReloadResource = !extensions.isEmpty() || isDirty;
					if (isDirty) {
						LocalizationUtils.generateLanguageFiles(mcreator.getGenerator(), mcreator.getWorkspace(),
								mcreator.getGeneratorConfiguration().getLanguageFileSpecification());
					}
					if (enableRedefine.get() && mcreator.getDebugPanel().getDebugClient() != null) {
						getTabs(mcreator).showTab(mcreator.consoleTab);
						mcreator.getGradleConsole().exec("classes", result -> {
							var PACKAGE_PATH = new File(mcreator.getWorkspaceFolder(), "build/classes/java/main");
							if (needJava && needVirtualMachine()) {

								VirtualMachine virtualMachine = mcreator.getDebugPanel().getDebugClient()
										.getVirtualMachine();
								if (virtualMachine != null) {
									LOG.info("get the nonnull virtual machine");
									if (redefineMethod.get().equals("Retransfer")) {
										mcreator.getGradleConsole().append("attached " + virtualMachine.version());
										try {
											if (AttachUtils.attachWithClass(mcreator.getWorkspaceFolder().toString(),
													getPlugin().getFile(), PACKAGE_PATH,
													modElementGUI.getModElement().getName())) {
												LOG.info("Attach Successful");
											} else {
												LOG.info("Failed");
											}
										} catch (Exception e) {
											LOG.info(e);
										}
									} else if (redefineMethod.get().equals("Redefine")) {
										var map = new HashMap<ReferenceType, byte[]>();
										virtualMachine.allClasses().forEach(classReference -> {
											String name = classReference.name().replace('.', '/');
											File classFile = new File(PACKAGE_PATH, name + ".class");
											if (classFile.exists() && name.contains(
													modElementGUI.getModElement().getName())) {

												try {
													var bytes = Files.readAllBytes(classFile.toPath());
													map.put(classReference, bytes);
													LOG.info("{}:{}", classReference.name(),
															classFile.getAbsolutePath());
												} catch (Exception e) {
													LOG.info(e);
												}
											}
										});
										if (!map.isEmpty()) {
											try {
												virtualMachine.redefineClasses(map);
											} catch (Exception e) {
												mcreator.getGradleConsole()
														.appendPlainText(e.getClass().getName() + ":" + e.getMessage(),
																Color.RED);
											}
											mcreator.getGradleConsole().append(map.keySet().toString());

										}
										LOG.info("redefined all client procedure classes");
									}
								}
							}
							if (redefineMethod.get().equals("Retransfer") && needReloadResource) {
								try {
									if (AttachUtils.attachToResourcesLoaded(mcreator.getWorkspaceFolder().toString(),
											getPlugin().getFile(),
											mcreator.getGeneratorConfiguration().getGeneratorFlavor(),
											isDirty)) {
										LOG.info("Attach Successful and command reload");
									} else {
										LOG.info("Failed with command");
									}
								} catch (Exception e) {
									mcreator.getGradleConsole()
											.appendPlainText(e.getClass().getName() + ":" + e.getMessage(), Color.RED);
								}
							}
							mcreator.getGradleConsole().appendPlainText("All resources has been updated", Color.WHITE);

							mcreator.getGradleConsole().appendPlainText("NeedJava: "+needJava+",NeedReload: "+needReloadResource+",Dirty: "+isDirty+",Extensions: "+extensions, Color.WHITE);
						});
						mcreator.getGradleConsole().markRunning();
					}
				});
			});
		});
	}

	private boolean needVirtualMachine() {
		List<String> needs = List.of("Redefine", "Retransfer");
		return needs.contains(redefineMethod.get());
	}

	private Set<String> getExtensions(MCreator mCreator, ModElementGUI<?> modElementGUI) {
		HashSet<String> sets = new HashSet<>();
		mCreator.getGenerator().getModElementGeneratorTemplatesList(modElementGUI.getElementFromGUI()).forEach(a -> {
			sets.add(com.google.common.io.Files.getFileExtension(a.getFile().getName()));
		});
		return sets;
	}

	private boolean isDirty(Workspace workspace) {
		try {
			Field field = workspace.getClass().getDeclaredField("changed");
			field.setAccessible(true);
			return (Boolean) field.get(workspace);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			return false;
		}
	}

	public static Logger getLOG() {
		return LOG;
	}

	//This will change in 2024.3 # getMCreatorTabs
	private MCreatorTabs getTabs(MCreator mcreator){
		return mcreator.getTabs();
	}
}
