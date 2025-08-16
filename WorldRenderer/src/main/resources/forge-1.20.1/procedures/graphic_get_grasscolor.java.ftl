/*@int*/((new Object() {
  public int get(LevelAccessor levelAccessor, double x, double y, double z) {
    return 255 << 24 | levelAccessor.getBiome(BlockPos.containing(x, y, z)).value().getGrassColor(x, z);
  }
}).get(world, ${input$x}, ${input$y}, ${input$z}))