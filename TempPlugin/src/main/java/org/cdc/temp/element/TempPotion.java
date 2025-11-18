package org.cdc.temp.element;

public record TempPotion(String readable_name, String registry_name, String code) {
	public enum CodeConstants {
		NEOFORGE("BuiltInRegistries.POTION.get(ResourceLocation.parse(\"%s\")).get().getDelegate()"),
		FORGE("ForgeRegistries.POTIONS.getValue(new ResourceLocation(\"%s\"))");
		private final String code;

		CodeConstants(String code) {
			this.code = code;
		}

		@Override public String toString() {
			return code;
		}
	}

}
