<#include "mcelements.ftl">
((new Object() {
  public boolean is(LevelAccessor levelAccessor, BlockPos blockPos, Biome.Precipitation precipitation) {
    return world.getBiome(blockPos).value().getPrecipitationAt(blockPos) == precipitation;
  }
}).is(world, ${toBlockPos(input$x,input$y,input$z)}, ${field$type}))