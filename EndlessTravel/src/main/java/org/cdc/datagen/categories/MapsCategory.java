package org.cdc.datagen.categories;

import org.cdc.framework.interfaces.IProcedureCategory;

public enum MapsCategory implements IProcedureCategory {
	INSTANCE;

	@Override public String getName() {
		return "maps";
	}

	@Override public String getDefaultColor() {
		return "45";
	}
}
