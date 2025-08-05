package org.cdc.datagen.types;

import org.cdc.framework.interfaces.IVariableType;

public enum ObjectMapType implements IVariableType {
	INSTANCE;

	@Override public String getBlocklyVariableType() {
		return "ObjectMap";
	}

	@Override public String getVariableType() {
		return "objectmap";
	}
}
