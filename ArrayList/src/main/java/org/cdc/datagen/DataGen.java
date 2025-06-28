package org.cdc.datagen;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.BuiltInTypes;
import org.cdc.framework.utils.Generators;

public class DataGen {
	public static void main(String[] args) {
		MCreatorPluginFactory mCreatorPluginFactory = MCreatorPluginFactory.createFactory(
				"ArrayList/src/main/resources");
		var en = mCreatorPluginFactory.createDefaultLanguage();

		mCreatorPluginFactory.createDataList("supportedtypes").appendElement("Text", """
								
				- String
				- "\"\"\"
				""").appendElement("Number", """
								
				- Double
				- 0""").appendElement("Entity", """
				    			
					- Entity
					- null""").appendElement("BlockState", """
				
				- BlockState
				- Blocks.AIR.defaultBlockState()
				""").appendElement("Boolean", """
				
				- Boolean
				- "false"
				""").setDefault("""
								
				- String
				- "\"\"\"
				""").initGenerator().buildAndOutput();

		mCreatorPluginFactory.createProcedureCategory("list").setColor(39).setLanguage(en, "Lists").buildAndOutput();
		mCreatorPluginFactory.createVariable().setName("objectlist").setNullable(false).setIgnoredByCoverage(true)
				.setColor(39).setBlocklyVariableType("ObjectList").initGenerator().buildAndOutput();

		mCreatorPluginFactory.getToolKit().createInputProcedure("list_add").setColor(39)
				.appendArgs0InputValue("list", "ObjectList").appendArgs0InputValue("element", (String) null, true)
				.toolBoxInitBuilder().setName("element").appendConstantString("helloworld").buildAndReturn()
				.setLanguage(en, "add element %2 to %1").setToolBoxId("list").initGenerator().buildAndOutput();
		mCreatorPluginFactory.getToolKit().createInputProcedure("list_clear")
				.appendArgs0InputValue("list", "ObjectList").setLanguage(en, "clear list %1").setToolBoxId("list")
				.initGenerator().buildAndOutput();
		mCreatorPluginFactory.getToolKit().createOutputProcedure("list_get", (String) null).setColor(39)
				.setToolBoxId("list").appendArgs0InputValue("list", "ObjectList")
				.appendArgs0InputValue("index", BuiltInTypes.Number)
				.appendArgs0FieldDataListSelector("type", "supportedtypes", "String").toolBoxInitBuilder()
				.setName("index").appendConstantNumber(0).buildAndReturn()
				.setLanguage(en, "get element index %2 from %1, type: %3").initGenerator().buildAndOutput();

		mCreatorPluginFactory.initGenerator(Generators.NEOFORGE1214);
		mCreatorPluginFactory.getToolKit().clearGenerator(Generators.NEOFORGE1214);
		mCreatorPluginFactory.initGenerator(Generators.NEOFORGE1211);
		mCreatorPluginFactory.getToolKit().clearGenerator(Generators.NEOFORGE1211);

		en.buildAndOutput();

	}
}
