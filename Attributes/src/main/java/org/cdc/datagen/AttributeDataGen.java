package org.cdc.datagen;

import org.cdc.datagen.types.AttributeModifierType;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.builder.PluginInfoBuilder;
import org.cdc.framework.interfaces.IFountainMain;
import org.cdc.framework.interfaces.annotation.DefaultPluginFolder;
import org.cdc.framework.utils.BuiltInToolBoxId;
import org.cdc.framework.utils.BuiltInTypes;
import org.cdc.framework.utils.Generators;
import org.cdc.framework.utils.MCreatorVersions;

import java.util.Locale;
import java.util.Map;

@DefaultPluginFolder public class AttributeDataGen implements IFountainMain {

	@Override public void generatePluginInfo(PluginInfoBuilder infoBuilder) {
		infoBuilder.setAuthor("cdc12345").setName("attributes").setId("attributes").setWeight(0)
				.addSupportedVersion(MCreatorVersions.V_2025_2).addSupportedVersion(MCreatorVersions.V_2025_1)
				.addSupportedVersion(MCreatorVersions.V_2024_4).buildAndOutput();
	}

	@Override public void generatePlugin(MCreatorPluginFactory plugin) {
		var en = plugin.createDefaultLanguage();
		var zh = plugin.createLanguage(Locale.CHINA);

		plugin.createProcedureTemplateFolder();

		plugin.createProcedureCategory("attributes").setParentCategory(BuiltInToolBoxId.Procedure.ITEM_PROCEDURES)
				.setColor("75").setLanguage(en, "Attribute Modifier").initGenerator().buildAndOutput();

		plugin.createDataList("types").appendElement("_modifiers", "List.of()")
				.initGenerator();

		plugin.createVariable(AttributeModifierType.getInstance()).setColor("76").initGenerator().buildAndOutput();

		plugin.createTrigger().setName("itemattributemodifers").appendDependency("itemstack", BuiltInTypes.ItemStack)
				.appendDependency("attributesmodifiers", "_modifiers")
				.setLanguage(en, "on item attribute modifier registered").initGenerator().buildAndOutput();

		plugin.getToolKit().createOutputProcedure("create_attribute_modifier", AttributeModifierType.getInstance())
				.setToolBoxId("attributes").setColor("75").appendArgs0InputValue("unique_id", "_unique_id")
				.appendArgs0InputValueWithDefaultToolboxInit("amount", BuiltInTypes.Number)
				.appendArgs0FieldDropDown("operation",
						Map.of("ADD_VALUE", "ADD_VALUE", "ADD_MULTIPLIED_BASE", "ADD_MULTIPLIED_BASE",
								"ADD_MULTIPLIED_TOTAL", "ADD_MULTIPLIED_TOTAL"))
				.setLanguage(en, "Create modifier uniqueId: %1, amount: %2, operation: %3").initGenerator()
				.setLanguage(zh,"创建属性修饰符UUID: %1,值: %2,操作: %3").buildAndOutput();
		plugin.getToolKit().createInputProcedure("attribute_add_modifier").setToolBoxId("attributes").setColor("75")
				.appendArgs0FieldDataListSelector("attribute", "attributes", "ATTACK_DAMAGE")
				.appendArgs0FieldInput("slot", "ANY")
				.appendArgs0InputValue("modifier", AttributeModifierType.getInstance())
				.appendDependency("attributesmodifiers", "_modifiers")
				.setLanguage(en, "add item attribute %1 modifier %3 with slot: %2").setLanguage(zh,"添加物品属性 %1 修饰符 %3 于槽位 %2")
				.setToolTip(en, "trigger must be the on item attribute modifier registered").initGenerator()
				.buildAndOutput();
		plugin.getToolKit().createOutputProcedure("unique_resource_location", "_unique_id").setToolBoxId("attributes")
				.appendArgs0InputValue("location", BuiltInTypes.String).toolBoxInitBuilder().setName("location")
				.appendConstantString("namespace:path").buildAndReturn().setLanguage(en, "Resource Location %1")
				.setToolTip(en, "This can only be used below 1.21").initGenerator().buildAndOutput();
		plugin.getToolKit().createOutputProcedure("unique_uuid", "_unique_id").setToolBoxId("attributes").setColor("75")
				.appendArgs0FieldInput("uuid", "UUID").setLanguage(en, "UUID: %1")
				.setToolTip(en, "This can only be used in 1.20.1").initGenerator().buildAndOutput();

		plugin.initGenerator(Generators.FORGE1201);
		plugin.getToolKit().clearGenerator();
		plugin.initGenerator(Generators.NEOFORGE1211);
		plugin.getToolKit().clearGenerator();
		plugin.initGenerator(Generators.NEOFORGE1218);
		plugin.getToolKit().clearGenerator();

		en.buildAndOutput();
		zh.buildAndOutput();
	}
}
