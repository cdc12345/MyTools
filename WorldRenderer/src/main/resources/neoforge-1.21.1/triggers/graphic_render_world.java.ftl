<#include "procedures.java.ftl">
@EventBusSubscriber(value = Dist.CLIENT)
public class ${name}Procedure {
  private static RenderLevelStageEvent provider = null;
  private static Map<EntityType, Entity> data = new HashMap<>();

  public static void renderBackground(String texts, double x, double y, double z, float yaw, float pitch, float roll, float scale, int color) {
    Minecraft minecraft = Minecraft.getInstance();
    Font font = minecraft.font;
		MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
		Vec3 pos = provider.getCamera().getPosition();
		PoseStack poseStack = provider.getPoseStack();
		poseStack.pushPose();
		poseStack.translate(x - pos.x(), y - pos.y(), z - pos.z());
		poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
		poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
		poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
		poseStack.scale(scale, -scale, 1.0F);
		poseStack.translate((font.width(texts) - 1) * -0.5F, (font.lineHeight - 1) * -0.5F, 0.0F);
		Matrix4f matrix4f = poseStack.last().pose();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		font.drawInBatch(texts, 0.0F, 0.0F, 0, false, matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH, color, LightTexture.FULL_BRIGHT);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
  }

  public static void renderBlock(BlockState blockState, double x, double y, double z, float yaw, float pitch, float roll, float scale, boolean glowing) {
    BlockPos blockPos = BlockPos.containing(x, y, z);
		Vec3 pos = provider.getCamera().getPosition();
    int packedLight = glowing ? LightTexture.FULL_BRIGHT : LevelRenderer.getLightColor(Minecraft.getInstance().level, blockPos);
		PoseStack poseStack = provider.getPoseStack();
		poseStack.pushPose();
		poseStack.translate(x - pos.x(), y - pos.y(), z - pos.z());
		poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
		poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
		poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
		poseStack.scale(scale, scale, scale);
    poseStack.translate(-0.5F, -0.5F, -0.5F);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    renderBlockModel(blockState, blockPos, poseStack, packedLight);
    renderBlockEntity(blockState, blockPos, poseStack, packedLight);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
	}

