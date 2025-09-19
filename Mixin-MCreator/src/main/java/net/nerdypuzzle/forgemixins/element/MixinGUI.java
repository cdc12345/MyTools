package net.nerdypuzzle.forgemixins.element;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class MixinGUI extends ModElementGUI<Mixin> {
    private JCheckBox client;
    private VComboBox<String> target;

    public MixinGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode);

		this.setModElementCreatedListener(a->{
			a.getModElement().setCodeLock(true);
		});

        this.initGUI();
        this.finalizeGUI();
    }

	@Override public void validate() {
		super.validate();
	}

	protected void initGUI() {
        JPanel page = new JPanel(new BorderLayout(10,10));
        JPanel control = new JPanel(new GridLayout(2,2,10,10));
        target = new VComboBox<>(new String[]{"Minecraft","TitleScreen","SelectWorldScreen"});
        target.setEditable(true);
		target.setSelectedItem(modElement.getName());
        control.add(new JLabel("Mixin Target: "));
        control.add(target);
        client = new JCheckBox("click to enable");
        control.add(new JLabel("Is Client: "));
        control.add(client);

        control.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
                "Mixin Properties", 0, 0, control.getFont().deriveFont(12.0f),
                Theme.current().getForegroundColor()));
        page.add("Center", PanelUtils.totalCenterInPanel(control));
        this.addPage("Mixin Config",page);
    }

	public void openInEditingMode(Mixin generatableElement) {
        client.setSelected(generatableElement.isClient);
        if (generatableElement.mixinClass != null) {
            target.setSelectedItem(generatableElement.mixinClass);
        }
    }

    @Override
    public Mixin getElementFromGUI() {
        var mixin = new Mixin(modElement);
        mixin.isClient = client.isSelected();
        mixin.mixinClass = target.getSelectedItem();
        return mixin;
    }

	@Nullable @Override public URI contextURL() throws URISyntaxException {
		return null;
	}
}
