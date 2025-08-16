<#include "mcelements.ftl">
/*@int*/((new Object() {
  public int get(LevelAccessor LevelAccessor, BlockPos blockPos) {
    return 255 << 24 | LevelAccessor.getBlockState(blockPos).getMapColor(LevelAccessor, blockPos).col;
  }
}).get(world, ${toBlockPos(input$x, input$y, input$z)}))