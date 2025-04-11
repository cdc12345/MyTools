package org.cdc.datagen.elements;

import org.cdc.framework.interfaces.IVariableType;

public enum VariableTypes implements IVariableType {
    ATOMIC_NUMBER("AtomicNumber","atomicnumber");

    private final String blocklyVariableType;
    private final String variableType;

    VariableTypes(String blocklyVariableType,String variableType){
        this.blocklyVariableType = blocklyVariableType;
        this.variableType = variableType;
    }

    @Override
    public String getBlocklyVariableType() {
        return blocklyVariableType;
    }

    @Override
    public String getVariableType() {
        return variableType;
    }
}
