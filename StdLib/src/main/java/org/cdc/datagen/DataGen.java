package org.cdc.datagen;

import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.utils.*;

import java.awt.*;
import java.io.File;
import java.util.Locale;

public class DataGen {

	// 我是人
	private static final String PLUS_SELF = "<block type=\"math_plus_self\"><value name=\"value\"><block type=\"math_number\"><field name=\"NUM\">0</field></block></value></block>";

	public static void main(String[] args) {
		MCreatorPluginFactory factory = new MCreatorPluginFactory(new File("src/main/resources").getAbsoluteFile());
		var en = factory.createDefaultLanguage();
		var zh = factory.createLanguage(Locale.CHINA);

		factory.createTrigger("on_gui_layer_render").appendDependency("namespace", BuiltInTypes.String)
				.appendDependency("path", BuiltInTypes.String).setLanguage(en, "On layer render").setCancelable(true)
				.setLanguage(zh, "GUI层渲染时").setSide(Side.Client).initGenerator().buildAndOutput();

		factory.getToolKit().createOutputProcedure("vec3_create_vec", "_vec3")
				.setToolBoxId(BuiltInToolBoxId.Procedure.MATH)
				.appendArgs0InputValueWithDefaultToolboxInit("x", BuiltInTypes.Number)
				.appendArgs0InputValueWithDefaultToolboxInit("y", BuiltInTypes.Number)
				.appendArgs0InputValueWithDefaultToolboxInit("z", BuiltInTypes.Number)
				.setLanguage(en, "create vector x: %1 y: %2 z: %3").setLanguage(zh, "创建向量x: %1 y: %2 z: %3")
				.initGenerator().buildAndOutput();
		factory.getToolKit().createOutputProcedure("entity_get_view_vectory", "_vec3")
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn()
				.appendArgs0InputValueWithDefaultToolboxInit("distance", BuiltInTypes.Number)
				.setLanguage(en, "get view vector of %1 with distance %2").initGenerator().buildAndOutput();

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
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().appendArgs0InputValue("value", BuiltInTypes.Number)
				.toolBoxInitBuilder().setName("value").appendElement(PLUS_SELF).buildAndReturn()
				.setLanguage(en, "set rendered entity %1 arrow count to %2")
				.setLanguage(zh, "设置实体%1被箭射中的数量为%2").initGenerator().buildAndOutput();
		factory.getToolKit().createOutputProcedure("entity_get_arrow_count", BuiltInTypes.Number)
				.setColor(BuiltInBlocklyColor.MATH.toString()).setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_DATA)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().setLanguage(en, "get rendered entity %1 arrow count")
				.setLanguage(zh, "实体%1身上箭头的数量").initGenerator().buildAndOutput();
		factory.createProcedure("entity_get_invulnerabletime").setOutput(BuiltInTypes.Number).setInputsInline(true)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).setColor(BuiltInBlocklyColor.MATH.toString())
				.toolBoxInitBuilder().setName("entity").appendDefaultEntity().buildAndReturn()
				.setLanguage(en, "get invulnerableTime of %1").setLanguage(zh, "实体%1的无敌帧")
				.setCategory(BuiltInToolBoxId.Procedure.ENTITY_DATA).initGenerator().buildAndOutput();
		factory.getToolKit().createInputProcedure("entity_set_invulnerabletime")
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).setColor(195)
				.appendArgs0InputValue("value", BuiltInTypes.Number).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().toolBoxInitBuilder().setName("value").appendElement(PLUS_SELF)
				.buildAndReturn().setCategory(BuiltInToolBoxId.Procedure.ENTITY_MANAGEMENT).initGenerator()
				.setLanguage(en, "set entity %1 invulnerableTime to %2").setLanguage(zh, "设置实体%1的无敌帧为%2")
				.buildAndOutput();
		factory.getToolKit().createOutputProcedure("entity_nbt_logic_get_advanced", BuiltInTypes.Boolean)
				.setColor(BuiltInBlocklyColor.LOGIC.toString()).setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_DATA)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().appendArgs0InputValue("tagName", BuiltInTypes.String)
				.toolBoxInitBuilder().setName("tagName").appendConstantString("node1.node2").buildAndReturn()
				.setLanguage(en, "get entity %1 logic nbt %2").setLanguage(zh, "得到实体%1的逻辑nbt%2").initGenerator()
				.buildAndOutput();
		factory.getToolKit().createOutputProcedure("entity_nbt_num_get_advanced", BuiltInTypes.Number)
				.setColor(BuiltInBlocklyColor.MATH.toString()).setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_DATA)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().appendArgs0InputValue("tagName", BuiltInTypes.String)
				.toolBoxInitBuilder().setName("tagName").appendConstantString("node1.node2").buildAndReturn()
				.setLanguage(en, "get entity %1 number nbt %2").setLanguage(zh, "得到实体%1的数字nbt%2").initGenerator()
				.buildAndOutput();
		factory.getToolKit().createOutputProcedure("entity_nbt_text_get_advanced", BuiltInTypes.String)
				.setColor(BuiltInBlocklyColor.TEXTS.toString()).setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_DATA)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().appendArgs0InputValue("tagName", BuiltInTypes.String)
				.toolBoxInitBuilder().setName("tagName").appendConstantString("node1.node2").buildAndReturn()
				.setLanguage(en, "get entity %1 text nbt %2").setLanguage(zh, "得到实体%1的字符串nbt%2").initGenerator()
				.buildAndOutput();
		factory.getToolKit().createInputProcedure("entity_nbt_text_set_advanced")
				.setColor(BuiltInBlocklyColor.TEXTS.toString())
				.setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_MANAGEMENT)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().appendArgs0InputValue("tagName", BuiltInTypes.String)
				.toolBoxInitBuilder().setName("tagName").appendConstantString("node1.node2.node3").buildAndReturn()
				.appendArgs0InputValue("tagValue", BuiltInTypes.String).toolBoxInitBuilder().setName("tagValue")
				.appendConstantString("").buildAndReturn().setLanguage(en, "set entity %1 text nbt %2 to %3")
				.setLanguage(zh, "得到实体%1的字符串nbt%2为%3").initGenerator().buildAndOutput();
		factory.getToolKit().createInputProcedure("entity_nbt_logic_set_advanced")
				.setColor(BuiltInBlocklyColor.LOGIC.toString())
				.setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_MANAGEMENT)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().appendArgs0InputValue("tagName", BuiltInTypes.String)
				.toolBoxInitBuilder().setName("tagName").appendConstantString("node1.node2.node3").buildAndReturn()
				.appendArgs0InputValue("tagValue", BuiltInTypes.Boolean).toolBoxInitBuilder().setName("tagValue")
				.appendConstantBoolean(true).buildAndReturn().setLanguage(en, "set entity %1 logic nbt %2 to %3")
				.setLanguage(zh, "设置实体%1的字符串nbt%2为%3").initGenerator().buildAndOutput();
		factory.getToolKit().createInputProcedure("entity_nbt_num_set_advanced")
				.setColor(BuiltInBlocklyColor.MATH.toString())
				.setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_MANAGEMENT)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().appendArgs0InputValue("tagName", BuiltInTypes.String)
				.toolBoxInitBuilder().setName("tagName").appendConstantString("node1.node2.node3").buildAndReturn()
				.appendArgs0InputValue("tagValue", BuiltInTypes.Number).toolBoxInitBuilder().setName("tagValue")
				.appendConstantNumber(0).buildAndReturn().setLanguage(en, "set entity %1 number nbt %2 to %3")
				.setLanguage(zh, "设置实体%1的数字nbt%2为%3").initGenerator().buildAndOutput();
		factory.getToolKit().createOutputProcedure("entity_nbt_has_advanced", BuiltInTypes.Boolean)
				.setColor(BuiltInBlocklyColor.LOGIC.toString()).setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_DATA)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().appendArgs0InputValue("tagName", BuiltInTypes.String)
				.toolBoxInitBuilder().setName("tagName").appendConstantString("").buildAndReturn()
				.setLanguage(en, "entity %1 has nbt %2").setLanguage(zh, "实体%1存在nbt%2").initGenerator()
				.buildAndOutput();
		factory.getToolKit().createInputProcedure("entity_nbt_create_empty_compound")
				.setColor(BuiltInBlocklyColor.ENTITY_COLOR).setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_MANAGEMENT)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().appendArgs0InputValue("tagName", BuiltInTypes.String)
				.toolBoxInitBuilder().setName("tagName").appendConstantString("").buildAndReturn()
				.setLanguage(en, "append empty compound tag to entity %1 location %2")
				.setLanguage(zh, "追加空nbt组件到实体%1位置%2").initGenerator().buildAndOutput();

		factory.getToolKit().createOutputProcedure("item_player_skull", BuiltInTypes.ItemStack)
				.setColor(BuiltInBlocklyColor.ITEMSTACK_COLOR).setToolBoxId(BuiltInToolBoxId.Procedure.ITEM_DATA)
				.appendArgs0InputValue("name", BuiltInTypes.String).toolBoxInitBuilder().setName("name")
				.appendConstantString("cdc12345").buildAndReturn().appendArgs0InputValue("uuid", BuiltInTypes.String)
				.toolBoxInitBuilder().setName("uuid").appendConstantString("fefb13e3-3089-433e-9f72-ffb85e62dd73")
				.buildAndReturn().setLanguage(en, "get player skull, name: %1, uuid: %2")
				.setLanguage(zh, "获取玩家头颅物品,name: %1,UUID: %2").initGenerator().buildAndOutput();
		factory.getToolKit().createInputProcedure("item_unbreakable_set")
				.setToolBoxId(BuiltInToolBoxId.Procedure.ITEM_MANAGEMENT).setColor(BuiltInBlocklyColor.ITEMSTACK_COLOR)
				.appendArgs0InputValue("itemstack", BuiltInTypes.ItemStack).toolBoxInitBuilder().setName("itemstack")
				.appendDefaultItem().buildAndReturn()
				.appendArgs0InputValueWithDefaultToolboxInit("add_to_tooltip", BuiltInTypes.Boolean)
				.setLanguage(en, "make itemstack %1 unbreakable, addToTooltip: %2")
				.setLanguage(zh, "使物品%1无法破坏，显示在物品tooltip: %2").initGenerator().buildAndOutput();

		factory.getToolKit().createOutputProcedure("math_plus_self", BuiltInTypes.Number)
				.setToolBoxId(BuiltInToolBoxId.Procedure.MATH).setColor(BuiltInBlocklyColor.MATH.toString())
				.appendArgs0InputValue("value", BuiltInTypes.Number).toolBoxInitBuilder().setName("value")
				.appendConstantNumber(0).buildAndReturn().setLanguage(en, "plus myself %1").setLanguage(zh, "自增%1")
				.initGenerator().buildAndOutput();
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
