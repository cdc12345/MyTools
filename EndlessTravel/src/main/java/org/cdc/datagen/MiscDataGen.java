package org.cdc.datagen;

import com.google.gson.JsonPrimitive;
import org.cdc.datagen.categories.ListCategory;
import org.cdc.datagen.types.ObjectListType;
import org.cdc.framework.MCreatorPluginFactory;
import org.cdc.framework.interfaces.IFountainMain;
import org.cdc.framework.interfaces.annotation.DefaultPluginFolder;
import org.cdc.framework.utils.*;
import org.cdc.framework.utils.parser.DefaultParameterConvertor;
import org.cdc.framework.utils.parser.MethodParser;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

@DefaultPluginFolder public class MiscDataGen implements IFountainMain {

	private static final String PLUS_SELF = "<block type=\"math_plus_self\"><value name=\"value\"><block type=\"math_number\"><field name=\"NUM\">0</field></block></value></block>";

	public void generatePlugin(MCreatorPluginFactory factory) throws IOException {
		MethodParser methodParser = new MethodParser();
		methodParser.setParameterStringFunction(new DefaultParameterConvertor());
		methodParser.parseClass(this.getClass().getResourceAsStream("/org/cdc/sources/MCSource.java"));

		var en = factory.createDefaultLanguage();
		var zh = factory.createLanguage(Locale.CHINA);

		Function<Path, Boolean> generatorCode = path -> {
			//only support neoforge 1.21.8
			if (path.toString().contains("neoforge-1.21.8")) {
				try {
					methodParser.parseMethod(path.getFileName().toString().split("\\.")[0]);
					Files.copy(new ByteArrayInputStream(methodParser.toFTLContent().getBytes()),path, StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (NoSuchElementException e){
					e.printStackTrace();
				}
			}
			return false;
		};

		factory.createTrigger("on_gui_layer_render").appendDependency("namespace", BuiltInTypes.String)
				.appendDependency("path", BuiltInTypes.String).setLanguage(en, "On layer render").setCancelable(true)
				.setLanguage(zh, "GUI层渲染时").setSide(Side.Client).initGenerator().buildAndOutput();

		factory.createApis("MCreator");

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

		//triggers
		factory.createTrigger("creativetab_content").appendDependency("tab", BuiltInTypes.String).initGenerator()
				.setLanguage(en, "BuildCreativeTab").buildAndOutput();

		//creativeTab
		var blueDarker = Color.BLUE.darker();
		factory.createProcedure().setName("creativetab").setParentCategory(BuiltInToolBoxId.Procedure.ITEM_PROCEDURES)
				.markType().setColor(blueDarker).setLanguage(en, "CreativeTab").initGenerator().buildAndOutput();
		factory.createProcedure("creativetab_inserbefore").setPreviousStatement(null).setNextStatement(null)
				.setColor(blueDarker).appendArgs0InputValue("before", BuiltInTypes.ItemStack)
				.appendArgs0InputValue("item", BuiltInTypes.ItemStack)
				.appendArgs0FieldDropDown("tabvisible", new JsonPrimitive("PARENT_TAB_ONLY"),
						new JsonPrimitive("SEARCH_TAB_ONLY"), new JsonPrimitive("PARENT_AND_SEARCH_TABS"))
				.appendToolBoxInit(
						"<value name=\"before\"><block type=\"mcitem_all\"><field name=\"value\"></field></block></value>")
				.appendToolBoxInit(
						"<value name=\"item\"><block type=\"mcitem_all\"><field name=\"value\"></field></block></value>")
				.setToolBoxId("creativetab").initGenerator()
				.setLanguage(en, "creativeTab insert %2 before %1,Visible: %3").buildAndOutput();
		factory.createProcedure("creativetab_insertafter").setPreviousStatement(null).setNextStatement(null)
				.setColor(blueDarker).appendArgs0InputValue("after", BuiltInTypes.ItemStack)
				.appendArgs0InputValue("item", BuiltInTypes.ItemStack)
				.appendArgs0FieldDropDown("tabvisible", new JsonPrimitive("PARENT_TAB_ONLY"),
						new JsonPrimitive("SEARCH_TAB_ONLY"), new JsonPrimitive("PARENT_AND_SEARCH_TABS"))
				.appendToolBoxInit(
						"<value name=\"after\"><block type=\"mcitem_all\"><field name=\"value\"></field></block></value>")
				.appendToolBoxInit(
						"<value name=\"item\"><block type=\"mcitem_all\"><field name=\"value\"></field></block></value>")
				.setToolBoxId("creativetab").setGeneratorListener(generatorCode).initGenerator()
				.setLanguage(en, "creativeTab insert %2 after %1,Visible: %3").buildAndOutput();
		factory.getToolKit().createInputProcedure("creativetab_remove").setToolBoxId("creativetab").setColor(blueDarker)
				.appendArgs0InputValue("item", BuiltInTypes.ItemStack).toolBoxInitBuilder().setName("item")
				.appendDefaultItem().buildAndReturn()
				.appendArgs0FieldDropDown("tabvisible", new JsonPrimitive("PARENT_TAB_ONLY"),
						new JsonPrimitive("SEARCH_TAB_ONLY"), new JsonPrimitive("PARENT_AND_SEARCH_TABS"))
				.setLanguage(en, "remove %1 from creativeTab visible: %2")
				.setLanguage(zh, "移除物品%1从创造物品栏,可视化 %2").initGenerator().buildAndOutput();

		//lambda
		factory.getToolKit().createOutputProcedure("lambda_do", "_lambda")
				.setToolBoxId(BuiltInToolBoxId.Procedure.ADVANCED).setColor("15").appendArgs0StatementInput("body")
				.statementBuilder().setName("body").buildAndReturn().setLanguage(en, "do %1").initGenerator()
				.buildAndOutput();
		factory.getToolKit().createOutputProcedure("lambda_arg", (String) null).appendArgs0FieldInput("index", "1")
				.setLanguage(en, "lambda parameter %1").setLanguage(zh, "lambda参数%1").initGenerator()
				.buildAndOutput();
		factory.getToolKit().createEndProcedure("lambda_set_result").setToolBoxId(BuiltInToolBoxId.Procedure.ADVANCED)
				.appendArgs0InputValue("result", (String) null).setLanguage(en, "set lambda result %1").initGenerator()
				.buildAndOutput();

		//list
		factory.getToolKit().createOutputProcedure("list_stream_to_string_end", BuiltInTypes.String)
				.setToolBoxId(ListCategory.INSTANCE).setColor(BuiltInBlocklyColor.TEXTS.toString())
				.appendArgs0InputValue("list", ObjectListType.INSTANCE)
				.appendArgs0InputValue("delimiter", BuiltInTypes.String).toolBoxInitBuilder().setName("delimiter")
				.appendConstantString(",").buildAndReturn().appendArgs0InputValue("prefix", BuiltInTypes.String)
				.toolBoxInitBuilder().setName("prefix").appendConstantString("[").buildAndReturn()
				.appendArgs0InputValue("suffix", BuiltInTypes.String).toolBoxInitBuilder().setName("suffix")
				.appendConstantString("]").buildAndReturn().appendArgs0InputValue("decorator", "_lambda")
				.toolBoxInitBuilder().setName("decorator").appendElement(
						"<block type=\"lambda_do\"><statement name=\"body\"><block type=\"lambda_set_result\"><value name=\"result\"><block type=\"text_join\"><mutation items=\"1\"></mutation><value name=\"ADD0\"><block type=\"lambda_arg\"><field name=\"index\">1</field></block></value></block></value></block></statement></block>")
				.buildAndReturn().setLanguage(en, "to Text: %1, delimiter: %2, prefix: %3, suffix: %4, decorator: %5")
				.setLanguage(zh, "转列表为字符串：%1, 分割符号：%2, 前拽：%3, 后拽：%4, decorator: %5").initGenerator()
				.buildAndOutput();

		//entity
		factory.getToolKit().createInputProcedure("entity_set_in_lover")
				.setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_MANAGEMENT)
				.appendArgs0InputValue("animal", BuiltInTypes.Entity)
				.appendArgs0InputValue("player", BuiltInTypes.Entity)
				.appendArgs0InputValueWithDefaultToolboxInit("in_love", BuiltInTypes.Boolean)
				.setLanguage(en, "make animal %1 in love state: %3 by player %2")
				.setLanguage(zh, "让动物%1的求爱模式为%3，媒婆玩家：%2").initGenerator().buildAndOutput();
		factory.getToolKit().createInputProcedure("entity_set_in_lover_time")
				.setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_MANAGEMENT)
				.appendArgs0InputValue("animal", BuiltInTypes.Entity)
				.appendArgs0InputValueWithDefaultToolboxInit("in_love_time", BuiltInTypes.Number)
				.setLanguage(en, "make animal %1 in love remain time: %2")
				.setLanguage(zh, "让动物%1的求爱模式剩余时间为%2").initGenerator().buildAndOutput();
		factory.getToolKit().createOutputProcedure("entity_save_to_string", BuiltInTypes.String)
				.setColor(BuiltInBlocklyColor.TEXTS.toString()).appendArgs0InputValue("entity", BuiltInTypes.Entity)
				.toolBoxInitBuilder().setName("entity").appendDefaultEntity().buildAndReturn()
				.setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_DATA).setLanguage(en, "entity %1 to String")
				.setLanguage(zh, "转换实体%1为字符串").initGenerator().buildAndOutput();
		factory.getToolKit().createOutputProcedure("string_to_entity", BuiltInTypes.Entity).setColor(195)
				.setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_DATA)
				.appendArgs0InputValue("source", BuiltInTypes.String).appendDependency("world", BuiltInTypes.World)
				.toolBoxInitBuilder().setName("source").appendConstantString("").buildAndReturn()
				.appendArgs0InputValue("after", "_lambda").toolBoxInitBuilder().setName("after").appendElement(
						"<block type=\"lambda_do\"><statement name=\"body\"><block type=\"world_add_entity\"><value name=\"entity\"><block type=\"lambda_arg\"><field name=\"index\">1</field></block></value></block></statement></block>")
				.buildAndReturn().setLanguage(en, "restore the entity,string: %1 %2")
				.setLanguage(zh, "将字符串%1转换为实体后 %2").initGenerator().buildAndOutput();
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
		factory.getToolKit().createInputProcedure("entity_set_swimming")
				.setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_MANAGEMENT)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().appendArgs0InputValue("swimming", BuiltInTypes.Boolean)
				.toolBoxInitBuilder().setName("swimming").appendConstantBoolean(false).buildAndReturn()
				.setLanguage(en, "set entity %1 swimming %2").setLanguage(zh, "使实体 %1 游泳状态为%2").initGenerator()
				.buildAndOutput();
		factory.getToolKit().createInputProcedure("entity_start_falling_flying")
				.setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_MANAGEMENT)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().setLanguage(en, "entity %1 start fall flying")
				.setLanguage(zh, "实体%1开始降落飞行").initGenerator().buildAndOutput();
		factory.getToolKit().createInputProcedure("entity_stop_falling_flying")
				.setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_MANAGEMENT)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().setLanguage(en, "entity %1 stop fall flying")
				.setLanguage(zh, "实体%1停止降落飞行").initGenerator().buildAndOutput();

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
		factory.getToolKit().createInputProcedure("entity_run_function_silent")
				.setColor(BuiltInBlocklyColor.ENTITY_COLOR).setToolBoxId(BuiltInToolBoxId.Procedure.ENTITY_MANAGEMENT)
				.appendArgs0InputValue("function", BuiltInTypes.String).toolBoxInitBuilder().setName("function")
				.appendConstantString("function").buildAndReturn()
				.appendArgs0InputValueWithDefaultToolboxInit("entity", BuiltInTypes.Entity)
				.appendArgs0FieldImage(BuiltInImages.SERVER, 8, 24)
				.setLanguage(en, "Run function silent %1 in the name %2 %3").setLanguage(zh, "使%2%3静默运行函数%1")
				.initGenerator().buildAndOutput();

		//item
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

		//world
		factory.getToolKit().createInputProcedure("world_add_entity")
				.setToolBoxId(BuiltInToolBoxId.Procedure.WORLD_MANAGEMENT).appendDependency("world", BuiltInTypes.World)
				.appendArgs0InputValue("entity", BuiltInTypes.Entity).toolBoxInitBuilder().setName("entity")
				.appendDefaultEntity().buildAndReturn().setLanguage(en, "world add new entity %1")
				.setLanguage(zh, "为世界增加新的实体 %1").initGenerator().buildAndOutput();
		factory.createProcedure("run_function_silent").appendArgs0InputValue("function", BuiltInTypes.String)
				.appendArgs0InputValue("x", "Number").appendArgs0InputValue("y", "Number")
				.appendArgs0InputValue("z", "Number").appendArgs0FieldImage("./res/server.png", 8, 24)
				.setInputsInline(true).setColor(35).setPreviousStatement(null).setNextStatement(null)
				.setToolBoxId("worldmanagement").appendToolBoxInit(
						"<value name=\"function\"><block type=\"text\"><field name=\"TEXT\">namespace:function</field></block></value>")
				.appendToolBoxInit("<value name=\"x\"><block type=\"coord_x\"></block></value>")
				.appendToolBoxInit("<value name=\"y\"><block type=\"coord_y\"></block></value>")
				.appendToolBoxInit("<value name=\"z\"><block type=\"coord_z\"></block></value>")
				.appendDependency("world", BuiltInTypes.World)
				.setLanguage(en, "Run function silent %1 at x: %2 y: %3 z: %4 %5")
				.setLanguage(zh, "在x:%2 y:%3 z:%4%5处运行函数%1").initGenerator().buildAndOutput();

		//advanced
		factory.getToolKit().createOutputProcedure("math_plus_self", BuiltInTypes.Number)
				.setToolBoxId(BuiltInToolBoxId.Procedure.MATH).setColor(BuiltInBlocklyColor.MATH.toString())
				.appendArgs0InputValue("value", BuiltInTypes.Number).toolBoxInitBuilder().setName("value")
				.appendConstantNumber(0).buildAndReturn().setLanguage(en, "plus myself %1").setLanguage(zh, "自增%1")
				.initGenerator().buildAndOutput();
		factory.getToolKit().createOutputProcedure("options_camera_type", BuiltInTypes.Boolean)
				.setToolBoxId(BuiltInToolBoxId.Procedure.ADVANCED).appendArgs0FieldDropDown("cameraType",
						Map.of("FIRST_PERSON", "FIRST_PERSON", " THIRD_PERSON_BACK", " THIRD_PERSON_BACK", "THIRD_PERSON_FRONT",
								"THIRD_PERSON_FRONT")).appendArgs0FieldImage(BuiltInImages.CLIENT, 8, 12)
				.setLanguage(en, "is Player has %1 %2").setLanguage(zh, "玩家是%1 %2").initGenerator().buildAndOutput();
		//		factory.getToolKit().createOutputProcedure("advanced_test_lambda",BuiltInTypes.String).setColor(Color.GRAY.darker())
		//				.appendArgs0StatementInput("statement").statementBuilder().setName("statement").buildAndReturn()
		//				.setToolBoxId(BuiltInToolBoxId.Procedure.ADVANCED).setLanguage(en, "Do nothing %1").initGenerator()
		//				.buildAndOutput();
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
		factory.initGenerator(Generators.NEOFORGE1218);
		factory.getToolKit().clearGenerator();
	}
}
