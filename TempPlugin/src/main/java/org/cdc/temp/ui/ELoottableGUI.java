package org.cdc.temp.ui;

import net.mcreator.minecraft.DataListLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.modgui.LootTableGUI;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ELoottableGUI extends LootTableGUI {

	public ELoottableGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
	}

	public JComboBox<String> getTypeComboBox() throws IllegalAccessException, NoSuchFieldException {
		Field fl = LootTableGUI.class.getDeclaredField("type");
		fl.setAccessible(true);
		var com = (JComboBox<String>) fl.get(this);
		fl.setAccessible(false);
		return  com;
	}

	public VComboBox<String> getNameComboBox() throws NoSuchFieldException, IllegalAccessException {
		Field fl = LootTableGUI.class.getDeclaredField("name");
		fl.setAccessible(true);
		var com = (VComboBox<String>) fl.get(this);
		fl.setAccessible(false);
		return com;
	}

	@Override protected void initGUI() {
		JComboBox<String> type;
		VComboBox<String> name;
		try {
			type = getTypeComboBox();
			name = getNameComboBox();
			var model = (DefaultComboBoxModel<String>) type.getModel();
			HashMap<String, String> stringStringHashMap = new HashMap<>();
			var list = DataListLoader.loadDataList("loottabletypes").stream().map(a -> {
						var name1 = a.getName().replaceAll("_", " ");
						if (a.getOther() != null && a.getOther() instanceof Map<?,?> map && map.containsKey("name")){
							stringStringHashMap.put(name1,map.get("name").toString());
						} else {
							stringStringHashMap.put(name1,name1.toLowerCase() + "/{0}");
						}
						return name1;
					}).toList();
			model.addAll(list);

			type.addActionListener(a -> {
				String currName = name.getEditor().getItem().toString();
				String currNameNoType = currName == null ? "" : currName.split("/")[currName.split("/").length - 1];
				var element = Objects.requireNonNull(type.getSelectedItem()).toString();
				if (stringStringHashMap.containsKey(element)) {
					name.getEditor().setItem(MessageFormat.format(stringStringHashMap.get(element), currNameNoType));
				}
			});
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		super.initGUI();
	}
}
