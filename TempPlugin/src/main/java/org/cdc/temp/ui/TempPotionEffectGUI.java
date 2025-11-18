package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.UIRES;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdc.temp.element.TempPotionEffect;

import javax.swing.*;
import java.util.Arrays;

public class TempPotionEffectGUI extends ReadableNameAndPathGUI<TempPotionEffect> {

	private static final Logger log = LogManager.getLogger(TempPotionEffectGUI.class);

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
		try {
			this.code.setSelectedItem(TempPotionEffect.CodeConstants.valueOf(
					generator.getGeneratorConfiguration().getGeneratorFlavor().name()).toString());
		} catch (Exception ignored){
			log.info("Missing {}",generator.getGeneratorName());
		}
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
