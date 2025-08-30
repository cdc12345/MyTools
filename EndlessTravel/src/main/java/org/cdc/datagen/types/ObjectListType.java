package org.cdc.datagen.types;

import org.cdc.framework.interfaces.IVariableType;

public enum ObjectListType implements IVariableType {
	INSTANCE;
	@Override public String getBlocklyVariableType() {
		return "ObjectList";
	}

	@Override public String getVariableType() {
		return "objectlist";
	}
}
