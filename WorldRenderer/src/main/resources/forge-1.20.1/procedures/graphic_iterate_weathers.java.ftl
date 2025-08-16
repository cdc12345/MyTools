if (world instanceof ClientLevel) {
  BlockPos _renderCenter = Minecraft.getInstance().gameRenderer.getMainCamera().getBlockPosition();
  int _renderLength = Minecraft.useFancyGraphics() ? 10 : 5;
  int _renderRange = ${opt.toInt(input$range)};
  int positionx, positiony, positionz;
  _renderLength = _renderRange > _renderLength ? _renderRange : _renderLength;
  for (int _renderZ = -_renderRange; _renderZ <= _renderRange; ++_renderZ) {
    for (int _renderX = -_renderRange; _renderX <= _renderRange; ++_renderX) {
      positionx = _renderCenter.getX() + _renderX;
      positionz = _renderCenter.getZ() + _renderZ;
      positiony = world.getHeight(Heightmap.Types.MOTION_BLOCKING, positionx, positionz);
      if (positiony <= _renderCenter.getY() + _renderLength) {
        positiony = positiony < _renderCenter.getY() - _renderLength ? _renderCenter.getY() - _renderLength : positiony;
        ${statement$foreach}
      }
    }
  }
}