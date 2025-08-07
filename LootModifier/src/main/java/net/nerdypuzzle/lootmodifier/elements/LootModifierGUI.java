package net.nerdypuzzle.lootmodifier.elements;

import net.mcreator.element.ModElementType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

public class LootModifierGUI extends ModElementGUI<LootModifier> {
    private final ValidationGroup page1group = new ValidationGroup();
    private final VTextField modifiedTable;
    private final SearchableComboBox<String> modifierTable;
    private final VComboBox<String> modifierMethod;
    private final JSpinner secondRate;


    public LootModifierGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode);
        modifiedTable = new VTextField(24);
        modifierTable = new SearchableComboBox<>();
        this.modifierMethod = new VComboBox<>(new String[]{"Append","Replace"});
        this.secondRate = new JSpinner();

        this.initGUI();
        super.finalizeGUI();
    }

    protected void initGUI() {
        JPanel pane1 = new JPanel(new BorderLayout());
        pane1.setOpaque(false);
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 0, 2));
        mainPanel.setOpaque(false);

        mainPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("lootmodifier/modified_table"), L10N.label("elementgui.lootmodifier.modified_table")));
        mainPanel.add(modifiedTable);
        mainPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("lootmodifier/modifier_table"), L10N.label("elementgui.lootmodifier.modifier_table")));
        mainPanel.add(modifierTable);
        mainPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("lootmodifier/modifier_method"),L10N.label("elementgui.lootmodifier.modifier_method")));
        mainPanel.add(modifierMethod);
        mainPanel.add(L10N.label("elementgui.lootmodifier.second_rate"));
        mainPanel.add(secondRate);


        modifiedTable.setValidator(new TextFieldValidator(modifiedTable, L10N.t("elementgui.lootmodified.modifier_needs_provider")));
        modifiedTable.enableRealtimeValidation();
        page1group.addValidationElement(modifiedTable);


        pane1.add(PanelUtils.totalCenterInPanel(mainPanel));
        addPage(pane1);
    }

    public void reloadDataLists() {
        super.reloadDataLists();
        ComboBoxUtil.updateComboBoxContents(modifierTable, this.mcreator.getWorkspace().getModElements().stream().filter((var) ->
				var.getType() == ModElementType.LOOTTABLE).map(ModElement::getName).collect(Collectors.toList()));
    }

    protected AggregatedValidationResult validatePage(int page) {
        if (modifierTable.getSelectedItem() == null)
            return new AggregatedValidationResult.FAIL(L10N.t("elementgui.lootmodifier.needs_modifier", new Object[0]));
        return new AggregatedValidationResult(this.page1group);
    }

    public void openInEditingMode(LootModifier modifier) {
        modifiedTable.setText(modifier.modifiedTable);
        modifierTable.setSelectedItem(modifier.modifierTable);
        modifierMethod.setSelectedItem(modifier.modifierMethod);
    }

    public LootModifier getElementFromGUI() {
        LootModifier modifier = new LootModifier(this.modElement);
        modifier.modifiedTable = modifiedTable.getText();
        modifier.modifierTable = modifierTable.getSelectedItem();
        modifier.modifierMethod = modifierMethod.getSelectedItem();
        return modifier;
    }

    @Override @Nullable public URI contextURL() throws URISyntaxException {
        return new URI("https://mcreator.net/plugin/103828/loot-modifier");
    }

}
