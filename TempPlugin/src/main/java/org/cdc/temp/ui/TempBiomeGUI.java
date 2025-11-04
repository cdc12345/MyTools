package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.UIRES;
import org.cdc.temp.element.TempBiome;

import javax.swing.*;

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

	@Override public ImageIcon getViewIcon() {
		return UIRES.get("mod_types.biome");
	}
}
