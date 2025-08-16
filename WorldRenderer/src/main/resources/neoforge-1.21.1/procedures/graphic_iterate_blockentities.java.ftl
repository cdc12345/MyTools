if (world instanceof ClientLevel _blockEntityContext) {
  int _scanRange = Minecraft.getInstance().options.getEffectiveRenderDistance();
  BlockPos _scanCenter = Minecraft.getInstance().player.blockPosition();
  LevelChunk _levelChunk;
  BlockState blockstateiterator;
  int positionx, positiony, positionz;
  for (int _chunkZ = -_scanRange; _chunkZ <= _scanRange; ++_chunkZ) {
    for (int _chunkX = -_scanRange; _chunkX <= _scanRange; ++_chunkX) {
      _levelChunk = _blockEntityContext.getChunk(SectionPos.blockToSectionCoord(_scanCenter.getX() + (_chunkX << 4)), SectionPos.blockToSectionCoord(_scanCenter.getZ() + (_chunkZ << 4)));
      if (_levelChunk != null) {
        for (Map.Entry<BlockPos, BlockEntity> _blockEntityEntry : _levelChunk.getBlockEntities().entrySet()) {
          blockstateiterator = _blockEntityEntry.getValue().getBlockState();
          positionx = _blockEntityEntry.getKey().getX();
          positiony = _blockEntityEntry.getKey().getY();
          positionz = _blockEntityEntry.getKey().getZ();
          ${statement$foreach}
        }
      }
    }
  }
}