package org.cdc.datagen;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.BuiltInTypes;
import org.cdc.framework.utils.Generators;

import java.util.List;

public class DataGen {
	public static void main(String[] args) {
		MCreatorPluginFactory mCreatorPluginFactory = MCreatorPluginFactory.createFactory(
				"ArrayList/src/main/resources");
		var en = mCreatorPluginFactory.createDefaultLanguage();

		mCreatorPluginFactory.createDataList("supportedtypes").appendElement("Text", List.of("String", "\"\"\"\""))
				.appendElement("Number", List.of("Double", "\"0\"")).appendElement("Entity", List.of("Entity", "null"))
				.appendElement("BlockState", List.of("BlockState", "Blocks.AIR.defaultBlockState()"))
				.appendElement("Logic", List.of("Boolean", "\"false\"")).setDefault().initGenerator().buildAndOutput();
		mCreatorPluginFactory.createDataList("types").appendElement("objectlist", "ArrayList<Object>").initGenerator()
				.build();

		mCreatorPluginFactory.createProcedureCategory("list").setColor(60).setLanguage(en, "Lists").buildAndOutput();
		mCreatorPluginFactory.createVariable().setName("objectlist").setBlocklyVariableType("ObjectList")
				.setNullable(false).setIgnoredByCoverage(true).initGenerator().buildAndOutput();

		mCreatorPluginFactory.getToolKit().createInputProcedure("list_add").setColor(40)
				.appendArgs0InputValue("list", "ObjectList").appendArgs0InputValue("element", (String) null, true)
				.toolBoxInitBuilder().setName("element").appendConstantString("helloworld").buildAndReturn()
				.setLanguage(en, "add element %2 to %1").setToolBoxId("list").initGenerator().buildAndOutput();
		mCreatorPluginFactory.getToolKit().createInputProcedure("list_clear")
				.appendArgs0InputValue("list", "ObjectList").setLanguage(en, "clear list %1").initGenerator()
				.buildAndOutput();
		mCreatorPluginFactory.getToolKit().createOutputProcedure("list_get", (String) null)
				.appendArgs0InputValue("list", "ObjectList").appendArgs0InputValue("index", BuiltInTypes.Number)
				.appendArgs0FieldDataListSelector("type", "supportedtypes", "String").toolBoxInitBuilder()
				.setName("index").appendConstantNumber(0).buildAndReturn()
				.setLanguage(en, "get element index %2 from %1, type: %3").initGenerator().buildAndOutput();
		mCreatorPluginFactory.getToolKit().createInputProcedure("list_remove")
				.appendArgs0InputValue("index", BuiltInTypes.Number).appendArgs0InputValue("list", "ObjectList")
				.toolBoxInitBuilder().setName("index").appendConstantNumber(0).buildAndReturn()
				.setLanguage(en, "remove index %1 from %2").initGenerator().buildAndOutput();
		mCreatorPluginFactory.getToolKit().createInputProcedure("list_set")
				.appendArgs0InputValue("index", BuiltInTypes.Number)
				.appendArgs0InputValue("element", (String) null, true).appendArgs0InputValue("list", "ObjectList")
				.toolBoxInitBuilder().setName("index").appendConstantNumber(0).buildAndReturn()
				.setLanguage(en, "set index %1 to %2 list: %3").initGenerator().buildAndOutput();
		mCreatorPluginFactory.getToolKit().createOutputProcedure("list_size", BuiltInTypes.Number)
				.appendArgs0InputValue("list", "ObjectList").initGenerator().setLanguage(en, "get size of %1")
				.buildAndOutput();



		mCreatorPluginFactory.initGenerator(Generators.NEOFORGE1214);
		mCreatorPluginFactory.getToolKit().clearGenerator();
		mCreatorPluginFactory.initGenerator(Generators.NEOFORGE1211);
		mCreatorPluginFactory.getToolKit().clearGenerator();
		mCreatorPluginFactory.initGenerator(Generators.FORGE1201);
		mCreatorPluginFactory.getToolKit().clearGenerator();

		en.buildAndOutput();

	}
}
