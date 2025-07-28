package org.cdc.datagen;

import org.cdc.datagen.categories.ListCategory;
import org.cdc.datagen.types.ObjectListType;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.*;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class DataGen {
	public static void main(String[] args) {
		MCreatorPluginFactory plugin = new MCreatorPluginFactory(new File("src/main/resources").getAbsoluteFile());
		var en = plugin.createDefaultLanguage();
		var zh = plugin.createLanguage(Locale.CHINA);

		ColorUtils.putSuggestColor("list", "40");

		//datalist
		plugin.createDataList("supportedtypes").appendElement("Text", List.of("String", "\"\\\"\\\"\""))
				.appendElement("Number", List.of("Double", "\"0\"")).appendElement("Entity", List.of("Entity", "null"))
				.appendElement("BlockState", List.of("BlockState", "Blocks.AIR.defaultBlockState()"))
				.appendElement("Logic", List.of("Boolean", "\"false\""))
				.appendElement("Direction", List.of("Direction", "\"Direction.NORTH\""))
				.appendElement("DamageSource",List.of("DamageSource","null"))
				.appendElement("ItemStack",List.of("ItemStack","ItemStack.EMPTY"))
				.setMessageLocalization(en, "Select a type").setMessageLocalization(zh, "选择一个类型").setDefault()
				.initGenerator().buildAndOutput();
		plugin.createDataList("types").appendElement("objectlist", "ArrayList<Object>").initGenerator().build();

		//ini
		plugin.createProcedureCategory(ListCategory.INSTANCE.getName()).setColor(40).setLanguage(en, "Lists")
				.setLanguage(zh, "列表").buildAndOutput();
		plugin.createVariable().setName("objectlist").setBlocklyVariableType("ObjectList").setColor(40)
				.setNullable(false).setIgnoredByCoverage(true).initGenerator().buildAndOutput();

		//list management
		plugin.getToolKit().createInputProcedure("list_add").setColor(40).appendArgs0InputValue("list", "ObjectList")
				.appendArgs0InputValue("element", (String) null, true).toolBoxInitBuilder().setName("element")
				.appendConstantString("helloworld").buildAndReturn().setLanguage(en, "add element %2 to %1")
				.setLanguage(zh, "添加元素 %2 到列表 %1").setToolBoxId(ListCategory.INSTANCE).initGenerator()
				.buildAndOutput();
		plugin.getToolKit().createInputProcedure("list_clear").appendArgs0InputValue("list", "ObjectList")
				.setLanguage(en, "clear list %1").setLanguage(zh, "清空列表%1").initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("list_get", (String) null).appendArgs0InputValue("list", "ObjectList")
				.appendArgs0InputValue("index", BuiltInTypes.Number)
				.appendArgs0FieldDataListSelector("type", "supportedtypes", "String").toolBoxInitBuilder()
				.setName("index").appendConstantNumber(0).buildAndReturn()
				.setPlaceHolderLanguage(en, "get element index %index from %list, type: %type").initGenerator()
				.setPlaceHolderLanguage(en, "得到元素, 索引 %index 列表 %list 类型: %type").buildAndOutput();
		plugin.getToolKit().createOutputProcedure("list_check_type", BuiltInTypes.Boolean)
				.appendArgs0InputValue("index", BuiltInTypes.Number)
				.appendArgs0InputValue("list", ObjectListType.INSTANCE)
				.appendArgs0FieldDataListSelector("type", "supportedtypes", "String")
				.setColor(BuiltInBlocklyColor.LOGIC.toString()).toolBoxInitBuilder().setName("index")
				.appendConstantNumber(0).buildAndReturn()
				.setPlaceHolderLanguage(en, "Element index %index from %list is type %type")
				.setPlaceHolderLanguage(zh, "列表%list中的第%index是%type").initGenerator().buildAndOutput();
		plugin.getToolKit().createInputProcedure("list_remove").appendArgs0InputValue("index", BuiltInTypes.Number)
				.appendArgs0InputValue("list", "ObjectList").toolBoxInitBuilder().setName("index")
				.appendConstantNumber(0).buildAndReturn().setPlaceHolderLanguage(en, "remove index %index from %list")
				.setPlaceHolderLanguage(zh, "移除列表%list中第%index的元素").initGenerator().buildAndOutput();
		plugin.getToolKit().createInputProcedure("list_set").appendArgs0InputValue("index", BuiltInTypes.Number)
				.appendArgs0InputValue("element", (String) null, true).appendArgs0InputValue("list", "ObjectList")
				.toolBoxInitBuilder().setName("index").appendConstantNumber(0).buildAndReturn().toolBoxInitBuilder()
				.setName("element").appendConstantString("element").buildAndReturn()
				.setPlaceHolderLanguage(en, "set index %index to %element list: %list")
				.setPlaceHolderLanguage(zh, "设置列表%list中的第%index为%element").initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("list_size", BuiltInTypes.Number)
				.appendArgs0InputValue("list", ObjectListType.INSTANCE).initGenerator()
				.setPlaceHolderLanguage(en, "get size of %list").setPlaceHolderLanguage(zh, "列表 %list 的长度")
				.buildAndOutput();
		plugin.getToolKit().createInputProcedure("list_reverse").appendArgs0InputValue("list", ObjectListType.INSTANCE)
				.setPlaceHolderLanguage(en, "reverse %list").setPlaceHolderLanguage(zh, "反转列表%list").initGenerator()
				.buildAndOutput();
		plugin.getToolKit().createInputProcedure("list_merge").appendArgs0InputValue("origin", ObjectListType.INSTANCE)
				.appendArgs0InputValue("target", ObjectListType.INSTANCE)
				.setPlaceHolderLanguage(en, "%origin merge with %target")
				.setPlaceHolderLanguage(zh, "%origin与%target合并").initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("list_contains", BuiltInTypes.Boolean)
				.setColor(BuiltInBlocklyColor.LOGIC.toString()).appendArgs0InputValue("list", ObjectListType.INSTANCE)
				.appendArgs0InputValue("value", (String) null).setPlaceHolderLanguage(en, "list %list contains %value")
				.setPlaceHolderLanguage(zh, "列表%list包含%value").initGenerator().buildAndOutput();

		plugin.getToolKit().createOutputProcedure("list_get_advanced", (String) null)
				.appendArgs0InputValue("list", "ObjectList").appendArgs0InputValue("index", BuiltInTypes.Number)
				.appendArgs0InputValue("type", BuiltInTypes.String).appendArgs0InputValue("defaultValue", (String) null)
				.toolBoxInitBuilder().setName("index").appendConstantNumber(0).buildAndReturn().toolBoxInitBuilder()
				.setName("type").appendConstantString("java.lang.String").buildAndReturn()
				.setPlaceHolderLanguage(en, "get element index %index from %list, type: %type, default %defaultValue")
				.initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("list_get_allnum", ObjectListType.INSTANCE)
				.appendArgs0InputValue("list", ObjectListType.INSTANCE)
				.setLanguage(en, "get all numbers from list %1 and sort")
				.setPlaceHolderLanguage(zh, "从列表%list获得所有数字并整理").initGenerator().buildAndOutput();

		//math
		plugin.getToolKit().createInputProcedure("number_plus_one").setColor(BuiltInBlocklyColor.MATH.toString())
				.appendArgs0InputValue("number", BuiltInTypes.Number).setToolBoxId(BuiltInToolBoxId.Procedure.MATH)
				.setLanguage(en, "number variable %1 + 1").setLanguage(zh, "让数字变量%1加一").initGenerator()
				.buildAndOutput();

		plugin.initGenerator(Generators.NEOFORGE1214);
		plugin.getToolKit().clearGenerator();
		plugin.initGenerator(Generators.NEOFORGE1211);
		plugin.getToolKit().clearGenerator();
		plugin.initGenerator(Generators.FORGE1201);
		plugin.getToolKit().clearGenerator();
		plugin.initGenerator(Generators.SPIGOT1214);
		plugin.getToolKit().clearGenerator();

		en.buildAndOutput();
		zh.buildAndOutput();

	}
}
