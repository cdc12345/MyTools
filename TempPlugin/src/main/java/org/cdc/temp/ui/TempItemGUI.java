package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import org.cdc.temp.element.TempItem;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class TempItemGUI extends TempElementGUI<TempItem> {
    private VTextField readableName;
    private VComboBox<String> type;
    private VComboBox<String> code;
    private VTextField registryName;

    private int profession;

    public TempItemGUI(MCreator mcreator) {
        super(mcreator);

        this.initGUI();
        this.finalizeGUI();
    }

    protected void initGUI() {
        JPanel config = new JPanel(new GridLayout(4,2));
        config.setOpaque(false);

        readableName = new VTextField();
        readableName.setOpaque(false);
        config.add(new JLabel(L10N.t("gui.item.readablename")));
        config.add(readableName);

        type = new VComboBox<>(new String[]{"block","item"});
        type.setOpaque(false);
        config.add(L10N.label("gui.item.type"));
        config.add(type);

        code = new VComboBox<>(Arrays.stream(TempItem.CodeConstants.values()).map(Object::toString).toList().toArray(new String[0]));
        code.setEditable(true);
        code.setSelectedItem("Blocks.AIR");
        type.addItemListener(e -> {
            if ("item".equals(type.getSelectedItem())) {
                code.setSelectedItem("Items.AIR");
            } else if ("block".equals(type.getSelectedItem())) {
                code.setSelectedItem("Blocks.AIR");
            }
        });
        config.add(new JLabel("Code: "));
        config.add(code);

        registryName = new VTextField();
        registryName.setOpaque(false);
        config.add(L10N.label("gui.item.registryname"));
        config.add(registryName);

        JPanel tip = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tip.setOpaque(false);
        tip.add(new JLabel(L10N.t("gui.tip.donotforge")));


        add("Center",PanelUtils.totalCenterInPanel(config));
        add("South",tip);
    }

    public TempItem getElementFromGUI() {
        TempItem tempItem = new TempItem();
        tempItem.readableName = readableName.getText();
        tempItem.type = Objects.requireNonNull(type.getSelectedItem()).toString();
        tempItem.code = Objects.toString(code.getSelectedItem());
        tempItem.registryName = registryName.getText();
        return tempItem;
    }

    @Override
    public String getViewName() {
        if (readableName.getText().isEmpty()) {
            return "TempPlugin";
        }
        return readableName.getText();
    }

    @Override public ImageIcon getViewIcon() {
        return UIRES.get("mod_types.item");
    }
}
