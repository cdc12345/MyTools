package org.cdc.dev.datagen;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.interfaces.IFountainMain;
import org.cdc.framework.interfaces.annotation.DefaultPluginFolder;
import org.cdc.framework.utils.BuiltInToolBoxId;
import org.cdc.framework.utils.BuiltInTypes;
import org.cdc.framework.utils.Generators;

import java.awt.*;
import java.util.Locale;

@DefaultPluginFolder public class ProcedureAddonDataGen implements IFountainMain {
	@Override public void generatePlugin(MCreatorPluginFactory pluginFactory) {
		var en = pluginFactory.createDefaultLanguage();
		var zh = pluginFactory.createLanguage(Locale.CHINA);

		//advanced
		pluginFactory.getToolKit().createInputProcedure("advanced_do_nothing").setColor(Color.GRAY.darker())
				.appendArgs0StatementInput("statement").statementBuilder().setName("statement").buildAndReturn()
				.setToolBoxId(BuiltInToolBoxId.Procedure.ADVANCED).setLanguage(en, "Do nothing %1")
				.setLanguage(zh, "啥也不做 %1").initGenerator().buildAndOutput();

		pluginFactory.getToolKit().createOutputProcedure("advanced_dynamic_variable_value", (String) null)
				.setColor(Color.ORANGE.darker()).appendArgs0FieldInput("name").setLanguage(en, "%1")
				.setLanguage(zh, "%1").initGenerator().buildAndOutput();
		pluginFactory.getToolKit().createInputProcedure("advanced_dynamic_variable_setter")
				.appendArgs0InputValue("variable", (String) null).toolBoxInitBuilder().setName("variable")
				.appendReferenceBlock("advanced_dynamic_variable_value").buildAndReturn()
				.appendArgs0InputValue("value", (String) null).appendArgs0FieldCheckbox("init", true)
				.setLanguage(en, "set %1 to %2 init: %3").initGenerator().buildAndOutput();

		pluginFactory.getToolKit().createOutputProcedure("plus_self", BuiltInTypes.Number)
				.setToolBoxId(BuiltInToolBoxId.Procedure.MATH).appendArgs0InputValue("value", BuiltInTypes.Number)
				.toolBoxInitBuilder().setName("value").appendConstantNumber(1).buildAndReturn()
				.setLanguage(en, "plus %1").setLanguage(zh, "系统自增 %1").initGenerator().buildAndOutput();

		en.buildAndOutput();
		zh.buildAndOutput();

		pluginFactory.initGenerator(Generators.NEOFORGE1211);
		pluginFactory.getToolKit().clearGenerator();
		pluginFactory.initGenerator(Generators.NEOFORGE1218);
		pluginFactory.getToolKit().clearGenerator();
		pluginFactory.initGenerator(Generators.NEOFORGE1214);
		pluginFactory.getToolKit().clearGenerator();
		pluginFactory.initGenerator(Generators.FORGE1201);
		pluginFactory.getToolKit().clearGenerator();
	}
}
