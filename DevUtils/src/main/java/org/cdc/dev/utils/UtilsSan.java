package org.cdc.dev.utils;

import net.mcreator.plugin.JavaPlugin;
import net.mcreator.plugin.events.ModifyTemplateResultEvent;

import java.util.regex.Pattern;

public class UtilsSan {

	private static Pattern definePlusSelf = Pattern.compile("=\\s+/\\*@PlusSelf\\*/");

	public static void registerPlusSelf(JavaPlugin javaPlugin) {
		javaPlugin.addListener(ModifyTemplateResultEvent.class, event -> {
			if (event.getTemplateOutput().contains("/*@PlusSelf*/")) {
				var matcher = definePlusSelf.matcher(event.getTemplateOutput());
				StringBuilder stringBuffer = new StringBuilder();
				if (matcher.find()) {
					do {
						matcher.appendReplacement(stringBuffer, "+=");
					} while (matcher.find());

					matcher.appendTail(stringBuffer);
					event.setTemplateOutput(stringBuffer.toString());
				}
			}
		});
	}
}
