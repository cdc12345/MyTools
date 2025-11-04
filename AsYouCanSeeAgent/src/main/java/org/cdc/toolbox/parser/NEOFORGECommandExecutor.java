package org.cdc.toolbox.parser;

import org.cdc.toolbox.interfaces.IArgParser;
import org.cdc.toolbox.uitls.MinecraftUtils;

import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

public class NEOFORGECommandExecutor implements IArgParser {
	/**
	 *
	 * @param arg arg[1] is the command
	 */
	public void parse(Instrumentation instrumentation, String... arg) {
		if (MinecraftUtils.getMinecraftClassLoader() == null) {
			MinecraftUtils.reloadNeoforgeMinecraftClassLoader(instrumentation);
		}

		try {
			Class<?> cls = MinecraftUtils.getMinecraftClassLoader().loadClass("net.minecraft.client.Minecraft");
			Class<?> listener = MinecraftUtils.getMinecraftClassLoader().loadClass(
					"net.minecraft.client.multiplayer.ClientPacketListener");
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			Field field = cls.getDeclaredField("instance");
			field.setAccessible(true);
			Object mineInstance = lookup.unreflectGetter(field).invoke();
			var getConnect = lookup.findVirtual(cls,"getConnection",MethodType.methodType(listener));
			Object connect = getConnect.invoke(mineInstance);
			if (connect != null)
				lookup.findVirtual(listener,"sendCommand",MethodType.methodType(Void.TYPE,String.class)).invoke(connect,arg[1]);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
