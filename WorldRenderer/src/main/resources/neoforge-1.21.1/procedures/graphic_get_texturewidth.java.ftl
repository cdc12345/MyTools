/*@int*/((new Object() {
  public int get() {
    RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
    return GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D,  0, GL11.GL_TEXTURE_WIDTH);
  }
}).get())