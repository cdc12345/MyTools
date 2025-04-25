package org.cdc.temp.element;


public class TempItem {
    public String readableName;
    public String type;
    public String code;
    public String registryName;

    public enum CodeConstants{
        NEOFORGE("BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(%s,%s))"),NEOFORGEITEM("BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(%s,%s))");
        private String code;
        CodeConstants(String code){
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }
    }
}
