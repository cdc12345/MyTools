package org.cdc.toolbox.uitls;

import java.lang.instrument.Instrumentation;

public class MinecraftUtils {
	private static ClassLoader mcClassLoader;

	public static void reloadForgeMinecraftClassLoader(Instrumentation instrumentation) {
		for (Class<?> cls : instrumentation.getAllLoadedClasses()) {
			if (cls.getName().equals("net.minecraft.client.Minecraft")) {
				setMinecraftClassLoader(cls.getClassLoader());
			}
		}
	}

	public static void reloadNeoforgeMinecraftClassLoader(Instrumentation instrumentation) {
		for (Class<?> cls : instrumentation.getAllLoadedClasses()) {
			if (cls.getName().equals("net.minecraft.client.Minecraft")) {
				setMinecraftClassLoader(cls.getClassLoader());
			}
		}
	}

	public static void setMinecraftClassLoader(ClassLoader classLoader){
		mcClassLoader = classLoader;
	}

	public static ClassLoader getMinecraftClassLoader(){
		return mcClassLoader;
	}
}
