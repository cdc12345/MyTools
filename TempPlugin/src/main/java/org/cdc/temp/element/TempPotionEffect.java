package org.cdc.temp.element;

public record TempPotionEffect(String readable_name, String registry_name, String code) {
	public enum CodeConstants {
		NEOFORGE("BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(\"%s\")).get().getDelegate()"), FORGE(
				"ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(\"%s\"))");
		private final String code;

		CodeConstants(String code) {
			this.code = code;
		}

		@Override public String toString() {
			return code;
		}
	}
}
