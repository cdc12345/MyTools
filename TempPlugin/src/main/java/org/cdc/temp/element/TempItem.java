package org.cdc.temp.element;


public class TempItem {
    public String readableName;
    public String type;
    public String code;
    public String registryName;

    public enum CodeConstants{
        NEOFORGEBLOCK("BuiltInRegistries.BLOCK.get(ResourceLocation.parse(\"%s\")).get().value()"),NEOFORGEITEM("BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(\"%s\")).get().value()");
        private final String code;
        CodeConstants(String code){
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }
    }
}
