package org.cdc.datagen.types;

import org.cdc.framework.interfaces.IVariableType;

/**
 * <a href="https://mcreator.net/plugin/118544/arraylists">Plugin Page</a>
 */
public enum ArrayListsType implements IVariableType {
	INSTANCE;
	@Override public String getBlocklyVariableType() {
		return "ArrayList";
	}

	@Override public String getVariableType() {
		return "arraylist";
	}
}
