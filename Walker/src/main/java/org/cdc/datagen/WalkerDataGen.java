package org.cdc.datagen;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.BuiltInBlocklyColor;
import org.cdc.framework.utils.BuiltInTypes;
import org.cdc.framework.utils.Generators;

import java.awt.*;
import java.io.File;

public class WalkerDataGen {
	public static void main(String[] args) {
		MCreatorPluginFactory mcr = new MCreatorPluginFactory(new File("src/main/resources").getAbsoluteFile());

		var en = mcr.createDefaultLanguage();

		mcr.createDataList("types").appendElement("jsonelement", "JsonElement").initGenerator().build();

		//		mcr.createVariable(JsonElementType.getInstance()).setColor("#F0FFF0").setNullable(true).initGenerator()
		//				.buildAndOutput();

		mcr.createProcedureCategory("walkers").setColor(Color.MAGENTA).setLanguage(en, "Walkers").buildAndOutput();
		mcr.createProcedureCategory("json_walker").setParentCategory("walkers").setColor(Color.MAGENTA)
				.setLanguage(en, "Json walker").buildAndOutput();
		mcr.createProcedureCategory("nbt_walker").setColor(Color.MAGENTA.darker()).setParentCategory("walkers")
				.setLanguage(en, "NBT Walker").buildAndOutput();

		mcr.getToolKit().createOutputProcedure("walk_nbt_element", (String) null).setToolBoxId("nbt_walker")
				.appendArgs0InputValueWithDefaultToolboxInit("entity", BuiltInTypes.Entity)
				.appendArgs0StatementInput("walker").statementBuilder().setName("walker")
				.appendProvide("_walker", "_walker").buildAndReturn().setLanguage(en, "Journey start with entity %1 %2")
				.initGenerator().buildAndOutput();
		mcr.getToolKit().createInputProcedure("walk_nbt_see_compound").appendDependency("_walker", "_walker")
				.appendArgs0InputValueWithDefaultToolboxInit("tagName", BuiltInTypes.String)
				.setLanguage(en, "get compound %1").initGenerator().buildAndOutput();
		mcr.getToolKit().createEndProcedure("walk_nbt_see_number").setToolBoxId("nbt_walker")
				.setColor(BuiltInBlocklyColor.MATH.toString()).appendDependency("_walker", "_walker")
				.appendArgs0InputValueWithDefaultToolboxInit("tagName", BuiltInTypes.String)
				.setLanguage(en, "get number %1").initGenerator().buildAndOutput();
		mcr.getToolKit().createEndProcedure("walk_nbt_see_text").setToolBoxId("nbt_walker")
				.setColor(BuiltInBlocklyColor.TEXTS.toString()).appendDependency("_walker", "_walker")
				.appendArgs0InputValueWithDefaultToolboxInit("tagName", BuiltInTypes.String).initGenerator()
				.setLanguage(en, "get text %1").buildAndOutput();
		mcr.getToolKit().createEndProcedure("walk_nbt_see_logic").setToolBoxId("nbt_walker")
				.setColor(BuiltInBlocklyColor.LOGIC.toString()).appendDependency("_walker", "_walker")
				.appendArgs0InputValueWithDefaultToolboxInit("tagName", BuiltInTypes.String).initGenerator()
				.setLanguage(en, "get nbt logic %1").buildAndOutput();
		mcr.getToolKit().createEndProcedure("walk_nbt_contains").setToolBoxId("nbt_walker")
				.setColor(BuiltInBlocklyColor.LOGIC.toString()).appendDependency("_walker", "_walker")
				.appendArgs0InputValueWithDefaultToolboxInit("tagName", BuiltInTypes.String).initGenerator()
				.setLanguage(en, "exist %1").buildAndOutput();

		//		mcr.createProcedure("walk_nbt_into").appendArgs0InputValue("key", BuiltInTypes.String)
		//				.setLanguage(en, "walk in %1").initGenerator().buildAndOutput();
		//
		//		mcr.getToolKit().createOutputProcedure("walk_json_set_number", BuiltInTypes.Number).appendArgs0InputValue("value",BuiltInTypes.Number).setPlaceHolderLanguage(en,"set value number: %value").initGenerator()
		//				.buildAndOutput();
		//		mcr.getToolKit().createOutputProcedure("walk_json_set_string", BuiltInTypes.String).appendArgs0InputValue("value",BuiltInTypes.String).setPlaceHolderLanguage(en,"set value string: %value").initGenerator()
		//				.buildAndOutput();
		//		mcr.getToolKit().createOutputProcedure("walk_json_set_logic", BuiltInTypes.Boolean).appendArgs0InputValue("value",BuiltInTypes.Boolean).setPlaceHolderLanguage(en,"set value logic: %value").initGenerator()
		//				.buildAndOutput();
		//		mcr.getToolKit().createOutputProcedure("walk_json_contains",BuiltInTypes.Boolean).appendArgs0InputValue("key",BuiltInTypes.String).setLanguage(en,"Is %key a right direction").initGenerator().buildAndOutput();
		//		mcr.getToolKit().createOutputProcedure("walk_json_see_number", BuiltInTypes.Number)
		//				.setLanguage(en, "get number").setColor(BuiltInBlocklyColor.MATH.toString()).initGenerator()
		//				.buildAndOutput();
		//		mcr.getToolKit().createOutputProcedure("walk_json_see_string", BuiltInTypes.String)
		//				.setLanguage(en, "get string").setColor(BuiltInBlocklyColor.TEXTS.toString()).initGenerator()
		//				.buildAndOutput();
		//		mcr.getToolKit().createOutputProcedure("walk_json_see_logic", BuiltInTypes.Boolean).setLanguage(en, "get logic")
		//				.setColor(BuiltInBlocklyColor.LOGIC.toString()).initGenerator().buildAndOutput();
		//

		mcr.initGenerator(Generators.NEOFORGE1211);
		mcr.initGenerator(Generators.NEOFORGE1214);

		en.buildAndOutput();
	}
}
