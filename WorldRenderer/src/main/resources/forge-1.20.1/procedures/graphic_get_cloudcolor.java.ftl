/*@int*/((new Object() {
  public int get(LevelAccessor levelAccessor, float partialTick) {
    if (levelAccessor instanceof ClientLevel) {
      Vec3 color = ((ClientLevel) levelAccessor).getCloudColor(partialTick);
      return 204 << 24 | (int) (color.x() * 255.0D) << 16 | (int) (color.y() * 255.0D) << 8 | (int) (color.z() * 255.0D);
    }
    return 0;
  }
}).get(world, (float) partialTick))