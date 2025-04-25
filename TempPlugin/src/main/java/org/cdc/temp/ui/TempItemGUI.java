package org.cdc.temp.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.views.ViewBase;
import org.cdc.temp.element.TempItem;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class TempItemGUI extends ViewBase {

    private Consumer<TempItem> consumer;
    private JComboBox<String> type;
    private JTextField readableName;
    private JComboBox<String> code;
    private JTextField registryName;

    private int profession;

    public TempItemGUI(MCreator mcreator) {
        super(mcreator);

        this.initGUI();
    }

    protected void initGUI() {
        JPanel config = new JPanel(new GridLayout(4,2));
        config.setOpaque(false);

        readableName = new JTextField();
        readableName.setOpaque(false);
        config.add(new JLabel(L10N.t("gui.item.readablename")));
        config.add(readableName);

        type = new JComboBox<>(new String[]{"block","item"});
        type.setOpaque(false);
        config.add(L10N.label("gui.item.type"));
        config.add(type);

        code = new JComboBox<>(Arrays.stream(TempItem.CodeConstants.values()).map(Object::toString).toList().toArray(new String[0]));
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

        registryName = new JTextField();
        registryName.setOpaque(false);
        config.add(L10N.label("gui.item.registryname"));
        config.add(registryName);

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

        JPanel tip = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tip.setOpaque(false);
        tip.add(new JLabel(L10N.t("gui.tip.donotforge")));

        add("North",
                ComponentUtils.applyPadding(PanelUtils.westAndEastElement(toolBarLeft, toolBar), 5, true, false,
                        true, false));
        add("Center",PanelUtils.totalCenterInPanel(config));
        add("South",tip);
    }

    public void setOnSaved(Consumer<TempItem> consumer){
        this.consumer = consumer;
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
        return "TempPlugin";
    }
}
