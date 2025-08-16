if (world instanceof ClientLevel) {
  for(Entity entityiterator : ((ClientLevel) world).entitiesForRendering()) {
    ${statement$foreach}
  }
}