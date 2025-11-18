package org.cdc.temp.element;

public record TempParticle(String readable_name, String registry_name, String code) {
	public enum CodeConstants {
		NEOFORGE("(BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse(\"%s\")).get().value() instanceof SimpleParticleType particleType)?particleType:null"),
		FORGE("ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(\"%s\")) instanceof SimpleParticleType simpleParticleType? simpleParticleType:null");
		private final String code;

		CodeConstants(String code) {
			this.code = code;
		}

		@Override public String toString() {
			return code;
		}
	}
}
