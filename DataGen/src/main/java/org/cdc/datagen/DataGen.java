package org.cdc.datagen;

import org.cdc.datagen.elements.VariableTypes;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.*;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DataGen {

    public static void main(String[] args) {
        List<String> argss = Arrays.asList(args);

        MCreatorPluginFactory pluginFactory = MCreatorPluginFactory.createFactory("src/main/resources");

        pluginFactory.createVariable(VariableTypes.ATOMIC_NUMBER).setColor(254).setNullable(true).setIgnoredByCoverage(true).buildAndOutput();

        pluginFactory.createDataList().setName(BuiltInDataList.TYPES).appendElement("atomicnumber").build();

        var en = pluginFactory.createDefaultLanguage().appendLocalization("elementgui.enchantment.damage_bonus", "Damage Bonus");
        var cn = pluginFactory.createLanguage(Locale.CHINA).appendLocalization("elementgui.enchantment.damage_bonus", "�˺��ӳ�");

        createProcedures(pluginFactory);
        createTriggers(pluginFactory);
        pluginFactory.createTrigger().setName("entity_pre_hurt_reload").appendDependency("x", BuiltInTypes.Number).appendDependency("y", BuiltInTypes.Number).appendDependency("z", BuiltInTypes.Number).appendDependency("world", BuiltInTypes.World)
                .appendDependency("amount", VariableTypes.ATOMIC_NUMBER).appendDependency("entity", BuiltInTypes.Entity)
                .appendDependency("damagesource", BuiltInTypes.DamageSource).appendDependency("sourceentity", BuiltInTypes.Entity).initGenerator().setLanguage(en, "Entity Pre Hurt")
                .setLanguage(cn, "ʵ������ǰ").setCancelable(true).buildAndOutput();
        pluginFactory.createTrigger().setName("living_pre_heal").appendDependency("x", BuiltInTypes.Number).appendDependency("y", BuiltInTypes.Number).appendDependency("world", BuiltInTypes.World).appendDependency("amount", VariableTypes.ATOMIC_NUMBER)
                .appendDependency("entity", BuiltInTypes.Entity).setLanguage(en, "Living Pre Heal").setCancelable(true).buildAndOutput();
        pluginFactory.createTrigger().setName("fov_update").appendDependency("x",BuiltInTypes.Number).appendDependency("y",BuiltInTypes.Number)
                .appendDependency("z",BuiltInTypes.Number).appendDependency("world",BuiltInTypes.World).appendDependency("amount",VariableTypes.ATOMIC_NUMBER).appendDependency("entity",BuiltInTypes.Entity).initGenerator().setLanguage(en,"Fov Update").buildAndOutput();


        pluginFactory.createProcedure("atomic_number_set").appendArgs0InputValue("key", VariableTypes.ATOMIC_NUMBER)
                .appendArgs0InputValue("value", BuiltInTypes.Number).toolBoxInitBuilder().setName("value")
                .appendConstantNumber(0).buildAndReturn().setInputsInline(true)
                .setNextStatement(null).setPreviousStatement(null).setColor(BuiltInBlocklyColor.MATH.toString())
                .setToolBoxId(BuiltInToolBoxId.Procedure.MATH).setLanguage(en, "set %1 to %2").initGenerator().buildAndOutput();
        pluginFactory.createProcedure("atomic_number_get").setColor(BuiltInBlocklyColor.MATH.toString()).appendArgs0InputValue("key",VariableTypes.ATOMIC_NUMBER).setLanguage(en,"get %1").setOutput(BuiltInTypes.Number).setToolBoxId(BuiltInToolBoxId.Procedure.MATH).setGroup("name").setInputsInline(true)
                        .initGenerator().buildAndOutput();

        //Debug
        pluginFactory.createProcedure("number_to_double").setColor(BuiltInBlocklyColor.MATH.toString())
                .appendArgs0InputValue("value",BuiltInTypes.Number).setOutput(BuiltInTypes.Number)
                .setGroup("name").setCategory(BuiltInToolBoxId.Procedure.ADVANCED).toolBoxInitBuilder().setName("value").appendConstantNumber(0).buildAndReturn().setLanguage(en,"Double(%1)")
                .initGenerator().buildAndOutput();
        pluginFactory.createProcedure("minecraft_isclient").setColor(BuiltInBlocklyColor.LOGIC.toString())
                        .setOutput(BuiltInTypes.Boolean).setGroup("name").setCategory(BuiltInToolBoxId.Procedure.ADVANCED)
                .setLanguage(en,"isClient").initGenerator().buildAndOutput();
        pluginFactory.createProcedure("minecraft_isserver").setColor(BuiltInBlocklyColor.LOGIC.toString())
                .setOutput(BuiltInTypes.Boolean).setGroup("name").setCategory(BuiltInToolBoxId.Procedure.ADVANCED)
                .setLanguage(en,"isServer").initGenerator().buildAndOutput();

        en.buildAndOutput();
        cn.buildAndOutput();

        boolean replace = argss.contains("--replace");
//        pluginFactory.initGenerator(Generators.DATAPACK1211,replace);
        pluginFactory.initGenerator(Generators.NEOFORGE1211, replace);
//        pluginFactory.initGenerator(Generators.DATAPACK1214,replace);
        pluginFactory.initGenerator(Generators.NEOFORGE1214, replace);
    }

    private static void createProcedures(MCreatorPluginFactory pluginFactory) {

    }

    private static void createTriggers(MCreatorPluginFactory pluginFactory) {

    }
}
