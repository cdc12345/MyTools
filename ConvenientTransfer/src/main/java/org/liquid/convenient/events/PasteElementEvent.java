package org.liquid.convenient.events;

import com.google.gson.JsonObject;
import net.mcreator.plugin.MCREvent;
import net.mcreator.ui.MCreator;

public class PasteElementEvent extends MCREvent {
	private final MCreator mCreator;
	private final JsonObject originalPasteElement;
	private final JsonObject currentPasteElement;

	public PasteElementEvent(MCreator mcreator,JsonObject currentPasteElement){
		this.mCreator = mcreator;
		this.originalPasteElement = currentPasteElement;
		this.currentPasteElement = currentPasteElement;
	}

	public MCreator getMCreator() {
		return mCreator;
	}

	public JsonObject getOriginalPasteElement() {
		return originalPasteElement.deepCopy();
	}

	public JsonObject getCurrentPasteElement() {
		return currentPasteElement;
	}
}
