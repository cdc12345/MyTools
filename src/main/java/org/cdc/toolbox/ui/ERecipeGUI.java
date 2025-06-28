package org.cdc.toolbox.ui;

import net.mcreator.element.types.Recipe;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.modgui.RecipeGUI;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;

public class ERecipeGUI extends RecipeGUI {
	private JComboBox<String> namespace1;

	public ERecipeGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
	}

	@Override protected void initGUI() {
		super.initGUI();
		namespace1 = new JComboBox<>(new String[] { "none" });
		JPanel addition = new JPanel(new GridLayout(1,2));
		namespace1.setEditable(true);
		namespace1.setOpaque(false);
		addition.add(PanelUtils.totalCenterInPanel(PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("recipe/namespace"),
						L10N.label("elementgui.recipe.name_space")), namespace1)));
		addition.setOpaque(false);
		super.addPage("addition",addition);
	}

	@Override public Recipe getElementFromGUI() {
		Recipe recipe = super.getElementFromGUI();
		if (!"none".equals(namespace1.getSelectedItem())){
			recipe.namespace = namespace1.getSelectedItem().toString();
		}
		return recipe;
	}
}
