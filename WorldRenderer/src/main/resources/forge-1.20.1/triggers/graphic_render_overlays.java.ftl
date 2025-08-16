<#include "procedures.java.ftl">
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ${name}Procedure {
  private static Map<EntityType, Entity> data = new HashMap<>();
  private static GuiGraphics guiGraphics = null;
  private static float partialTick = 0.0F;
  private static int currentStage = 0;
  private static int targetStage = 0; // NONE: 0, ALWAYS: 1, GAME: 2, GUI: 3

  private static boolean target(int targetStage) {
    if (targetStage == currentStage) {
      ${name}Procedure.targetStage = targetStage;
      return true;
    } else if (targetStage == 1) {
      if (currentStage != 0) {
        ${name}Procedure.targetStage = currentStage;
        return true;
      }
    }
    return false;
  }

  private static void release() {
    targetStage = 0;
  }

  public static void renderEntity(LevelAccessor levelAccessor, EntityType type, double x, double y, double depth, float yaw, float pitch, float roll, float scale, boolean modelOnly) {
    if (currentStage == 0 || currentStage != targetStage)
      return;
    if (type == null)
      return;
    if (levelAccessor instanceof ClientLevel level && Minecraft.getInstance().gameRenderer.getMainCamera() != null) {
      Entity entity = null;
      if (data.containsKey(type)) {
        entity = data.get(type);
      } else {
        entity = type.create(level);
        data.put(type, entity);
      }
      renderEntity(entity, 0.0F, x, y, depth, yaw, pitch, roll, scale, modelOnly);
    }
  }

  public static void renderEntity(LevelAccessor levelAccessor, Entity entity, double x, double y, double depth, float yaw, float pitch, float roll, float scale, boolean modelOnly) {
    if (currentStage == 0 || currentStage != targetStage)
      return;
    Minecraft minecraft = Minecraft.getInstance();
    if (levelAccessor instanceof ClientLevel && minecraft.gameRenderer.getMainCamera() != null)
      renderEntity(entity, partialTick, x, y, depth, yaw, pitch, roll, scale, modelOnly);
  }

  private static void renderEntity(Entity entity, float partialTick, double x, double y, double depth, float yaw, float pitch, float roll, float scale, boolean modelOnly) {
		if (entity == null)
			return;
    float offset = entity.getBbHeight() / 2.0F;
    float yRotO = entity.yRotO;
		float yRot = entity.getYRot();
		float xRotO = entity.xRotO;
		float xRot = entity.getXRot();
		entity.yRotO = 180.0F;
		entity.setYRot(180.0F);
		entity.xRotO = 0.0F;
		entity.setXRot(0.0F);
	  PoseStack poseStack = guiGraphics.pose();
	  poseStack.pushPose();
	  poseStack.translate(x, y, -depth);
	  poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
	  poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
	  poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll + 180.0F));
    poseStack.mulPoseMatrix(new Matrix4f().scaling(scale, scale, -scale));
    poseStack.translate(0.0F, -offset, 0.0F);
    Lighting.setupForEntityInInventory();
		if (entity instanceof LivingEntity livingEntity) {
      float yBodyRotO = livingEntity.yBodyRotO;
		  float yBodyRot = livingEntity.yBodyRot;
		  float yHeadRotO = livingEntity.yHeadRotO;
		  float yHeadRot = livingEntity.yHeadRot;
		  livingEntity.yBodyRotO = 180.0F;
		  livingEntity.yBodyRot = 180.0F;
		  livingEntity.yHeadRotO = 180.0F;
		  livingEntity.yHeadRot = 180.0F;
      renderEntity(livingEntity, partialTick, poseStack, modelOnly);
		  livingEntity.yBodyRotO = yBodyRotO;
		  livingEntity.yBodyRot = yBodyRot;
		  livingEntity.yHeadRotO = yHeadRotO;
		  livingEntity.yHeadRot = yHeadRot;
    } else {
      renderEntity(entity, partialTick, poseStack, modelOnly);
    }
    Lighting.setupFor3DItems();
		poseStack.popPose();
    entity.yRotO = yRotO;
		entity.setYRot(yRot);
		entity.xRotO = xRotO;
		entity.setXRot(xRot);
	}

  private static void renderEntity(Entity entity, float partialTick, PoseStack poseStack, boolean modelOnly) {
    Minecraft minecraft = Minecraft.getInstance();
    if (modelOnly) {
      boolean customNameVisible = entity.isCustomNameVisible();
      entity.setCustomNameVisible(false);
      minecraft.getEntityRenderDispatcher().getRenderer(entity).render(entity, 0.0F, partialTick, poseStack, guiGraphics.bufferSource(), LightTexture.FULL_BRIGHT);
      guiGraphics.flush();
      entity.setCustomNameVisible(customNameVisible);
    } else {
      EntityRenderDispatcher renderer = minecraft.getEntityRenderDispatcher();
      renderer.setRenderShadow(false);
      renderer.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTick, poseStack, guiGraphics.bufferSource(), LightTexture.FULL_BRIGHT);
      guiGraphics.flush();
      renderer.setRenderShadow(true);
    }
  }

  public static void renderItem(LevelAccessor levelAccessor, ItemStack itemStack, double x, double y, double depth, float yaw, float pitch, float roll, float scale) {
    if (currentStage == 0 || currentStage != targetStage)
      return;
    if (itemStack.isEmpty())
      return;
    Minecraft minecraft = Minecraft.getInstance();
    if (levelAccessor instanceof ClientLevel level && minecraft.gameRenderer.getMainCamera() != null) {
      ItemRenderer renderer = minecraft.getItemRenderer();
      BakedModel bakedModel = renderer.getModel(itemStack, level, null, 0);
      PoseStack poseStack = guiGraphics.pose();
		  poseStack.pushPose();
	    poseStack.translate(x, y, -depth);
	    poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
	    poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
	    poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
      poseStack.mulPoseMatrix(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
			poseStack.scale(scale, scale, scale);
      if (!bakedModel.usesBlockLight())
        Lighting.setupForFlatItems();
      renderer.render(itemStack, ItemDisplayContext.GUI, false, poseStack, guiGraphics.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, bakedModel);
      guiGraphics.flush();
      Lighting.setupFor3DItems();
      poseStack.popPose();
    }
  }

  public static void renderRectangle(float x1, float y1, float x2, float y2, float depth, int color) {
    if (currentStage == 0 || currentStage != targetStage)
      return;
    float x3, y3, x4, y4;
    if (x1 <= x2) {
      x3 = x1;
      x4 = x2;
    } else {
      x3 = x2;
      x4 = x1;
    }
    if (y1 <= y2) {
      y3 = y1;
      y4 = y2;
    } else {
      y3 = y2;
      y4 = y1;
    }
    int red = color >> 16 & 255;
    int green = color >> 8 & 255;
    int blue = color & 255;
    int alpha = color >>> 24;
    Matrix4f matrix4f = guiGraphics.pose().last().pose();
    VertexConsumer vertexConsumer = guiGraphics.bufferSource().getBuffer(RenderType.gui());
    vertexConsumer.vertex(matrix4f, x3, y3, -depth).color(red, green, blue, alpha).endVertex();
    vertexConsumer.vertex(matrix4f, x3, y4, -depth).color(red, green, blue, alpha).endVertex();
    vertexConsumer.vertex(matrix4f, x4, y4, -depth).color(red, green, blue, alpha).endVertex();
    vertexConsumer.vertex(matrix4f, x4, y3, -depth).color(red, green, blue, alpha).endVertex();
  }

  public static void renderShape(VertexBuffer vertexBuffer, double x, double y, double depth, float yaw, float pitch, float roll, float xScale, float yScale, float zScale, int color) {
    if (currentStage == 0 || currentStage != targetStage)
      return;
    if (vertexBuffer == null)
      return;
    PoseStack poseStack = guiGraphics.pose();
    poseStack.pushPose();
    poseStack.translate(x, y, -depth);
    poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
    poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
    poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
    poseStack.scale(xScale, yScale, zScale);
    PoseStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushPose();
		modelViewStack.mulPoseMatrix(poseStack.last().pose());
    RenderSystem.setShaderColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, (color >>> 24) / 255.0F);
    vertexBuffer.bind();
    vertexBuffer.drawWithShader(modelViewStack.last().pose(), RenderSystem.getProjectionMatrix(), vertexBuffer.getFormat().hasUV(0) ? GameRenderer.getPositionTexColorShader() : GameRenderer.getPositionColorShader());
    VertexBuffer.unbind();
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    modelViewStack.popPose();
    poseStack.popPose();
  }

  public static void renderTexts(String texts, float x, float y, float depth, float angle, float scale, int color, int alignment) {
    if (currentStage == 0 || currentStage != targetStage)
      return;
    Font font = Minecraft.getInstance().font;
    float offsetX = 0.0F, offsetY = 0.0F;
    switch (alignment) {
      case 0:
        offsetX = (font.width(texts) - 1) * 0.5F;
        offsetY = (font.lineHeight - 1) * 0.5F;
        break;
      case 1:
        offsetY = (font.lineHeight - 1) * 0.5F;
        break;
      case 2:
        offsetX = (font.width(texts) - 1) * -0.5F;
        offsetY = (font.lineHeight - 1) * 0.5F;
        break;
      case 3:
        offsetX = (font.width(texts) - 1) * 0.5F;
        break;
      case 4:
        break;
      case 5:
        offsetX = (font.width(texts) - 1) * -0.5F;
        break;
      case 6:
        offsetX = (font.width(texts) - 1) * 0.5F;
        offsetY = (font.lineHeight - 1) * -0.5F;
        break;
      case 7:
        offsetY = (font.lineHeight - 1) * -0.5F;
        break;
      case 8:
        offsetX = (font.width(texts) - 1) * -0.5F;
        offsetY = (font.lineHeight - 1) * -0.5F;
        break;
    }
    PoseStack poseStack = guiGraphics.pose();
    poseStack.pushPose();
    poseStack.translate(x + offsetX * scale, y + offsetY * scale, -depth);
		poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(angle));
		poseStack.scale(scale, scale, 1.0F);
    poseStack.translate((font.width(texts) - 1) * -0.5F, (font.lineHeight - 1) * -0.5F, 0.0F);
    Matrix4f matrix4f = poseStack.last().pose();
    font.drawInBatch(texts, 0.0F, 0.0F, color, false, matrix4f, guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
    poseStack.popPose();
  }

  public static void renderTexture(float x, float y, float depth, float angle, float scale, int color, int alignment) {    
    if (currentStage == 0 || currentStage != targetStage)
      return;
    float offsetX = 0.0F, offsetY = 0.0F;
    switch (alignment) {
      case 0:
        offsetX = 0.5F;
        offsetY = 0.5F;
        break;
      case 1:
        offsetY = 0.5F;
        break;
      case 2:
        offsetX = -0.5F;
        offsetY = 0.5F;
        break;
      case 3:
        offsetX = 0.5F;
        break;
      case 4:
        break;
      case 5:
        offsetX = -0.5F;
        break;
      case 6:
        offsetX = 0.5F;
        offsetY = -0.5F;
        break;
      case 7:
        offsetY = -0.5F;
        break;
      case 8:
        offsetX = -0.5F;
        offsetY = -0.5F;
        break;
    }
    RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
    int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
    int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
    int red = color >> 16 & 255;
    int green = color >> 8 & 255;
    int blue = color & 255;
    int alpha = color >>> 24;
    PoseStack poseStack = guiGraphics.pose();
    poseStack.pushPose();
    poseStack.translate(x, y, -depth);
		poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(angle));
		poseStack.scale(width * scale, height * scale, 1.0F);
    poseStack.translate(offsetX, offsetY, 0.0F);
    Matrix4f matrix4f = poseStack.last().pose();
    RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
    BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
    bufferBuilder.vertex(matrix4f, -0.5F, -0.5F, 0.0F).uv(0.0F, 0.0F).color(red, green, blue, alpha).endVertex();
    bufferBuilder.vertex(matrix4f, -0.5F, 0.5F, 0.0F).uv(0.0F, 1.0F).color(red, green, blue, alpha).endVertex();
    bufferBuilder.vertex(matrix4f, 0.5F, 0.5F, 0.0F).uv(1.0F, 1.0F).color(red, green, blue, alpha).endVertex();
    bufferBuilder.vertex(matrix4f, 0.5F, -0.5F, 0.0F).uv(1.0F, 0.0F).color(red, green, blue, alpha).endVertex();
    BufferUploader.drawWithShader(bufferBuilder.end());
    poseStack.popPose();
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void renderGUI(RenderGuiEvent.Pre event) {
    currentStage = 2;
    guiGraphics = event.getGuiGraphics();
    partialTick = event.getPartialTick();
    renderOverlays(event);
    currentStage = 0;
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void renderScreen(ScreenEvent.Render.Post event) {
    currentStage = 3;
    guiGraphics = event.getGuiGraphics();
    partialTick = event.getPartialTick();
    renderOverlays(event);
    currentStage = 0;
  }

  private static void renderOverlays(Event event) {
    Minecraft minecraft = Minecraft.getInstance();
    double scale = minecraft.getWindow().getGuiScale();
    if (scale > 0.0D) {
      RenderSystem.depthMask(true);
		  RenderSystem.enableDepthTest();
      RenderSystem.disableCull();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      <#assign dependenciesCode><#compress>
        <@procedureDependenciesCode dependencies, {
          "mouseX": "minecraft.mouseHandler.xpos() / scale",
          "mouseY": "minecraft.mouseHandler.ypos() / scale",
          "partialTick": "partialTick",
          "event": "event"
        }/>
      </#compress></#assign>
      execute(event<#if dependenciesCode?has_content>, </#if>${dependenciesCode});
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.enableCull();
		  RenderSystem.enableDepthTest();
		  RenderSystem.depthMask(true);
    }
  }