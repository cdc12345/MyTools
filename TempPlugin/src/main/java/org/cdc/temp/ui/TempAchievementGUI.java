package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import org.cdc.temp.element.TempAchievement;

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
}
