package org.cdc.dev.sections;

import net.mcreator.preferences.PreferencesSection;
import net.mcreator.preferences.entries.BooleanEntry;

public class DevUtilsSection extends PreferencesSection {

	private static DevUtilsSection INSTANCE;

	public static DevUtilsSection getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DevUtilsSection("devutils");;
		return INSTANCE;
	}

	private BooleanEntry exportPluginsSensitives;

	public DevUtilsSection(String preferencesIdentifier) {
		super(preferencesIdentifier);

		this.addEntry(exportPluginsSensitives = new BooleanEntry("exportPluginsSensitives",false));
	}

	@Override public String getSectionKey() {
		return "devUtilsSection";
	}

	public BooleanEntry getExportPluginsSensitives() {
		return exportPluginsSensitives;
	}
}
