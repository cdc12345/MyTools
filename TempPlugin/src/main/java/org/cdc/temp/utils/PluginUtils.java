package org.cdc.temp.utils;

import net.mcreator.java.JavaConventions;
import net.mcreator.util.StringUtils;
import org.cdc.framework.builder.DataListBuilder;
import org.cdc.temp.TempPluginMain;
import org.cdc.temp.element.TempAchievement;
import org.cdc.temp.element.TempBiome;
import org.cdc.temp.element.TempItem;
import org.cdc.temp.element.TempStructure;

import java.util.List;

public class PluginUtils {

	private static DataListBuilder blocksitems;
	private static DataListBuilder achievements;
	private static DataListBuilder structures;
	private static DataListBuilder biomes;

	public static Runnable doCreateItem(TempItem generatableElement) {
		String readableName = generatableElement.readableName;
		String type = generatableElement.type;
		String code = generatableElement.code;
		String registryName = generatableElement.registryName;
		if (readableName == null || type == null || registryName == null) {
			return () -> {};
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
		var bui = blocksitems.appendElement(String.format("""
						%s: 
						  readable_name: "%s"
						  type: %s
						""", StringUtils.uppercaseFirstLetter(type) + "s" + "." + JavaConventions.convertToValidClassName(
						readableName), readableName, type),

				List.of(code, registryName)).initGenerator();
		bui.buildAndOutput();
		return () -> bui.redo.accept(null);
	}

	public static Runnable doCreateAchievement(TempAchievement tempAchievement) {
		String readable_name = tempAchievement.readable_name();
		String path = tempAchievement.registry_name();
		if (path == null || readable_name == null) {
			return () -> {};
		}

		if (readable_name.isEmpty()) {
			readable_name = null;
		}

		if (achievements == null) {
			achievements = TempPluginMain.getInstance().getmCreatorPluginFactory().createDataList("achievements");
		}

		var path1 = PathUtils.getPath(path);
		var bui = achievements.appendElement(path1, readable_name, List.of(path)).initGenerator();
		bui.buildAndOutput();
		return () -> bui.redo.accept(null);
	}

	public static Runnable doCreateStructure(TempStructure tempStructure) {
		String readable_name = tempStructure.readable_name();
		String path = tempStructure.registry_name();
		if (path == null || readable_name == null) {
			return () -> {};
		}

		if (readable_name.isEmpty()) {
			readable_name = null;
		}

		if (structures == null) {
			structures = TempPluginMain.getInstance().getmCreatorPluginFactory().createDataList("structures");
		}

		var path1 = PathUtils.getPath(path);
		var bui = structures.appendElement(path1, readable_name, List.of(path)).initGenerator();
		bui.buildAndOutput();
		return () -> bui.redo.accept(null);
	}

	public static Runnable doCreateBiome(TempBiome tempElement) {
		String readable_name = tempElement.readable_name();
		String path = tempElement.registry_name();
		if (path == null || readable_name == null) {
			return () -> {};
		}

		if (readable_name.isEmpty()) {
			readable_name = null;
		}

		if (biomes == null) {
			biomes = TempPluginMain.getInstance().getmCreatorPluginFactory().createDataList("biomes");
		}

		var path1 = PathUtils.getPath(path);
		var bui = biomes.appendElement(path1, readable_name, List.of(path)).initGenerator();
		bui.buildAndOutput();
		return () -> bui.redo.accept(null);
	}
}
