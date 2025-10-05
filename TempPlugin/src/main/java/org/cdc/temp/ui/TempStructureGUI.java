package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.validation.component.VTextField;
import org.cdc.temp.element.TempStructure;

public class TempStructureGUI extends ReadableNameAndPathGUI<TempStructure> {
	private VTextField readableName;
	private VTextField registryName;

	public TempStructureGUI(MCreator mcreator) {
		super(mcreator);

		this.initGUI();
		this.finalizeGUI();
	}

	@Override TempStructure getElementFromGUI() {
		return new TempStructure(readableName.getText(), registryName.getText());
	}

	@Override public String getViewName() {
		return "TempStructure";
	}
}
