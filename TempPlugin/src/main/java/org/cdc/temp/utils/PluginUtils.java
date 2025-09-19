package org.cdc.temp.utils;

import net.mcreator.java.JavaConventions;
import net.mcreator.util.StringUtils;
import org.cdc.framework.builder.DataListBuilder;
import org.cdc.temp.TempPluginMain;
import org.cdc.temp.element.TempItem;

import java.util.List;

public class PluginUtils {

	private static DataListBuilder blocksitems;

	public static void doCreateItem(TempItem generatableElement) {
		String readableName = generatableElement.readableName;
		String type = generatableElement.type;
		String code = generatableElement.code;
		String registryName = generatableElement.registryName;
		if (readableName == null || type == null || registryName == null) {
			return;
		}

		if (blocksitems == null) {
			blocksitems = TempPluginMain.getInstance().getmCreatorPluginFactory().createDataList()
					.setName("blocksitems");
		}

		if (code.isEmpty()) {
			if ("item".equals(type)) {
				code = "Items.AIR";
			} else if ("block".equals(type)) {
				code = "Blocks.AIR";
			} else {
				code = "Null";
			}
		}
		var arr = registryName.split(":");
		code = String.format(code, arr[0], arr[1]);
		blocksitems.appendElement(String.format("""
						%s: 
						  readable_name: "%s"
						  type: %s
						""", StringUtils.uppercaseFirstLetter(type) + "s" + "." + JavaConventions.convertToValidClassName(
						readableName), readableName, type),null,

				List.of(code, registryName)).initGenerator().buildAndOutput();

	}
}
