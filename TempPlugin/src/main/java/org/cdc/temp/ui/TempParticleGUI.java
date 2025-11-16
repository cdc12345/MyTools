package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.UIRES;
import org.cdc.temp.element.TempParticle;

import javax.swing.*;
import java.util.Arrays;

public class TempParticleGUI extends ReadableNameAndPathGUI<TempParticle> {

	public TempParticleGUI(MCreator mcreator) {
		super(mcreator);

		initGUI();
		finalizeGUI();
	}

	@Override void initGUI() {
		super.initGUI();
		this.code.setModel(new DefaultComboBoxModel<>(
				Arrays.stream(TempParticle.CodeConstants.values()).map(TempParticle.CodeConstants::toString)
						.toArray(String[]::new)));
		this.code.setEditable(true);
	}

	@Override protected String getDefaultViewName() {
		return "TempParticle";
	}

	@Override TempParticle getElementFromGUI() {
		return new TempParticle(getReadableName(), getRegistryName(), getCode());
	}

	@Override public ImageIcon getViewIcon() {
		return UIRES.get("mod_types.particle");
	}

	@Override public boolean needCode() {
		return true;
	}
}
