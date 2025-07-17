package org.cdc.datagen.variables;

import org.cdc.framework.interfaces.IVariableType;

public class JsonElementType implements IVariableType {

	private static JsonElementType INSTANCE;

	public static JsonElementType getInstance() {
		if (INSTANCE == null)
			INSTANCE = new JsonElementType();
		return INSTANCE;
	}

	@Override public String getBlocklyVariableType() {
		return "JsonElement";
	}

	@Override public String getVariableType() {
		return "jsonelment";
	}
}
