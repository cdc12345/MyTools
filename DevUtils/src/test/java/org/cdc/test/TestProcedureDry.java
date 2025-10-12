package org.cdc.test;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestProcedureDry {
	public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
		var xml = "<xml xmlns=\"https://developers.google.com/blockly/xml\"><variables><variable type=\"ObjectList\" id=\"list\">list</variable><variable type=\"ObjectList\" id=\"list2\">list2</variable><variable type=\"ArrayList\" id=\"newlist\">newlist</variable></variables><block type=\"event_trigger\" deletable=\"false\" x=\"0\" y=\"0\"><field name=\"trigger\">no_ext_trigger</field><next><block type=\"text_print\"><value name=\"TEXT\"><block type=\"variables_get_objectlist\"><mutation xmlns=\"http://www.w3.org/1999/xhtml\" is_player_var=\"false\" has_entity=\"false\"></mutation><field name=\"VAR\">local:list</field></block></value><next><block type=\"list_add\"><value name=\"element\"><block type=\"empty_itemstack\"></block></value><value name=\"list\"><block type=\"variables_get_objectlist\"><mutation xmlns=\"http://www.w3.org/1999/xhtml\" is_player_var=\"false\" has_entity=\"false\"></mutation><field name=\"VAR\">local:list</field></block></value><next><block type=\"list_add\"><value name=\"element\"><block type=\"text\"><field name=\"TEXT\">123</field></block></value><value name=\"list\"><block type=\"variables_get_objectlist\"><mutation xmlns=\"http://www.w3.org/1999/xhtml\" is_player_var=\"false\" has_entity=\"false\"></mutation><field name=\"VAR\">local:list</field></block></value><next><block type=\"list_add\"><value name=\"element\"><block type=\"math_number\"><field name=\"NUM\">0</field></block></value><value name=\"list\"><block type=\"variables_get_objectlist\"><mutation xmlns=\"http://www.w3.org/1999/xhtml\" is_player_var=\"false\" has_entity=\"false\"></mutation><field name=\"VAR\">local:list</field></block></value><next><block type=\"list_for_each\"><value name=\"for_list\"><block type=\"variables_get_objectlist\"><mutation xmlns=\"http://www.w3.org/1999/xhtml\" is_player_var=\"false\" has_entity=\"false\"></mutation><field name=\"VAR\">local:list</field></block></value><value name=\"_placeholder\"><block type=\"index_of_list\"><field name=\"mark\">1</field></block></value><statement name=\"for_each\"><block type=\"text_print\"><value name=\"TEXT\"><block type=\"list_get\"><field name=\"type\">Text</field><value name=\"index\"><block type=\"index_of_list\"><field name=\"mark\">1</field></block></value><value name=\"list\"><block type=\"variables_get_objectlist\"><mutation xmlns=\"http://www.w3.org/1999/xhtml\" is_player_var=\"false\" has_entity=\"false\"></mutation><field name=\"VAR\">local:list</field></block></value></block></value></block></statement><next><block type=\"text_print\"><value name=\"TEXT\"><block type=\"list_get\"><field name=\"type\">ItemStack</field><value name=\"index\"><block type=\"math_number\"><field name=\"NUM\">0</field></block></value><value name=\"list\"><block type=\"variables_get_objectlist\"><mutation xmlns=\"http://www.w3.org/1999/xhtml\" is_player_var=\"false\" has_entity=\"false\"></mutation><field name=\"VAR\">local:list</field></block></value></block></value><next><block type=\"list_merge\"><value name=\"origin\"><block type=\"variables_get_objectlist\"><mutation xmlns=\"http://www.w3.org/1999/xhtml\" is_player_var=\"false\" has_entity=\"false\"></mutation><field name=\"VAR\">local:list</field></block></value><value name=\"target\"><block type=\"variables_get_objectlist\"><mutation xmlns=\"http://www.w3.org/1999/xhtml\" is_player_var=\"false\" has_entity=\"false\"></mutation><field name=\"VAR\">local:list2</field></block></value><next><block type=\"list_merge\"><value name=\"origin\"><block type=\"variables_get_objectlist\"><mutation xmlns=\"http://www.w3.org/1999/xhtml\" is_player_var=\"false\" has_entity=\"false\"></mutation><field name=\"VAR\">local:list</field></block></value><value name=\"target\"><block type=\"list_split_string\"><value name=\"seperator\"><block type=\"text\"><field name=\"TEXT\">,</field></block></value><value name=\"text\"><block type=\"text\"><field name=\"TEXT\">1,2,3,4</field></block></value></block></value><next><block type=\"text_print\"><value name=\"TEXT\"><block type=\"list_index_of\"><value name=\"value\"><block type=\"text\"><field name=\"TEXT\">123</field></block></value><value name=\"list\"><block type=\"variables_get_objectlist\"><mutation xmlns=\"http://www.w3.org/1999/xhtml\" is_player_var=\"false\" has_entity=\"false\"></mutation><field name=\"VAR\">local:list</field></block></value></block></value><next><block type=\"text_print\"><value name=\"TEXT\"><block type=\"list_index_of\"><value name=\"value\"><block type=\"text\"><field name=\"TEXT\">element</field></block></value><value name=\"list\"><block type=\"arraylists_compatible_with_objectslist\"><value name=\"arraylists_var\"><block type=\"variables_get_arraylist\"><mutation xmlns=\"http://www.w3.org/1999/xhtml\" is_player_var=\"false\" has_entity=\"false\"></mutation><field name=\"VAR\">local:newlist</field></block></value></block></value></block></value><next><block type=\"text_print\"><value name=\"TEXT\"><block type=\"list_stream_to_string\"><field name=\"decorator\">Object::toString</field><value name=\"list\"><block type=\"variables_get_objectlist\"><mutation xmlns=\"http://www.w3.org/1999/xhtml\" is_player_var=\"false\" has_entity=\"false\"></mutation><field name=\"VAR\">local:list2</field></block></value><value name=\"delimiter\"><block type=\"text\"><field name=\"TEXT\">,</field></block></value><value name=\"prefix\"><block type=\"text\"><field name=\"TEXT\">[</field></block></value><value name=\"suffix\"><block type=\"text\"><field name=\"TEXT\">]</field></block></value></block></value><next><block type=\"text_print\"><value name=\"TEXT\"><block type=\"list_stream_to_string_end\"><value name=\"list\"><block type=\"variables_get_objectlist\"><mutation xmlns=\"http://www.w3.org/1999/xhtml\" is_player_var=\"false\" has_entity=\"false\"></mutation><field name=\"VAR\">local:list2</field></block></value><value name=\"delimiter\"><block type=\"text\"><field name=\"TEXT\">,</field></block></value><value name=\"prefix\"><block type=\"text\"><field name=\"TEXT\">[</field></block></value><value name=\"suffix\"><block type=\"text\"><field name=\"TEXT\">]</field></block></value><value name=\"decorator\"><block type=\"lambda_do\"><statement name=\"body\"><block type=\"lambda_set_result\"><value name=\"result\"><block type=\"text_join\"><mutation items=\"1\"></mutation><value name=\"ADD0\"><block type=\"lambda_arg\"><field name=\"index\">1</field></block></value></block></value></block></statement></block></value></block></value><next><block type=\"java_code\"><field name=\"CODE\">/*code*/</field></block></next></block></next></block></next></block></next></block></next></block></next></block></next></block></next></block></next></block></next></block></next></block></next></block></next></block></xml>";
		try {
			// 创建一个SAXParser
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			// 创建一个处理器
			DefaultHandler handler = new DefaultHandler() {
				private boolean inEmployee = false;
				private String name = null;
				private String position = null;

				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					if (qName.equalsIgnoreCase("block")) {
						inEmployee = true;
					}
				}

				public void endElement(String uri, String localName, String qName) throws SAXException {
					if (qName.equalsIgnoreCase("block")) {
						inEmployee = false;
						System.out.println("Employee Name: " + name);
						System.out.println("Position: " + position);
						System.out.println("---------------");
					}
				}

				public void characters(char[] ch, int start, int length) throws SAXException {
					if (inEmployee) {
						String data = new String(ch);
						System.out.println(data);
					}
				}
			};

			// 解析XML文件
			parser.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
