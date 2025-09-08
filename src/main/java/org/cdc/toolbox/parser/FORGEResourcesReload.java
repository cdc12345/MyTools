package org.cdc.toolbox.parser;

import org.cdc.toolbox.interfaces.IArgParser;
import org.cdc.toolbox.uitls.MinecraftUtils;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FORGEResourcesReload implements IArgParser {
	@Override public void parse(Instrumentation instrumentation, String... arg) {
		new NEOFORGECommandExecutor().parse(instrumentation, "", "reload");

		if (arg.length == 2 && arg[1].equals("reloadPacks")) {
			CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(() -> {
				try {
					Class<?> cls = MinecraftUtils.getMinecraftClassLoader().loadClass("net.minecraft.client.Minecraft");
					Field field = cls.getDeclaredField("instance");
					field.setAccessible(true);
					Object mineInstance = field.get(null);
					cls.getMethod("reloadResourcePacks").invoke(mineInstance);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}
	}
}
