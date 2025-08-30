private static Entity deserializeEntity(LevelAccessor levelAccessor, String entityJson, Consumer<Entity> consumer) {
	var jsonObject = new com.google.gson.Gson().fromJson(entityJson, com.google.gson.JsonObject.class);
	var compound = CompoundTag.CODEC.decode(new com.mojang.serialization.Dynamic<>(JsonOps.INSTANCE, jsonObject)).getOrThrow().getFirst();
	String entityId = compound.getString("id");
	var entity = levelAccessor.registryAccess().get(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(entityId))).get().value().create(
			(Level) levelAccessor, EntitySpawnReason.EVENT);
	entity.load(compound);
	entity.setUUID(UUID.randomUUID());
	consumer.accept(entity);
	return entity;
}