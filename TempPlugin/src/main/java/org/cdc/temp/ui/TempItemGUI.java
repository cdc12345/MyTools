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

    public TempItemGUI(MCreator mcreator) {
        super(mcreator);

        this.initGUI();
        this.finalizeGUI();
    }

    protected void initGUI() {
        JPanel config = new JPanel(new GridLayout(4,1));
        config.setOpaque(false);

        readableName = new VTextField();
        readableName.setPreferredSize(new Dimension(480,readableName.getFontMetrics(readableName.getFont()).getHeight() + 20));
        readableName.setOpaque(false);
        config.add(PanelUtils.centerAndEastElement(L10N.label("gui.item.readablename"),readableName));

        type = new VComboBox<>(new String[]{"block","item"});
        type.setPreferredSize(readableName.getPreferredSize());
        type.setOpaque(false);
        config.add(PanelUtils.centerAndEastElement(L10N.label("gui.item.type"),type));

        code = new VComboBox<>(Arrays.stream(TempItem.CodeConstants.values()).map(Object::toString).toList().toArray(new String[0]));
        code.setPreferredSize(readableName.getPreferredSize());
        code.setEditable(true);
        code.setSelectedItem("Blocks.AIR");
        type.addItemListener(e -> {
            if ("item".equals(type.getSelectedItem())) {
                code.setSelectedItem("Items.AIR");
            } else if ("block".equals(type.getSelectedItem())) {
                code.setSelectedItem("Blocks.AIR");
            }
        });
        config.add(PanelUtils.centerAndEastElement(new JLabel("Code: "),code));

        registryName = new VTextField();
        registryName.setPreferredSize(readableName.getPreferredSize());
        registryName.setOpaque(false);
        config.add(PanelUtils.centerAndEastElement(L10N.label("gui.item.registryname"),registryName));

        JPanel tip = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tip.setOpaque(false);
        tip.add(new JLabel(L10N.t("gui.tip.donotforge")));


        add("Center",PanelUtils.totalCenterInPanel(config));
        add("South",tip);
    }

    public TempItem getElementFromGUI() {
        TempItem tempItem = new TempItem();
        tempItem.readableName = readableName.getText();
        tempItem.type = Objects.requireNonNull(type.getSelectedItem());
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

    @Override public void fillGUIFromElement(TempItem tempElement) {
        this.readableName.setText(tempElement.readableName);
        this.type.setSelectedItem(tempElement.type);
        this.code.setSelectedItem(tempElement.code);
        this.registryName.setText(tempElement.registryName);
    }
}
