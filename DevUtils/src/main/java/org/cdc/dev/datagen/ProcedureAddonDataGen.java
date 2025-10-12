package org.cdc.dev.datagen;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.interfaces.IFountainMain;
import org.cdc.framework.interfaces.annotation.DefaultPluginFolder;
import org.cdc.framework.utils.BuiltInToolBoxId;
import org.cdc.framework.utils.Generators;

import java.awt.*;
import java.util.Locale;

@DefaultPluginFolder
public class ProcedureAddonDataGen implements IFountainMain {
	@Override public void generatePlugin(MCreatorPluginFactory pluginFactory) {
		var en = pluginFactory.createDefaultLanguage();
		var zh = pluginFactory.createLanguage(Locale.CHINA);

		//advanced
		pluginFactory.getToolKit().createInputProcedure("advanced_do_nothing").setColor(Color.GRAY.darker())
				.appendArgs0StatementInput("statement").statementBuilder().setName("statement").buildAndReturn()
				.setToolBoxId(BuiltInToolBoxId.Procedure.ADVANCED).setLanguage(en, "Do nothing %1")
				.setLanguage(zh, "啥也不做 %1").initGenerator().buildAndOutput();

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
