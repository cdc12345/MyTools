package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import org.cdc.temp.element.TempStructure;

public class TempStructureGUI extends ReadableNameAndPathGUI<TempStructure> {

	public TempStructureGUI(MCreator mcreator) {
		super(mcreator);

		this.initGUI();
		this.finalizeGUI();
	}

	@Override TempStructure getElementFromGUI() {
		return new TempStructure(readableName.getText(), path.getText());
	}

	@Override public String getDefaultViewName() {
		return "TempStructure";
	}
}
