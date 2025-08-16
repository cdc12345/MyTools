if (Minecraft.getInstance().player != null) {
  Entity entity = Minecraft.getInstance().player;
  double x = entity.getX();
  double y = entity.getY();
  double z = entity.getZ();
  LevelAccessor world = entity.level();
  ResourceKey<Level> dimension = entity.level().dimension();
  ${statement$do}
}