  private static void renderBlockEntity(BlockState blockState, BlockPos blockPos, PoseStack poseStack, int packedLight) {
    if (blockState.getBlock() instanceof EntityBlock entityBlock) {
      Minecraft minecraft = Minecraft.getInstance();
      ClientLevel level = minecraft.level;
      BlockEntity blockEntity = entityBlock.newBlockEntity(blockPos, blockState);
      if (blockEntity != null) {
        BlockEntityRenderer blockEntityRenderer = minecraft.getBlockEntityRenderDispatcher().getRenderer(blockEntity);
        if (blockEntityRenderer != null) {
          MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
          blockEntity.setLevel(level);
          blockEntityRenderer.render(blockEntity, 0.0F, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        }
      }
    }
  }

  private static void renderBlockModel(BlockState blockState, BlockPos blockPos, PoseStack poseStack, int packedLight) {
    if (blockState.getRenderShape() == RenderShape.MODEL) {
      Minecraft minecraft = Minecraft.getInstance();
      ClientLevel level = minecraft.level;
      MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
      BlockRenderDispatcher dispatcher = minecraft.getBlockRenderer();
      ModelBlockRenderer renderer = dispatcher.getModelRenderer();
      BakedModel bakedModel = dispatcher.getBlockModel(blockState);
      ModelData modelData = bakedModel.getModelData(level, blockPos, blockState, ModelData.builder().build());
      PoseStack.Pose pose = poseStack.last();
      int color = minecraft.getBlockColors().getColor(blockState, level, blockPos);
      float red = (color >> 16 & 255) / 255.0F;
      float green = (color >> 8 & 255) / 255.0F;
      float blue = (color & 255) / 255.0F;
      for (RenderType renderType : bakedModel.getRenderTypes(blockState, RandomSource.create(42L), modelData)) {
        renderer.renderModel(pose, bufferSource.getBuffer(Sheets.translucentCullBlockSheet()), blockState, bakedModel, red, green, blue, packedLight, OverlayTexture.NO_OVERLAY, modelData, renderType);
      }
    }
  }

  public static void renderEntity(EntityType type, double x, double y, double z, float yaw, float pitch, float roll, float scale, boolean glowing) {
    if (type == null)
      return;
    Entity entity;
    ClientLevel level = Minecraft.getInstance().level;
    if (data.containsKey(type)) {
      entity = data.get(type);
      if (entity.level() != level) {
        entity = type.create(level);
        data.put(type, entity);
      }
    } else {
      entity = type.create(level);
      data.put(type, entity);
    }
    renderEntity(entity, 0.0F, x, y, z, yaw, pitch, roll, scale, glowing ? LightTexture.FULL_BRIGHT : LevelRenderer.getLightColor(level, BlockPos.containing(x, y, z)));
  }

  public static void renderEntity(Entity entity, double x, double y, double z, float yaw, float pitch, float roll, float scale, boolean glowing) {
    float partialTick = provider.getPartialTick().getGameTimeDeltaPartialTick(false);
    int packedLight = glowing ? LightTexture.FULL_BRIGHT : Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(entity, partialTick);
    renderEntity(entity, partialTick, x, y, z, yaw, pitch, roll, scale, packedLight);
  }

  private static void renderEntity(Entity entity, float partialTick, double x, double y, double z, float yaw, float pitch, float roll, float scale, int packedLight) {
		if (entity == null)
			return;
		Minecraft minecraft = Minecraft.getInstance();
    MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
		EntityRenderer renderer = minecraft.getEntityRenderDispatcher().getRenderer(entity);
		Vec3 pos = provider.getCamera().getPosition();
    float offset = (entity.getBbHeight() / 2.0F) * scale;
		PoseStack poseStack = provider.getPoseStack();
		poseStack.pushPose();
		poseStack.translate(x - pos.x(), y + offset - pos.y(), z - pos.z());
		poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
		poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
		poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
    poseStack.translate(0.0F, -offset, 0.0F);
    poseStack.scale(scale, scale, scale);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		renderer.render(entity, entity.getViewYRot(partialTick), partialTick, poseStack, bufferSource, packedLight);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
	}

  public static void renderItem(ItemStack itemStack, double x, double y, double z, float yaw, float pitch, float roll, float scale, boolean flipping, boolean glowing) {
    Minecraft minecraft = Minecraft.getInstance();
		MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
		ItemRenderer renderer = minecraft.getItemRenderer();
		Vec3 pos = provider.getCamera().getPosition();
    int packedLight = glowing ? LightTexture.FULL_BRIGHT : LevelRenderer.getLightColor(minecraft.level, BlockPos.containing(x, y, z));
		PoseStack poseStack = provider.getPoseStack();
		poseStack.pushPose();
		poseStack.translate(x - pos.x(), y - pos.y(), z - pos.z());
		poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
		poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
		poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
		poseStack.scale(scale, scale, scale);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		renderer.renderStatic(null, itemStack, ItemDisplayContext.FIXED, flipping, poseStack, bufferSource, minecraft.level, packedLight, OverlayTexture.NO_OVERLAY, 0);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
	}

  public static void renderLine(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
		MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		Vec3 pos = provider.getCamera().getPosition();
		Vector3f normal = new Vec3(x2 - x1, y2 - y1, z2 - z1).normalize().toVector3f();
		Matrix4f matrix4f = provider.getPoseStack().last().pose();
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
		vertexConsumer.addVertex(matrix4f, (float) (x1 - pos.x()), (float) (y1 - pos.y()), (float) (z1 - pos.z())).setColor(color).setNormal(normal.x(), normal.y(), normal.z());
    vertexConsumer.addVertex(matrix4f, (float) (x2 - pos.x()), (float) (y2 - pos.y()), (float) (z2 - pos.z())).setColor(color).setNormal(normal.x(), normal.y(), normal.z());
	}

  public static void renderTexts(String texts, double x, double y, double z, float yaw, float pitch, float roll, float scale, int color, boolean glowing) {
    Minecraft minecraft = Minecraft.getInstance();
    Font font = minecraft.font;
		MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
		Vec3 pos = provider.getCamera().getPosition();
    int packedLight = glowing ? LightTexture.FULL_BRIGHT : LevelRenderer.getLightColor(minecraft.level, BlockPos.containing(x, y, z));
		PoseStack poseStack = provider.getPoseStack();
		poseStack.pushPose();
		poseStack.translate(x - pos.x(), y - pos.y(), z - pos.z());
		poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
		poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
		poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
		poseStack.scale(scale, -scale, 1.0F);
		poseStack.translate((font.width(texts) - 1) * -0.5F, (font.lineHeight - 1) * -0.5F, 0.0F);
		Matrix4f matrix4f = poseStack.last().pose();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    font.drawInBatch(texts, 0.0F, 0.0F, color, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
  }

  @SubscribeEvent
  public static void renderModels(RenderLevelStageEvent event) {
    provider = event;
    if (provider.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
      ClientLevel level = Minecraft.getInstance().level;
      Entity entity = provider.getCamera().getEntity();
      Vec3 pos = entity.getPosition(provider.getPartialTick().getGameTimeDeltaPartialTick(false));
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      <#assign dependenciesCode><#compress>
        <@procedureDependenciesCode dependencies, {
          "dimension": "level.dimension()",
          "entity": "entity",
          "partialTick": "provider.getPartialTick().getGameTimeDeltaPartialTick(false)",
          "ticks": "provider.getRenderTick()",
          "x": "pos.x()",
          "y": "pos.y()",
          "z": "pos.z()",
          "world": "level",
          "event": "provider"
        }/>
      </#compress></#assign>
      execute(provider<#if dependenciesCode?has_content>, </#if>${dependenciesCode});
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.enableCull();
		  RenderSystem.enableDepthTest();
			RenderSystem.depthMask(true);
    }
  }