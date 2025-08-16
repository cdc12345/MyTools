/*@int*/((new Object() {
  public int get(LevelAccessor levelAccessor, float partialTick) {
    if (levelAccessor instanceof ClientLevel) {
      int color = (int) (((ClientLevel) levelAccessor).getStarBrightness(partialTick) * 255.0F);
      return color << 24 | color << 16 | color << 8 | color;
    }
    return 0;
  }
}).get(world, (float) partialTick))