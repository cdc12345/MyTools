package org.cdc.datagen;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.BuiltInBlocklyColor;
import org.cdc.framework.utils.BuiltInToolBoxId;
import org.cdc.framework.utils.BuiltInTypes;
import org.cdc.framework.utils.Generators;

import java.awt.*;
import java.io.File;
import java.util.Locale;

public class DataGen {
	public static void main(String[] args) {
		MCreatorPluginFactory factory = new MCreatorPluginFactory(new File("src/main/resources").getAbsoluteFile());
		var en = factory.createDefaultLanguage();
		var zh = factory.createLanguage(Locale.CHINA);

		factory.getToolKit().createOutputProcedure("entity_save_to_string", BuiltInTypes.String)
				.setColor(BuiltInBlocklyColor.TEXTS.toString()).appendArgs0InputValue("entity", BuiltInTypes.Entity)
				.toolBoxInitBuilder().setName("entity").appendDefaultEntity().buildAndReturn()
				.setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_DATA).setLanguage(en, "entity %1 to String")
				.setLanguage(zh, "转换实体%1为字符串").initGenerator().buildAndOutput();
		factory.getToolKit().createOutputProcedure("string_to_entity", BuiltInTypes.Entity).setColor(195)
				.setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_DATA)
				.appendArgs0InputValue("source", BuiltInTypes.String).appendDependency("world", BuiltInTypes.World)
				.toolBoxInitBuilder().setName("source").appendConstantString("").buildAndReturn()
				.setLanguage(en, "restore the entity,string: %1").setLanguage(zh, "将字符串%1转换为实体")
				.initGenerator().buildAndOutput();
		factory.getToolKit().createInputProcedure("entity_set_arrow_count")
				.setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_MANAGEMENT)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity").appendDefaultEntity().buildAndReturn()
				.appendArgs0InputValue("value", BuiltInTypes.Number).toolBoxInitBuilder().setName("value").appendConstantNumber(0).buildAndReturn()
				.setLanguage(en, "set rendered entity %1 arrow count to %2").setLanguage(zh,"设置实体%1被箭射中的数量为%2").initGenerator().buildAndOutput();
		factory.getToolKit().createOutputProcedure("entity_get_arrow_count", BuiltInTypes.Number).setColor(BuiltInBlocklyColor.MATH.toString())
				.setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_DATA)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().setLanguage(en, "get rendered entity %1 arrow count").setLanguage(zh,"实体%1身上箭头的数量")
				.initGenerator().buildAndOutput();
		factory.createProcedure("entity_get_invulnerabletime").setOutput(BuiltInTypes.Number).setInputsInline(true)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).setColor(BuiltInBlocklyColor.MATH.toString())
				.toolBoxInitBuilder().setName("entity").appendDefaultEntity().buildAndReturn()
				.setLanguage(en, "get invulnerableTime of %1").setLanguage(zh,"实体%1的无敌帧").setCategory(BuiltInToolBoxId.Procedure.ENTITY_DATA)
				.initGenerator().buildAndOutput();
		factory.getToolKit().createInputProcedure("entity_set_invulnerabletime")
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).setColor(195)
				.appendArgs0InputValue("value", BuiltInTypes.Number).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().toolBoxInitBuilder().setName("value").appendConstantNumber(0)
				.buildAndReturn().setCategory(BuiltInToolBoxId.Procedure.ENTITY_MANAGEMENT).initGenerator()
				.setLanguage(en, "set entity %1 invulnerableTime to %2").setLanguage(zh,"设置实体%1的无敌帧为%2").buildAndOutput();

		factory.getToolKit().createOutputProcedure("math_plus_self", BuiltInTypes.Number).setToolBoxId(BuiltInToolBoxId.Procedure.MATH).setColor(BuiltInBlocklyColor
						.MATH.toString())
				.appendArgs0InputValue("value", BuiltInTypes.Number).toolBoxInitBuilder().setName("value")
				.appendConstantNumber(0).buildAndReturn().setLanguage(en, "plus myself %1").setLanguage(zh,"自增%1").initGenerator()
				.buildAndOutput();
		factory.getToolKit().createInputProcedure("advanced_do_nothing").setColor(Color.GRAY.darker())
				.appendArgs0StatementInput("statement").statementBuilder().setName("statement").buildAndReturn()
				.setToolBoxId(BuiltInToolBoxId.Procedure.ADVANCED).setLanguage(en, "Do nothing %1").initGenerator()
				.buildAndOutput();

		en.buildAndOutput();
		zh.buildAndOutput();

		factory.initGenerator(Generators.NEOFORGE1214);
		factory.getToolKit().clearGenerator();
		factory.initGenerator(Generators.NEOFORGE1211);
		factory.getToolKit().clearGenerator();
	}
}
