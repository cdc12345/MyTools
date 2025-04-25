package org.cdc.datagen;

import com.google.gson.JsonPrimitive;
import org.cdc.datagen.elements.VariableTypes;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.*;

import java.awt.*;
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
        var cn = pluginFactory.createLanguage(Locale.CHINA).appendLocalization("elementgui.enchantment.damage_bonus", "伤害加成");

        createProcedures(pluginFactory);
        createTriggers(pluginFactory);
        pluginFactory.createTrigger().setName("entity_pre_hurt_reload").appendDependency("x", BuiltInTypes.Number).appendDependency("y", BuiltInTypes.Number).appendDependency("z", BuiltInTypes.Number).appendDependency("world", BuiltInTypes.World)
                .appendDependency("amount", VariableTypes.ATOMIC_NUMBER).appendDependency("entity", BuiltInTypes.Entity)
                .appendDependency("damagesource", BuiltInTypes.DamageSource).appendDependency("sourceentity", BuiltInTypes.Entity).initGenerator().setLanguage(en, "TEntity Pre Hurt")
                .setLanguage(cn, "T实体受伤前").setCancelable(true).buildAndOutput();
        pluginFactory.createTrigger().setName("living_pre_heal").appendDependency("x", BuiltInTypes.Number).appendDependency("y", BuiltInTypes.Number).appendDependency("world", BuiltInTypes.World).appendDependency("amount", VariableTypes.ATOMIC_NUMBER)
                .appendDependency("entity", BuiltInTypes.Entity).setLanguage(en, "Living Pre Heal").setCancelable(true).buildAndOutput();
        pluginFactory.createTrigger().setName("fov_update").appendDependency("x", BuiltInTypes.Number).appendDependency("y", BuiltInTypes.Number)
                .appendDependency("z", BuiltInTypes.Number).appendDependency("world", BuiltInTypes.World).appendDependency("amount", VariableTypes.ATOMIC_NUMBER)
                .appendDependency("entity", BuiltInTypes.Entity).initGenerator().setLanguage(en, "Fov Update")
                .setSide(Side.Client).buildAndOutput();
        pluginFactory.createTrigger("creativetab_content").appendDependency("tab", BuiltInTypes.String).initGenerator().setLanguage(en, "BuildCreativeTab").buildAndOutput();

        //CreativeTab
        pluginFactory.createProcedure().setName("creativetab").markType().setColor(Color.BLUE).setLanguage(en, "CreativeTab").initGenerator().buildAndOutput();
        pluginFactory.createProcedure("creativetab_inserbefore").setPreviousStatement(null).setNextStatement(null).setColor(Color.BLUE).appendArgs0InputValue("before", BuiltInTypes.ItemStack).appendArgs0InputValue("item", BuiltInTypes.ItemStack).appendArgs0FieldDropDown("tabvisible", new JsonPrimitive("PARENT_TAB_ONLY"), new JsonPrimitive("SEARCH_TAB_ONLY"), new JsonPrimitive("PARENT_AND_SEARCH_TABS")).appendToolBoxInit("<value name=\"before\"><block type=\"mcitem_all\"><field name=\"value\"></field></block></value>").appendToolBoxInit("<value name=\"item\"><block type=\"mcitem_all\"><field name=\"value\"></field></block></value>").setToolBoxId("creativetab").initGenerator().setLanguage(en, "CreativeTab insert %2 before %1,Visible: %3").buildAndOutput();
        pluginFactory.createProcedure("creativetab_insertafter").setPreviousStatement(null).setNextStatement(null).setColor(Color.BLUE).appendArgs0InputValue("after", BuiltInTypes.ItemStack).appendArgs0InputValue("item", BuiltInTypes.ItemStack).appendArgs0FieldDropDown("tabvisible", new JsonPrimitive("PARENT_TAB_ONLY"), new JsonPrimitive("SEARCH_TAB_ONLY"), new JsonPrimitive("PARENT_AND_SEARCH_TABS")).appendToolBoxInit("<value name=\"after\"><block type=\"mcitem_all\"><field name=\"value\"></field></block></value>").appendToolBoxInit("<value name=\"item\"><block type=\"mcitem_all\"><field name=\"value\"></field></block></value>").setToolBoxId("creativetab").initGenerator().setLanguage(en, "CreativeTab insert %2 after %1,Visible: %3").buildAndOutput();

//        pluginFactory.createDataList("eventnumberparameters").appendElement("amount").initGenerator().buildAndOutput();

        pluginFactory.createProcedure("atomic_number_set").appendArgs0InputValue("key", VariableTypes.ATOMIC_NUMBER)
                .appendArgs0InputValue("value", BuiltInTypes.Number).toolBoxInitBuilder().setName("value")
                .appendConstantNumber(0).buildAndReturn().setInputsInline(true)
                .setNextStatement(null).setPreviousStatement(null).setColor(BuiltInBlocklyColor.MATH.toString())
                .setToolBoxId(BuiltInToolBoxId.Procedure.MATH).setLanguage(en, "set %1 to %2").initGenerator().buildAndOutput();
        pluginFactory.createProcedure("atomic_number_get").setColor(BuiltInBlocklyColor.MATH.toString()).appendArgs0InputValue("key", VariableTypes.ATOMIC_NUMBER).setLanguage(en, "get %1").setOutput(BuiltInTypes.Number).setToolBoxId(BuiltInToolBoxId.Procedure.MATH).setGroup("name").setInputsInline(true)
                .initGenerator().buildAndOutput();

        //Debug
        pluginFactory.createProcedure("number_to_double").setColor(BuiltInBlocklyColor.MATH.toString())
                .appendArgs0InputValue("value", BuiltInTypes.Number).setOutput(BuiltInTypes.Number)
                .setGroup("name").setCategory(BuiltInToolBoxId.Procedure.ADVANCED).toolBoxInitBuilder().setName("value").appendConstantNumber(0).buildAndReturn().setLanguage(en, "Double(%1)")
                .initGenerator().buildAndOutput();
        pluginFactory.createProcedure("minecraft_isclient").setColor(BuiltInBlocklyColor.LOGIC.toString())
                .setOutput(BuiltInTypes.Boolean).setGroup("name").setCategory(BuiltInToolBoxId.Procedure.ADVANCED)
                .setLanguage(en, "isClient").initGenerator().buildAndOutput();
        pluginFactory.createProcedure("minecraft_isserver").setColor(BuiltInBlocklyColor.LOGIC.toString())
                .setOutput(BuiltInTypes.Boolean).setGroup("name").setCategory(BuiltInToolBoxId.Procedure.ADVANCED)
                .setLanguage(en, "isServer").initGenerator().buildAndOutput();
//        pluginFactory.createProcedure("event_number_parameter_set").setColor(BuiltInBlocklyColor.MATH.toString())
//                .setNextStatement(null).setPreviousStatement(null).setCategory(BuiltInToolBoxId.Procedure.ADVANCED).setGroup("name")
//                .appendArgs0FieldDataListSelector("parameter","eventnumberparameters","amount").appendArgs0InputValue("value",BuiltInTypes.Number)
//                .setLanguage(en,"set number event parameter %1 to %2").initGenerator().buildAndOutput();

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
