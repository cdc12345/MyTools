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

	public static boolean attachWithCommand(String workspaceBuildFolder, File pluginFile, String command,
			GeneratorFlavor generatorFlavor)
			throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
		VirtualMachine virtualMachine;
		virtualMachine = attachSelf(workspaceBuildFolder);
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

		//		String lib = findLibraryPath(new File(System.getProperty("user.dir") + "/lib"),"javassist");
		//		if (lib != null)
		//			virtualMachine.loadAgentLibrary(lib);
		virtualMachine.loadAgent(pluginFile.getAbsolutePath(),
				"org.cdc.toolbox.parser.RetransferClassParser>" + buildPath.getAbsolutePath() + ">" + modElementName);
		return true;
	}

	private static VirtualMachine attachSelf(String workspaceBuildFolder)
			throws AttachNotSupportedException, IOException {
		VirtualMachine virtualMachine = null;
		for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
			if (descriptor.displayName().contains("net.neoforged.devlaunch.Main") || descriptor.displayName()
					.contains("cpw.mods.bootstraplauncher.BootstrapLauncher")) {
				virtualMachine = VirtualMachine.attach(descriptor);
				MyToolBoxMain.getLOG().info(workspaceBuildFolder);
				if (!virtualMachine.getSystemProperties().getProperty("user.dir")
						.contains(workspaceBuildFolder)) {
					virtualMachine.detach();
					virtualMachine = null;
				}
			}
		}
		return virtualMachine;
	}

	private static String findLibraryPath(File libraryPath, String name) {
		MyToolBoxMain.getLOG().info(libraryPath.getPath());
		for (File file : Objects.requireNonNull(libraryPath.listFiles())) {
			if (file.getName().startsWith(name)) {
				return file.getPath();
			}
		}
		return null;
	}
}
