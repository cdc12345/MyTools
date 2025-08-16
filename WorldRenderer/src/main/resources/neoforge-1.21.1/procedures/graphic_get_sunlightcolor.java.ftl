/*@int*/((new Object() {
  public int get(LevelAccessor levelAccessor, float partialTick) {
    if (levelAccessor instanceof ClientLevel clientLevel) {
      float[] color = clientLevel.effects().getSunriseColor(clientLevel.getTimeOfDay(partialTick), partialTick);
      if (color != null)
        return (int) (color[3] * 255.0F) << 24 | (int) (color[0] * 255.0F) << 16 | (int) (color[1] * 255.0F) << 8 | (int) (color[2] * 255.0F);
    }
    return 0;
  }
}).get(world, (float) partialTick))