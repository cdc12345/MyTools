package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;

import javax.swing.*;
import java.awt.*;

public abstract class ReadableNameAndPathGUI<T> extends TempElementGUI<T> {

	protected VTextField readableName;
	protected VComboBox<String> code;
	protected VTextField path;

	public ReadableNameAndPathGUI(MCreator mcreator) {
		super(mcreator);
	}

	@Override void initGUI() {
		JPanel config = new JPanel(new GridLayout(3, 1));
		config.setOpaque(false);

		readableName = new VTextField();
		readableName.setPreferredSize(
				new Dimension(420, readableName.getFontMetrics(readableName.getFont()).getHeight() + 20));
		readableName.setOpaque(false);
		config.add(PanelUtils.centerAndEastElement(new JLabel(L10N.t("gui.generally.readable_name")), readableName));

		code = new VComboBox<>();
		code.setPreferredSize(readableName.getPreferredSize());
		code.setEnabled(needCode());
		code.setEditable(false);
		code.setOpaque(false);
		config.add(PanelUtils.centerAndEastElement(new JLabel("Code: "), code));

		path = new VTextField();
		path.setPreferredSize(readableName.getPreferredSize());
		path.setOpaque(false);
		path.setText("minecraft:");
		config.add(PanelUtils.centerAndEastElement(new JLabel(L10N.t("gui.generally.registry_name")), path));

		JPanel tip = new JPanel(new FlowLayout(FlowLayout.CENTER));
		tip.setOpaque(false);
		tip.add(new JLabel(L10N.t("gui.tip.donotforge")));

		add("Center", PanelUtils.totalCenterInPanel(config));
		add("South", tip);
	}

	public String getReadableName() {
		return readableName.getText();
	}

	public String getRegistryName() {
		return path.getText();
	}

	public String getCode() {return code.getSelectedItem();}

	public boolean needCode(){
		return false;
	}

	protected abstract String getDefaultViewName();

	@Override public String getViewName() {
		if (readableName.getText().isEmpty()) {
			return getDefaultViewName();
		} else {
			return readableName.getText();
		}
	}
}
