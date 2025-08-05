package org.cdc.datagen;

import org.cdc.datagen.categories.ListCategory;
import org.cdc.datagen.categories.MapsCategory;
import org.cdc.datagen.types.ArrayListsType;
import org.cdc.datagen.types.ObjectListType;
import org.cdc.datagen.types.ObjectMapType;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.*;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class CollectionsDataGen {
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
				.appendElement("DamageSource", List.of("DamageSource", "null"))
				.appendElement("ItemStack", List.of("ItemStack", "ItemStack.EMPTY"))
				.appendElement("ObjectList", List.of("ArrayList", "null"))
				.appendElement("ObjectMap", List.of("HashMap", "null")).setMessageLocalization(en, "Select a type")
				.setMessageLocalization(zh, "选择一个类型").setDefault().initGenerator().buildAndOutput();
		plugin.createDataList("types").appendElement("objectlist", "ArrayList<Object>")
				.appendElement("objectmap", "HashMap<String,Object>").initGenerator().build();

		//ini
		plugin.createProcedureCategory(MapsCategory.INSTANCE.getName()).setColor(45).setLanguage(en, "Maps")
				.setLanguage(zh, "字典").buildAndOutput();
		plugin.createProcedureCategory(ListCategory.INSTANCE.getName()).setColor(40).setLanguage(en, "Lists")
				.setLanguage(zh, "列表").buildAndOutput();
		plugin.createVariable().setName("objectlist").setBlocklyVariableType("ObjectList").setColor(40)
				.setNullable(false).setIgnoredByCoverage(true).setSetterText(en, "set objectList")
				.setGetterText(en, "get objectList").setReturnText(en, "return objectList")
				.setCustomDependency(en, "ObjectList dependency")
				.setCallProcedureRetval(en, "Call procedure and get objectList return value")
				.setSetterText(zh, "设objectList").setGetterText(zh, "得到objectList").initGenerator().buildAndOutput();
		plugin.createVariable().setName("objectmap").setBlocklyVariableType("ObjectMap").setColor(40).setNullable(false)
				.setIgnoredByCoverage(true).setSetterText(en, "set objectMap").setGetterText(en, "get objectMap")
				.setReturnText(en, "return objectMap").setCustomDependency(en, "ObjectMap dependency")
				.setCallProcedureRetval(en, "Call procedure and get objectMap return value")
				.setSetterText(zh, "设objectMap").setGetterText(zh, "得到objectMap").initGenerator().buildAndOutput();

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
				.setPlaceHolderLanguage(zh, "得到元素, 索引 %index 列表 %list 类型: %type").buildAndOutput();
		plugin.getToolKit().createOutputProcedure("list_check_type", BuiltInTypes.Boolean)
				.appendArgs0InputValue("index", BuiltInTypes.Number)
				.appendArgs0InputValue("list", ObjectListType.INSTANCE)
				.appendArgs0FieldDataListSelector("type", "supportedtypes", "String")
				.setColor(BuiltInBlocklyColor.LOGIC.toString()).toolBoxInitBuilder().setName("index")
				.appendConstantNumber(0).buildAndReturn()
				.setPlaceHolderLanguage(en, "is element index %index in %list type %type")
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
				.setColor(BuiltInBlocklyColor.MATH.toString()).appendArgs0InputValue("list", ObjectListType.INSTANCE)
				.initGenerator().setPlaceHolderLanguage(en, "get size of %list")
				.setPlaceHolderLanguage(zh, "列表 %list 的长度").buildAndOutput();
		plugin.getToolKit().createInputProcedure("list_reverse").appendArgs0InputValue("list", ObjectListType.INSTANCE)
				.setPlaceHolderLanguage(en, "reverse %list").setPlaceHolderLanguage(zh, "反转列表%list").initGenerator()
				.buildAndOutput();
		plugin.getToolKit().createInputProcedure("list_merge").appendArgs0InputValue("origin", ObjectListType.INSTANCE)
				.appendArgs0InputValue("target", ObjectListType.INSTANCE)
				.setPlaceHolderLanguage(en, "%origin add all from %target")
				.setPlaceHolderLanguage(zh, "给%origin添加所有%target的元素").initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("list_contains", BuiltInTypes.Boolean)
				.setColor(BuiltInBlocklyColor.LOGIC.toString()).appendArgs0InputValue("list", ObjectListType.INSTANCE)
				.appendArgs0InputValue("value", (String) null).toolBoxInitBuilder().setName("value")
				.appendConstantString("value").buildAndReturn().setPlaceHolderLanguage(en, "list %list contains %value")
				.setPlaceHolderLanguage(zh, "列表%list包含%value").initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("list_is_empty", BuiltInTypes.Boolean)
				.setColor(BuiltInBlocklyColor.LOGIC.toString()).appendArgs0InputValue("list", ObjectListType.INSTANCE)
				.setLanguage(en, "is %1 empty").initGenerator().buildAndOutput();

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
		plugin.getToolKit().createOutputProcedure("index_of_list", BuiltInTypes.Number)
				.setColor(BuiltInBlocklyColor.MATH.toString()).appendArgs0FieldInput("mark", "1")
				.setLanguage(en, "Index %1").initGenerator().buildAndOutput();
		plugin.getToolKit().createInputProcedure("list_for_each").setToolBoxId(ListCategory.INSTANCE)
				.appendArgs0InputValue("for_list", ObjectListType.INSTANCE)
				.appendArgs0InputValue("_placeholder", BuiltInTypes.Number, true).toolBoxInitBuilder()
				.setName("_placeholder").appendReferenceBlock("index_of_list").buildAndReturn()
				.appendExtension(BuiltInExtensions.IS_CUSTOM_LOOP).appendArgs0StatementInput("for_each")
				.statementBuilder().setName("for_each").buildAndReturn().setLanguage(en, "for each %1 %2 %3")
				.initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("list_split_string", ObjectListType.INSTANCE)
				.setToolBoxId(ListCategory.INSTANCE)
				.appendArgs0InputValueWithDefaultToolboxInit("text", BuiltInTypes.String)
				.appendArgs0InputValue("seperator", BuiltInTypes.String).toolBoxInitBuilder().setName("seperator")
				.appendConstantString(",").buildAndReturn().setLanguage(en, "split %1 using %2")
				.setLanguage(zh, "使用%2分割%1").initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("list_index_of", BuiltInTypes.Number)
				.setToolBoxId(ListCategory.INSTANCE).setColor(BuiltInBlocklyColor.MATH.toString())
				.appendArgs0InputValue("list", ObjectListType.INSTANCE).appendArgs0InputValue("value", (String) null)
				.toolBoxInitBuilder().setName("value").appendConstantString("element").buildAndReturn()
				.setLanguage(en, "get index of %2 in list %1 or -1 if value not exist")
				.setLanguage(zh, "%2如果在%1那么返回其索引否则-1").initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("list_last_index_of", BuiltInTypes.Number)
				.setToolBoxId(ListCategory.INSTANCE).setColor(BuiltInBlocklyColor.MATH.toString())
				.appendArgs0InputValue("list", ObjectListType.INSTANCE).appendArgs0InputValue("value", (String) null)
				.toolBoxInitBuilder().setName("value").appendConstantString("element").buildAndReturn()
				.setLanguage(en, "get last index of %2 in list %1 or -1 if value not exist")
				.setLanguage(zh, "%2如果在%1那么返回其最后所在的索引否则-1").initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("list_compatible_with_arraylists", ArrayListsType.INSTANCE)
				.setToolBoxId(ListCategory.INSTANCE).setColor(300)
				.appendArgs0InputValue("objectlist_var", ObjectListType.INSTANCE)
				.setLanguage(en, "ArrayListsSupport: to ArrayLists %1").initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("arraylists_compatible_with_objectslist", ObjectListType.INSTANCE)
				.setToolBoxId(ListCategory.INSTANCE).appendArgs0InputValue("arraylists_var", ArrayListsType.INSTANCE)
				.setLanguage(en, "ArrayListsSupport: to ObjectList %1").initGenerator().buildAndOutput();
		//map
		plugin.getToolKit().createInputProcedure("map_put").setToolBoxId(MapsCategory.INSTANCE).setColor(45)
				.appendArgs0InputValue("map", ObjectMapType.INSTANCE)
				.appendArgs0InputValue("map_key", BuiltInTypes.String, true).toolBoxInitBuilder().setName("map_key")
				.appendConstantString("Map key").buildAndReturn().appendArgs0InputValue("map_value", (String) null)
				.toolBoxInitBuilder().setName("map_value").appendConstantString("Map value").buildAndReturn()
				.setLanguage(en, "put key %2 value %3 to %1").setLanguage(zh, "添加映射 %2 -> %3 到表 %1")
				.initGenerator().buildAndOutput();
		plugin.getToolKit().createInputProcedure("map_clear").appendArgs0InputValue("map", ObjectMapType.INSTANCE)
				.setLanguage(en, "clear map %1").setLanguage(zh, "清空字典%1").initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("map_get", (String) null)
				.appendArgs0InputValue("map", ObjectMapType.INSTANCE)
				.appendArgs0InputValue("map_key", BuiltInTypes.String).toolBoxInitBuilder().setName("map_key")
				.appendConstantString("Map key").buildAndReturn()
				.appendArgs0FieldDataListSelector("type", "supportedtypes", "String")
				.setPlaceHolderLanguage(en, "get value key %map_key from %map, type: %type").initGenerator()
				.setPlaceHolderLanguage(zh, "得到值, 键%map_key 来源：%map 类型：%type").buildAndOutput();
		plugin.getToolKit().createInputProcedure("map_remove").appendArgs0InputValue("map_key", BuiltInTypes.String)
				.appendArgs0InputValue("map", ObjectMapType.INSTANCE).toolBoxInitBuilder().setName("map_key")
				.appendConstantString("Map key").buildAndReturn()
				.setPlaceHolderLanguage(en, "remove key %map_key from %map")
				.setPlaceHolderLanguage(zh, "移除字典%map中键%map_key的映射").initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("map_size", BuiltInTypes.Number)
				.setColor(BuiltInBlocklyColor.MATH.toString()).appendArgs0InputValue("map", ObjectMapType.INSTANCE)
				.initGenerator().setPlaceHolderLanguage(en, "get size of %map")
				.setPlaceHolderLanguage(zh, "字典 %map 的长度").buildAndOutput();
		plugin.getToolKit().createOutputProcedure("map_contains_key", BuiltInTypes.Boolean)
				.setColor(BuiltInBlocklyColor.LOGIC.toString()).appendArgs0InputValue("map", ObjectMapType.INSTANCE)
				.appendArgs0InputValue("map_key", BuiltInTypes.String).toolBoxInitBuilder().setName("map_key")
				.appendConstantString("Map key").buildAndReturn()
				.setPlaceHolderLanguage(en, "map %map contains key %map_key")
				.setPlaceHolderLanguage(zh, "列表%map存在键%map_key的映射").initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("map_is_empty", BuiltInTypes.Boolean)
				.setColor(BuiltInBlocklyColor.LOGIC.toString()).appendArgs0InputValue("map", ObjectMapType.INSTANCE)
				.setLanguage(en, "is %1 empty").initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("map_get_advanced", (String) null)
				.appendArgs0InputValue("map", ObjectMapType.INSTANCE)
				.appendArgs0InputValue("map_key", BuiltInTypes.String)
				.appendArgs0InputValue("type", BuiltInTypes.String).appendArgs0InputValue("defaultValue", (String) null)
				.toolBoxInitBuilder().setName("map_key").appendConstantString("Map key").buildAndReturn()
				.toolBoxInitBuilder().setName("type").appendConstantString("java.lang.String").buildAndReturn()
				.setPlaceHolderLanguage(en,
						"get value through key %map_key from %map, type: %type, default %defaultValue").initGenerator()
				.buildAndOutput();
		plugin.getToolKit().createOutputProcedure("key_of_map", BuiltInTypes.String)
				.setColor(BuiltInBlocklyColor.TEXTS.toString()).appendArgs0FieldInput("mark", "1")
				.setLanguage(en, "Key %1").initGenerator().buildAndOutput();
		plugin.getToolKit().createInputProcedure("map_for_each").setColor(45)
				.appendArgs0InputValue("for_map", ObjectMapType.INSTANCE)
				.appendArgs0InputValue("_placeholder", BuiltInTypes.String, true).toolBoxInitBuilder()
				.setName("_placeholder").appendReferenceBlock("key_of_map").buildAndReturn()
				.appendExtension(BuiltInExtensions.IS_CUSTOM_LOOP).appendArgs0StatementInput("for_each")
				.statementBuilder().setName("for_each").buildAndReturn().setLanguage(en, "for each %1 %2 %3")
				.initGenerator().buildAndOutput();
		plugin.getToolKit().createInputProcedure("map_putall").setToolBoxId(MapsCategory.INSTANCE).setColor(45)
				.appendArgs0InputValue("receiver", ObjectMapType.INSTANCE)
				.appendArgs0InputValue("source", ObjectMapType.INSTANCE).setLanguage(en, "put all entry from %2 to %1")
				.setLanguage(zh, "将%2所有的映射推入%1").initGenerator().buildAndOutput();


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
