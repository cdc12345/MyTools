package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.UIRES;
import org.cdc.temp.element.TempAchievement;

import javax.swing.*;

public class TempAchievementGUI extends ReadableNameAndPathGUI<TempAchievement> {

	public TempAchievementGUI(MCreator mcreator) {
		super(mcreator);

		this.initGUI();
		this.finalizeGUI();
	}

	@Override TempAchievement getElementFromGUI() {
		return new TempAchievement(readableName.getText(),path.getText());
	}

	@Override protected String getDefaultViewName() {
		return "TempAchievement";
	}

	@Override public ImageIcon getViewIcon() {
		return UIRES.get("mod_types.achievement");
	}
}
