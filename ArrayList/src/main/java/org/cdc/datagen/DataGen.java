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
		MCreatorPluginFactory mCreatorPluginFactory = new MCreatorPluginFactory(
				new File("src/main/resources").getAbsoluteFile());
		var en = mCreatorPluginFactory.createDefaultLanguage();
		var zh = mCreatorPluginFactory.createLanguage(Locale.CHINA);

		ColorUtils.putSuggestColor("list", "40");

		//datalist
		mCreatorPluginFactory.createDataList("supportedtypes").appendElement("Text", List.of("String", "\"\"\"\""))
				.appendElement("Number", List.of("Double", "\"0\"")).appendElement("Entity", List.of("Entity", "null"))
				.appendElement("BlockState", List.of("BlockState", "Blocks.AIR.defaultBlockState()"))
				.appendElement("Logic", List.of("Boolean", "\"false\"")).setMessageLocalization(en,"Select a type").setDefault().initGenerator().buildAndOutput();
		mCreatorPluginFactory.createDataList("types").appendElement("objectlist", "ArrayList<Object>").initGenerator()
				.build();

		//ini
		mCreatorPluginFactory.createProcedureCategory(ListCategory.INSTANCE.getName()).setColor(40)
				.setLanguage(en, "Lists").buildAndOutput();
		mCreatorPluginFactory.createVariable().setName("objectlist").setBlocklyVariableType("ObjectList").setColor(40)
				.setNullable(false).setIgnoredByCoverage(true).initGenerator().buildAndOutput();

		//list management
		mCreatorPluginFactory.getToolKit().createInputProcedure("list_add").setColor(40)
				.appendArgs0InputValue("list", "ObjectList").appendArgs0InputValue("element", (String) null, true)
				.toolBoxInitBuilder().setName("element").appendConstantString("helloworld").buildAndReturn()
				.setLanguage(en, "add element %2 to %1").setToolBoxId(ListCategory.INSTANCE).initGenerator()
				.buildAndOutput();
		mCreatorPluginFactory.getToolKit().createInputProcedure("list_clear")
				.appendArgs0InputValue("list", "ObjectList").setLanguage(en, "clear list %1").initGenerator()
				.buildAndOutput();
		mCreatorPluginFactory.getToolKit().createOutputProcedure("list_get", (String) null)
				.appendArgs0InputValue("list", "ObjectList").appendArgs0InputValue("index", BuiltInTypes.Number)
				.appendArgs0FieldDataListSelector("type", "supportedtypes", "String").toolBoxInitBuilder()
				.setName("index").appendConstantNumber(0).buildAndReturn()
				.setPlaceHolderLanguage(en, "get element index %index from %list, type: %type").initGenerator()
				.buildAndOutput();
		mCreatorPluginFactory.getToolKit().createOutputProcedure("list_check_type", BuiltInTypes.Boolean)
				.appendArgs0InputValue("index", BuiltInTypes.Number)
				.appendArgs0InputValue("list", ObjectListType.INSTANCE)
				.appendArgs0FieldDataListSelector("type", "supportedtypes", "String")
				.setColor(BuiltInBlocklyColor.LOGIC.toString()).toolBoxInitBuilder().setName("index")
				.appendConstantNumber(0).buildAndReturn()
				.setPlaceHolderLanguage(en, "Element index %index from %list is type %type")
				.setPlaceHolderLanguage(zh, "列表%list中的第%index是%type").initGenerator().buildAndOutput();
		mCreatorPluginFactory.getToolKit().createInputProcedure("list_remove")
				.appendArgs0InputValue("index", BuiltInTypes.Number).appendArgs0InputValue("list", "ObjectList")
				.toolBoxInitBuilder().setName("index").appendConstantNumber(0).buildAndReturn()
				.setPlaceHolderLanguage(en, "remove index %index from %list")
				.setPlaceHolderLanguage(zh, "移除列表%list中第%index的元素").initGenerator().buildAndOutput();
		mCreatorPluginFactory.getToolKit().createInputProcedure("list_set")
				.appendArgs0InputValue("index", BuiltInTypes.Number)
				.appendArgs0InputValue("element", (String) null, true).appendArgs0InputValue("list", "ObjectList")
				.toolBoxInitBuilder().setName("index").appendConstantNumber(0).buildAndReturn().toolBoxInitBuilder()
				.setName("element").appendConstantString("element").buildAndReturn()
				.setPlaceHolderLanguage(en, "set index %index to %element list: %list")
				.setPlaceHolderLanguage(zh, "设置列表%list中的第%index为%element").initGenerator().buildAndOutput();
		mCreatorPluginFactory.getToolKit().createOutputProcedure("list_size", BuiltInTypes.Number)
				.appendArgs0InputValue("list", ObjectListType.INSTANCE).initGenerator()
				.setPlaceHolderLanguage(en, "get size of %list").buildAndOutput();
		mCreatorPluginFactory.getToolKit().createInputProcedure("list_reverse")
				.appendArgs0InputValue("list", ObjectListType.INSTANCE).setPlaceHolderLanguage(en, "reverse %list")
				.setPlaceHolderLanguage(zh, "反转列表%list").initGenerator().buildAndOutput();

		mCreatorPluginFactory.getToolKit().createOutputProcedure("list_get_advanced", (String) null)
				.appendArgs0InputValue("list", "ObjectList").appendArgs0InputValue("index", BuiltInTypes.Number)
				.appendArgs0InputValue("type", BuiltInTypes.String).appendArgs0InputValue("defaultValue", (String) null)
				.toolBoxInitBuilder().setName("index").appendConstantNumber(0).buildAndReturn().toolBoxInitBuilder()
				.setName("type").appendConstantString("java.lang.String").buildAndReturn()
				.setPlaceHolderLanguage(en, "get element index %index from %list, type: %type, default %defaultValue")
				.initGenerator().buildAndOutput();
		mCreatorPluginFactory.getToolKit().createOutputProcedure("list_get_allnum", ObjectListType.INSTANCE)
				.appendArgs0InputValue("list", ObjectListType.INSTANCE)
				.setLanguage(en, "get all numbers from list %1 and sort")
				.setPlaceHolderLanguage(zh, "从列表%list获得所有数字并整理").initGenerator().buildAndOutput();
		mCreatorPluginFactory.getToolKit().createOutputProcedure("list_contains", BuiltInTypes.Boolean)
				.setColor(BuiltInBlocklyColor.LOGIC.toString()).appendArgs0InputValue("list", ObjectListType.INSTANCE)
				.appendArgs0InputValue("value", (String) null).setPlaceHolderLanguage(en, "list %list contains %value")
				.setPlaceHolderLanguage(zh, "列表%list包含%value").initGenerator().buildAndOutput();

		//math
		mCreatorPluginFactory.getToolKit().createInputProcedure("number_plus_one")
				.setColor(BuiltInBlocklyColor.MATH.toString()).appendArgs0InputValue("number", BuiltInTypes.Number)
				.setToolBoxId(BuiltInToolBoxId.Procedure.MATH).setLanguage(en, "number %1 + 1")
				.setLanguage(zh, "让数字变量%1加一").initGenerator().buildAndOutput();

		mCreatorPluginFactory.initGenerator(Generators.NEOFORGE1214);
		mCreatorPluginFactory.getToolKit().clearGenerator();
		mCreatorPluginFactory.initGenerator(Generators.NEOFORGE1211);
		mCreatorPluginFactory.getToolKit().clearGenerator();
		mCreatorPluginFactory.initGenerator(Generators.FORGE1201);
		mCreatorPluginFactory.getToolKit().clearGenerator();
		mCreatorPluginFactory.initGenerator(Generators.SPIGOT1214);
		mCreatorPluginFactory.getToolKit().clearGenerator();


		en.buildAndOutput();
		zh.buildAndOutput();
	}
}
