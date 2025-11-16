package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.UIRES;
import org.cdc.temp.element.TempPotion;

import javax.swing.*;
import java.util.Arrays;

public class TempPotionGUI extends ReadableNameAndPathGUI<TempPotion> {

	public TempPotionGUI(MCreator mcreator) {
		super(mcreator);

		initGUI();
		finalizeGUI();
	}

	@Override void initGUI() {
		super.initGUI();
		this.code.setModel(new DefaultComboBoxModel<>(
				Arrays.stream(TempPotion.CodeConstants.values()).map(TempPotion.CodeConstants::toString)
						.toArray(String[]::new)));
		this.code.setEditable(true);
	}

	@Override protected String getDefaultViewName() {
		return "TempPotion";
	}

	@Override TempPotion getElementFromGUI() {
		return new TempPotion(getReadableName(), getRegistryName(), getCode());
	}

	@Override public ImageIcon getViewIcon() {
		return UIRES.get("mod_types.potion");
	}

	@Override public boolean needCode() {
		return true;
	}
}
