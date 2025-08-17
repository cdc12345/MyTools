package org.cdc.datagen.types;

import org.cdc.framework.interfaces.IVariableType;

public class AttributeModifierType implements IVariableType {
	private static AttributeModifierType INSTANCE;

	public static AttributeModifierType getInstance() {
		if (INSTANCE == null)
			INSTANCE = new AttributeModifierType();
		return INSTANCE;
	}

	@Override public String getBlocklyVariableType() {
		return "AttributeModifier";
	}

	@Override public String getVariableType() {
		return "attributemodifier";
	}
}
