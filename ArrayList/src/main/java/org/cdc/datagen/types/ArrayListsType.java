package org.cdc.datagen.types;

import org.cdc.framework.interfaces.IVariableType;

public enum ArrayListsType implements IVariableType {
	INSTANCE;
	@Override public String getBlocklyVariableType() {
		return "ArrayList";
	}

	@Override public String getVariableType() {
		return "arraylist";
	}
}
