package org.cdc.toolbox.uitls;

import com.sun.tools.attach.*;
import net.mcreator.generator.GeneratorFlavor;
import org.cdc.toolbox.MyToolBoxMain;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class AttachUtils {

	public static boolean attachToResourcesLoaded(String workspaceBuildFolder, File pluginFile,
			GeneratorFlavor generatorFlavor, boolean reloadPacks)
			throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
		VirtualMachine virtualMachine;
		virtualMachine = attachSelf(workspaceBuildFolder);
		if (virtualMachine == null) {
			return false;
		}

		virtualMachine.loadAgent(pluginFile.getAbsolutePath(),
				"org.cdc.toolbox.parser." + generatorFlavor.name() + "ResourcesReload" + (reloadPacks ?
						">reloadPacks" :
						""));
		return true;
	}

	public static boolean attachWithCommand(String workspaceFolder, File pluginFile, String command,
			GeneratorFlavor generatorFlavor)
			throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
		VirtualMachine virtualMachine;
		virtualMachine = attachSelf(workspaceFolder);
		if (virtualMachine == null) {
			return false;
		}

		virtualMachine.loadAgent(pluginFile.getAbsolutePath(),
				"org.cdc.toolbox.parser." + generatorFlavor.name() + "CommandExecutor>" + command);
		return true;
	}

	public static boolean attachWithClass(String workspaceBuildFolder, File pluginFile, File buildPath,
			String modElementName)
			throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
		VirtualMachine virtualMachine;
		virtualMachine = attachSelf(workspaceBuildFolder);
		if (virtualMachine == null) {
			return false;
		}

		String lib = findJavassistPath(new File(System.getProperty("user.dir") + "/lib"));
		if (lib != null)
			virtualMachine.loadAgentLibrary(lib);
		virtualMachine.loadAgent(pluginFile.getAbsolutePath(),
				"org.cdc.toolbox.parser.RetransferClassParser>" + buildPath.getAbsolutePath() + ">" + modElementName);
		return true;
	}

	private static VirtualMachine attachSelf(String workspaceBuildFolder) throws AttachNotSupportedException, IOException {
		VirtualMachine virtualMachine = null;
		for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
			if (descriptor.displayName().contains("net.neoforged.devlaunch.Main") || descriptor.displayName()
					.contains("cpw.mods.modlauncher.Launcher")) {
				virtualMachine = VirtualMachine.attach(descriptor);
				if (!virtualMachine.getSystemProperties().getProperty("java.class.path").contains(workspaceBuildFolder)) {
					virtualMachine.detach();
					virtualMachine = null;
				}
			}
		}
		return virtualMachine;
	}

	private static String findJavassistPath(File libraryPath) {
		MyToolBoxMain.getLOG().info(libraryPath.getPath());
		for (File file : Objects.requireNonNull(libraryPath.listFiles())) {
			if (file.getName().startsWith("javassist")) {
				return file.getPath();
			}
		}
		return null;
	}
}
