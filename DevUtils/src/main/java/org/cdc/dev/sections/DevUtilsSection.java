package org.cdc.dev.sections;

import net.mcreator.preferences.PreferencesSection;
import net.mcreator.preferences.entries.BooleanEntry;

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

	public DevUtilsSection(String preferencesIdentifier) {
		super(preferencesIdentifier);

		this.addEntry(autoGenerateModifier = new BooleanEntry("autoGenerateModifier", true));
		this.addEntry(watchFileChanged = new BooleanEntry("watchFileChanged", false));
		this.addEntry(recordBase = new BooleanEntry("recordBase", true));
		this.addEntry(exportPluginsSensitives = new BooleanEntry("exportPluginsSensitives", false));
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
}
