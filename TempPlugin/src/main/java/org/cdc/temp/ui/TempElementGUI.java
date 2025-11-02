package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.views.ViewBase;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public abstract class TempElementGUI<G> extends ViewBase {
	protected Consumer<G> consumer;

	private Runnable redo;

	protected TempElementGUI(MCreator mcreator) {
		super(mcreator);
		redo = () -> {};
	}

	abstract void initGUI();

	protected final void finalizeGUI() {
		JButton save = L10N.button("elementgui.save_mod_element");
		save.setMargin(new Insets(1, 40, 1, 40));
		save.setBackground(Theme.current().getInterfaceAccentColor());
		save.setForeground(Theme.current().getSecondAltBackgroundColor());
		save.addActionListener(event -> consumer.accept(getElementFromGUI()));

		JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		JPanel toolBarLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		toolBarLeft.setOpaque(false);
		toolBar.setOpaque(false);
		toolBar.add(save);

		add("North",
				ComponentUtils.applyPadding(PanelUtils.westAndEastElement(toolBarLeft, toolBar), 5, true, false, true,
						false));
	}

	public void setOnSaved(Consumer<G> consumer) {
		this.consumer = consumer;
	}

	abstract G getElementFromGUI();

	public Runnable getRedo() {
		return redo;
	}

	public void setRedo(Runnable redo) {
		this.redo = redo;
	}
}
