private static String serializeEntity(Entity sourceEntity) {
		CompoundTag compoundTag = new CompoundTag();
		sourceEntity.save(compoundTag);
		return CompoundTag.CODEC.encode(compoundTag, JsonOps.INSTANCE, new com.google.gson.JsonObject()).getOrThrow().toString();
}