package org.cdc.toolbox;

import com.sun.jdi.ReferenceType;
import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.Plugin;
import net.mcreator.plugin.events.ui.ModElementGUIEvent;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.gradle.GradleConsole;
import net.mcreator.ui.modgui.ProcedureGUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class MyToolBoxMain extends JavaPlugin {

	private static final Logger LOG = LogManager.getLogger("cdc's toolbox");

	public MyToolBoxMain(Plugin plugin) {
		super(plugin);

		addListener(ModElementGUIEvent.WhenSaving.class, a -> {
			if (a.getModElementGUI() instanceof ProcedureGUI
					&& a.getMCreator().getDebugPanel().getDebugClient() != null) {
				MCreator mCreator = a.getMCreator();
				mCreator.getGradleConsole().exec("build", result -> {
					var PACKAGE_PATH = new File(mCreator.getWorkspaceFolder(), "build/classes/java/main");
					var virtualMachine = mCreator.getDebugPanel().getDebugClient().getVirtualMachine();
					if (virtualMachine != null) {
						LOG.info("get the nonnull virtual machine");
						if (mCreator.getGradleConsole().getStatus() == GradleConsole.READY) {
							var map = new HashMap<ReferenceType, byte[]>();
							virtualMachine.allClasses().forEach(b -> {
								String name = b.name().replace('.', '/');
								File classFile = new File(PACKAGE_PATH, name + ".class");
								if (classFile.exists() && name.contains("/procedures/")) {
									try {
										var bytes = Files.readAllBytes(classFile.toPath());
										map.put(b, bytes);
										LOG.info("{}:{}", b.name(), classFile.getAbsolutePath());
									} catch (IOException e) {
										throw new RuntimeException(e);
									}
								}

							});
							virtualMachine.redefineClasses(map);
							LOG.info("redefined all client procedure classes");
						}
					}
				});
				mCreator.getGradleConsole().markRunning();
			}
		});
	}

}
