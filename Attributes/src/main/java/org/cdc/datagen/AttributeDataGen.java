package org.cdc.datagen;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.interfaces.IFountainMain;
import org.cdc.framework.interfaces.annotation.DefaultPluginFolder;
import org.cdc.framework.utils.BuiltInTypes;
import org.cdc.framework.utils.Generators;
import org.cdc.framework.utils.MCreatorVersions;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@DefaultPluginFolder public class AttributeDataGen implements IFountainMain {
	@Override public void generatePlugin(MCreatorPluginFactory plugin) {
		plugin.createInfo().setAuthor("cdc12345").setName("attributes").setId("attributes").setWeight(0)
				.addSupportedVersion(MCreatorVersions.V_2025_2).buildAndOutput();

		var en = plugin.createDefaultLanguage();
		var zh = plugin.createLanguage(Locale.CHINA);

		plugin.createProcedureTemplateFolder();

		plugin.createProcedureCategory("attributes").setColor("75").setLanguage(en, "Attributes").initGenerator()
				.buildAndOutput();

		plugin.createDataList("types").appendElement("_modifiers", null, List.of("List<ItemAttributeModifiers.Entry>"))
				.initGenerator().build();

		plugin.createTrigger().setName("itemattributemodifers").appendDependency("itemstack", BuiltInTypes.ItemStack)
				.appendDependency("attributesmodifiers", "_modifiers")
				.setLanguage(en, "on item attribute modifier registered").initGenerator().buildAndOutput();

		plugin.getToolKit().createInputProcedure("attribute_add_modifier").setToolBoxId("attributes").setColor("75")
				.appendArgs0InputValue("unique_id", "_unique_id")
				.appendArgs0FieldDataListSelector("attribute", "attributes", "ATTACK_DAMAGE")
				.appendArgs0InputValueWithDefaultToolboxInit("amount", BuiltInTypes.Number)
				.appendArgs0FieldDropDown("operation",
						Map.of("ADD_VALUE", "ADD_VALUE", "ADD_MULTIPLIED_BASE", "ADD_MULTIPLIED_BASE",
								"ADD_MULTIPLIED_TOTAL", "ADD_MULTIPLIED_TOTAL")).appendArgs0FieldInput("slot", "ANY")
				.appendDependency("attributesmodifiers", "_modifiers")
				.setLanguage(en, "add attribute %2 modifier with amount %3, operation: %4, unique id: %1, Slot: %5")
				.initGenerator().buildAndOutput();
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
		plugin.initGenerator(Generators.NEOFORGE1214);
		plugin.getToolKit().clearGenerator();

		en.buildAndOutput();
		zh.buildAndOutput();
	}
}
