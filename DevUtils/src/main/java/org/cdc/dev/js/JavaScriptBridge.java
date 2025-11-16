package org.cdc.dev.js;

import net.mcreator.ui.init.L10N;
import org.cdc.dev.sections.DevUtilsSection;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class JavaScriptBridge {
	public void setClipboard(String text) {
		System.out.println(text);
		StringSelection stringSelection = new StringSelection(text);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
	}

	public String t(String key){
		return L10N.t(key);
	}

	public boolean isEnableContextMenu(String key){
		return DevUtilsSection.getInstance().isEnableContextMenu(key);
	}
}
