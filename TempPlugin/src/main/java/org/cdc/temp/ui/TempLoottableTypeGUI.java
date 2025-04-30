package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.component.VTextField;
import org.cdc.temp.element.TempLoottableType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class TempLoottableTypeGUI extends TempElementGUI<TempLoottableType>{

	protected VTextField typeName;
	protected VTextField nameModel;

	public TempLoottableTypeGUI(MCreator mcreator) {
		super(mcreator);

		this.initGUI();
		this.finalizeGUI();
	}

	@Override void initGUI() {
		JPanel jPanel = new JPanel(new GridLayout(2,2));

		typeName = new VTextField();
		jPanel.add(L10N.label("gui.item.typeName"));
		jPanel.add(typeName);

		nameModel = new VTextField();
		typeName.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) {
				nameModel.setText(typeName.getText().toLowerCase()+"/"+"{0}");
			}

			@Override public void removeUpdate(DocumentEvent e) {
				nameModel.setText(typeName.getText().toLowerCase()+"/"+"{0}");
			}

			@Override public void changedUpdate(DocumentEvent e) {
				nameModel.setText(typeName.getText().toLowerCase()+"/"+"{0}");
			}
		});
		jPanel.add(L10N.label("gui.item.nameModel"));
		jPanel.add(nameModel);

		JPanel tip = new JPanel(new FlowLayout(FlowLayout.CENTER));
		tip.setOpaque(false);
		tip.add(new JLabel(L10N.t("gui.tip.donotforge")));


		add("Center", PanelUtils.totalCenterInPanel(jPanel));
		add("South",tip);
	}

	@Override TempLoottableType getElementFromGUI() {
		TempLoottableType type = new TempLoottableType();
		type.typeName = typeName.getText();
		type.nameModel = nameModel.getText();
		return type;
	}

	@Override public String getViewName() {
		return "TempLoottableType";
	}
}
