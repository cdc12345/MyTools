package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.UIRES;
import org.cdc.temp.element.TempPotionEffect;

import javax.swing.*;
import java.util.Arrays;

public class TempPotionEffectGUI extends ReadableNameAndPathGUI<TempPotionEffect> {

	public TempPotionEffectGUI(MCreator mcreator) {
		super(mcreator);

		initGUI();
		finalizeGUI();
	}

	@Override void initGUI() {
		super.initGUI();
		this.code.setModel(new DefaultComboBoxModel<>(
				Arrays.stream(TempPotionEffect.CodeConstants.values()).map(TempPotionEffect.CodeConstants::toString)
						.toArray(String[]::new)));
		this.code.setEditable(true);
	}

	@Override protected String getDefaultViewName() {
		return "TempPotionEffect";
	}

	@Override TempPotionEffect getElementFromGUI() {
		return new TempPotionEffect(getReadableName(), getRegistryName(), getCode());
	}

	@Override public ImageIcon getViewIcon() {
		return UIRES.get("mod_types.potioneffect");
	}

	@Override public boolean needCode() {
		return true;
	}
}
