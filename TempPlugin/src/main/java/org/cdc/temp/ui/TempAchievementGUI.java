package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.validation.component.VTextField;
import org.cdc.temp.element.TempAchievement;

public class TempAchievementGUI extends ReadableNameAndPathGUI<TempAchievement> {

	private VTextField readableName;
	private VTextField path;

	public TempAchievementGUI(MCreator mcreator) {
		super(mcreator);

		this.initGUI();
		this.finalizeGUI();
	}

	@Override TempAchievement getElementFromGUI() {
		return new TempAchievement(readableName.getText(),path.getText());
	}

	@Override public String getViewName() {
		return "TempAchievement";
	}
}
