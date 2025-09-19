package org.cdc.toolbox.parser;

import org.cdc.toolbox.interfaces.IArgParser;
import org.cdc.toolbox.uitls.MinecraftUtils;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class FORGECommandExecutor implements IArgParser {
	/**
	 *
	 * @param arg arg[1] is the command
	 */
	public void parse(Instrumentation instrumentation, String... arg) {
		if (MinecraftUtils.getMinecraftClassLoader() == null) {
			MinecraftUtils.reloadForgeMinecraftClassLoader(instrumentation);
		}

		try {
			Class<?> cls = MinecraftUtils.getMinecraftClassLoader().loadClass("net.minecraft.client.Minecraft");
			Class<?> listener = MinecraftUtils.getMinecraftClassLoader().loadClass(
					"net.minecraft.client.multiplayer.ClientPacketListener");
			Field field = cls.getDeclaredField("instance");
			field.setAccessible(true);
			Object mineInstance = field.get(null);
			Object connect = cls.getDeclaredMethod("getConnection").invoke(mineInstance);
			if (connect != null)
				listener.getDeclaredMethod("sendCommand", String.class).invoke(connect, arg[1]);
		} catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException |
				 NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
