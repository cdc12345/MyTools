package org.cdc.dev.sections;

import net.mcreator.preferences.PreferencesSection;
import net.mcreator.preferences.entries.BooleanEntry;

import java.util.HashMap;

public class DevUtilsSection extends PreferencesSection {

	private static DevUtilsSection INSTANCE;

	public static DevUtilsSection getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DevUtilsSection("devutils");
		;
		return INSTANCE;
	}

	private final BooleanEntry exportPluginsSensitives;
	private final BooleanEntry autoGenerateModifier;
	private final BooleanEntry watchFileChanged;
	private final BooleanEntry recordBase;

	private final HashMap<String, BooleanEntry> contextMenus;

	public DevUtilsSection(String preferencesIdentifier) {
		super(preferencesIdentifier);

		this.contextMenus = new HashMap<>();

		this.addEntry(autoGenerateModifier = new BooleanEntry("autoGenerateModifier", true));
		this.addEntry(watchFileChanged = new BooleanEntry("watchFileChanged", false));
		this.addEntry(recordBase = new BooleanEntry("recordBase", true));
		this.addEntry(exportPluginsSensitives = new BooleanEntry("exportPluginsSensitives", false));

		contextMenus.put("wrap_block_with_plus_one", this.addEntry(new BooleanEntry("wrap_block_with_plus_one", true)));
		contextMenus.put("copy_selected_block_as_xml",
				this.addEntry(new BooleanEntry("copy_selected_block_as_xml", false)));
		contextMenus.put("replace_with_iterator", this.addEntry(new BooleanEntry("replace_with_iterator", false)));
		contextMenus.put("wrap_text_in_join", this.addEntry(new BooleanEntry("wrap_text_in_join", true)));
		contextMenus.put("wrap_entity_in_compare", this.addEntry(new BooleanEntry("wrap_entity_in_compare", false)));
		contextMenus.put("variables_set_to_get", this.addEntry(new BooleanEntry("variables_set_to_get", true)));
		contextMenus.put("variables_get_to_set", this.addEntry(new BooleanEntry("variables_get_to_set", true)));
		contextMenus.put("wrap_compare_mcitems", this.addEntry(new BooleanEntry("wrap_compare_mcitems", true)));
		contextMenus.put("wrap_controls_if", this.addEntry(new BooleanEntry("wrap_controls_if", true)));
		contextMenus.put("wrap_math_binary_ops", this.addEntry(new BooleanEntry("wrap_math_binary_ops", true)));
		contextMenus.put("wrap_logic_negate", this.addEntry(new BooleanEntry("wrap_logic_negate", true)));
	}

	@Override public String getSectionKey() {
		return "devUtilsSection";
	}

	public BooleanEntry getExportPluginsSensitives() {
		return exportPluginsSensitives;
	}

	public BooleanEntry getRecordBase() {
		return recordBase;
	}

	public boolean isAutoGenerateModifier() {
		return autoGenerateModifier.get();
	}

	public boolean isWatchedFileChanged() {
		return watchFileChanged.get();
	}

	public boolean isEnableContextMenu(String key) {
		if (contextMenus.containsKey(key)) {
			return contextMenus.get(key).get();
		} else {
			return true;
		}
	}

}
