package org.cdc.toolbox.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.cdc.toolbox.element.MyEnchantment;

import net.mcreator.element.types.Enchantment;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.modgui.EnchantmentGUI;
import net.mcreator.workspace.elements.ModElement;

public class MyEnchantmentGUI extends EnchantmentGUI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JSpinner damageAddon;

	public MyEnchantmentGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		
	}
	
	@Override
	protected void initGUI() {
		super.initGUI();
		
		JPanel extension = new JPanel(new BorderLayout());
		
		extension.setOpaque(false);
		
		JPanel selp1 = new JPanel(new GridLayout(1, 2));
		selp1.setOpaque(false);
		
		this.damageAddon  = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));
		this.damageAddon.setOpaque(false);
		
		selp1.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/damage_modifier"),
				L10N.label("elementgui.enchantment.damage_bonus")));
		selp1.add(damageAddon);
		
		extension.add(PanelUtils.totalCenterInPanel(selp1));
		
		addPage("extension", extension);
	}
	
	@Override
	public void openInEditingMode(Enchantment enchantment) {
		super.openInEditingMode(enchantment);
		
		if (enchantment instanceof MyEnchantment my) {
			this.damageAddon.setValue(my.damageAddon);
		}
	}
	
	@Override
	public Enchantment getElementFromGUI() {
		var en = super.getElementFromGUI();
		var my = new MyEnchantment(modElement);
		my.name = en.name;
		my.anvilCost = en.anvilCost;
		my.canGenerateInLootTables = en.canGenerateInLootTables;
		my.canVillagerTrade = en.canVillagerTrade;
		my.damageModifier = en.damageModifier;
		my.incompatibleEnchantments = en.incompatibleEnchantments;
		my.isCurse = en.isCurse;
		my.isTreasureEnchantment = en.isTreasureEnchantment;
		my.maxLevel = en.maxLevel;
		my.supportedItems = en.supportedItems;
		my.supportedSlots = en.supportedSlots;
		my.weight = en.weight;
		my.damageAddon = (int)this.damageAddon.getValue();
		return my;
	}

}
