package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.component.VTextField;

import javax.swing.*;
import java.awt.*;

public abstract class ReadableNameAndPathGUI<T> extends TempElementGUI<T>{

	protected VTextField readableName;
	protected VTextField path;

	public ReadableNameAndPathGUI(MCreator mcreator) {
		super(mcreator);
	}

	@Override void initGUI() {
		JPanel config = new JPanel(new GridLayout(2, 2));
		config.setOpaque(false);

		readableName = new VTextField();
		readableName.setOpaque(false);
		config.add(new JLabel(L10N.t("gui.generally.readable_name")));
		config.add(readableName);

		path = new VTextField();
		path.setOpaque(false);
		config.add(new JLabel(L10N.t("gui.generally.registry_name")));
		config.add(path);

		JPanel tip = new JPanel(new FlowLayout(FlowLayout.CENTER));
		tip.setOpaque(false);
		tip.add(new JLabel(L10N.t("gui.tip.donotforge")));

		add("Center", PanelUtils.totalCenterInPanel(config));
		add("South",tip);
	}

	public String getReadableName(){
		return readableName.getText();
	}

	public String getRegistryName(){
		return path.getText();
	}
}
