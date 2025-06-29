package org.cdc.datagen.categories;

import org.cdc.framework.interfaces.IProcedureCategory;

public enum ListCategory implements IProcedureCategory {
	INSTANCE;

	@Override public String getName() {
		return "list";
	}
}
