package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import org.cdc.temp.element.TempBiome;

public class TempBiomeGUI extends ReadableNameAndPathGUI<TempBiome> {

	public TempBiomeGUI(MCreator mcreator) {
		super(mcreator);

		this.initGUI();
		this.finalizeGUI();
	}

	@Override TempBiome getElementFromGUI() {
		return new TempBiome(getReadableName(), getRegistryName());
	}

	@Override public String getDefaultViewName() {
		return "TempBiome";
	}
}
