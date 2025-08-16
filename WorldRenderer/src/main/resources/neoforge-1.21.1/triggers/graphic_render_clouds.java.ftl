<#include "procedures.java.ftl">
@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ${name}Procedure {
  private static int ticks = 0;
  private static float partialTick = 0.0F;
  private static PoseStack poseStack = null;
	private static Matrix4f modelViewMatrix = null;
  private static Matrix4f projectionMatrix = null;
  private static VertexBuffer cloudBuffer = null;
  private static CloudStatus cloudStatus = null;
  private static double x = 0.0D;
  private static double y = 0.0D;
  private static double z = 0.0D;
	private static float width = 12.0F;
	private static float height = 4.0F;
	private static final Predicate<Object[]> PREDICATE = params -> {
    ticks = (Integer) params[1];
    partialTick = (Float) params[2];
    poseStack = (PoseStack) params[3];
		modelViewMatrix = (Matrix4f) params[7];
    projectionMatrix = (Matrix4f) params[8];
    Minecraft minecraft = Minecraft.getInstance();
    Entity entity = minecraft.gameRenderer.getMainCamera().getEntity();
    if (entity != null) {
			ClientLevel level = minecraft.level;
      Vec3 pos = entity.getPosition(partialTick);
      <#assign dependenciesCode><#compress>
        <@procedureDependenciesCode dependencies, {
          "dimension": "level.dimension()",
          "entity": "entity",
					"partialTick": "partialTick",
          "ticks": "ticks",
          "x": "pos.x()",
          "y": "pos.y()",
          "z": "pos.z()",
          "world": "level",
          "event": "null"
        }/>
      </#compress></#assign>
			<#if generator.procedureNamesToObjects(name).get(0).getReturnValueType(generator.getWorkspace()) == "logic">
      	return execute(null<#if dependenciesCode?has_content>, </#if>${dependenciesCode});
			<#else>
				execute(null<#if dependenciesCode?has_content>, </#if>${dependenciesCode});
				return true;
			</#if>
    }
    return false;
	};

	private static void buildClouds(CloudStatus cloudStatus, double x, double y, double z) {
    if (cloudBuffer == null || ${name}Procedure.cloudStatus != cloudStatus || ${name}Procedure.x != x || ${name}Procedure.y != y || ${name}Procedure.z != z) {
      ${name}Procedure.cloudStatus = cloudStatus;
      ${name}Procedure.x = x;
      ${name}Procedure.y = y;
      ${name}Procedure.z = z;
		  Minecraft minecraft = Minecraft.getInstance();
			RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
		  float du = 1.0F / GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		  float dv = 1.0F / GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
		  float dx = Mth.floor(x) * du;
		  float dz = Mth.floor(z) * dv;
		  float cloudY = (float) Math.floor(y / height) * height;
      BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
		  if (cloudStatus == CloudStatus.FANCY) {
			  for (int i = -3; i <= 4; ++i) {
				  for (int j = -3; j <= 4; ++j) {
					  float cloudX = i * 8.0F;
					  float cloudZ = j * 8.0F;
					  if (cloudY > -height - 1) {
						  bufferBuilder.addVertex(cloudX, cloudY, cloudZ + 8.0F).setUv(cloudX * du + dx, (cloudZ + 8.0F) * dv + dz).setColor(0.7F, 0.7F, 0.7F, 1.0F).setNormal(0.0F, -1.0F, 0.0F);
						  bufferBuilder.addVertex(cloudX + 8.0F, cloudY, cloudZ + 8.0F).setUv((cloudX + 8.0F) * du + dx, (cloudZ + 8.0F) * dv + dz).setColor(0.7F, 0.7F, 0.7F, 1.0F).setNormal(0.0F, -1.0F, 0.0F);
						  bufferBuilder.addVertex(cloudX + 8.0F, cloudY, cloudZ).setUv((cloudX + 8.0F) * du + dx, cloudZ * dv + dz).setColor(0.7F, 0.7F, 0.7F, 1.0F).setNormal(0.0F, -1.0F, 0.0F);
						  bufferBuilder.addVertex(cloudX, cloudY, cloudZ).setUv(cloudX * du + dx, cloudZ * dv + dz).setColor(0.7F, 0.7F, 0.7F, 1.0F).setNormal(0.0F, -1.0F, 0.0F);
					  }
					  if (cloudY <= height + 1) {
						  bufferBuilder.addVertex(cloudX, cloudY + height - 9.765625E-4F, cloudZ + 8.0F).setUv(cloudX * du + dx, (cloudZ + 8.0F) * dv + dz).setColor(1.0F, 1.0F, 1.0F, 1.0F).setNormal(0.0F, 1.0F, 0.0F);
						  bufferBuilder.addVertex(cloudX + 8.0F, cloudY + height - 9.765625E-4F, cloudZ + 8.0F).setUv((cloudX + 8.0F) * du + dx, (cloudZ + 8.0F) * dv + dz).setColor(1.0F, 1.0F, 1.0F, 1.0F).setNormal(0.0F, 1.0F, 0.0F);
						  bufferBuilder.addVertex(cloudX + 8.0F, cloudY + height - 9.765625E-4F, cloudZ).setUv((cloudX + 8.0F) * du + dx, cloudZ * dv + dz).setColor(1.0F, 1.0F, 1.0F, 1.0F).setNormal(0.0F, 1.0F, 0.0F);
						  bufferBuilder.addVertex(cloudX, cloudY + height - 9.765625E-4F, cloudZ).setUv(cloudX * du + dx, cloudZ * dv + dz).setColor(1.0F, 1.0F, 1.0F, 1.0F).setNormal(0.0F, 1.0F, 0.0F);
					  }
					  if (i > -1) {
						  for (int k = 0; k < 8; ++k) {
							  bufferBuilder.addVertex(cloudX + k, cloudY, cloudZ + 8.0F).setUv((cloudX + k + 0.5F) * du + dx, (cloudZ + 8.0F) * dv + dz).setColor(0.9F, 0.9F, 0.9F, 1.0F).setNormal(-1.0F, 0.0F, 0.0F);
							  bufferBuilder.addVertex(cloudX + k, cloudY + height, cloudZ + 8.0F).setUv((cloudX + k + 0.5F) * du + dx, (cloudZ + 8.0F) * dv + dz).setColor(0.9F, 0.9F, 0.9F, 1.0F).setNormal(-1.0F, 0.0F, 0.0F);
							  bufferBuilder.addVertex(cloudX + k, cloudY + height, cloudZ).setUv((cloudX + k + 0.5F) * du + dx, cloudZ * dv + dz).setColor(0.9F, 0.9F, 0.9F, 1.0F).setNormal(-1.0F, 0.0F, 0.0F);
							  bufferBuilder.addVertex(cloudX + k, cloudY, cloudZ).setUv((cloudX + k + 0.5F) * du + dx, cloudZ * dv + dz).setColor(0.9F, 0.9F, 0.9F, 1.0F).setNormal(-1.0F, 0.0F, 0.0F);
					  	}
					  }
					  if (i <= 1) {
						  for (int k = 0; k < 8; ++k) {
						  	bufferBuilder.addVertex(cloudX + k + 1.0F - 9.765625E-4F, cloudY, cloudZ + 8.0F).setUv((cloudX + k + 0.5F) * du + dx, (cloudZ + 8.0F) * dv + dz).setColor(0.9F, 0.9F, 0.9F, 1.0F).setNormal(1.0F, 0.0F, 0.0F);
							  bufferBuilder.addVertex(cloudX + k + 1.0F - 9.765625E-4F, cloudY + height, cloudZ + 8.0F).setUv((cloudX + k + 0.5F) * du + dx, (cloudZ + 8.0F) * dv + dz).setColor(0.9F, 0.9F, 0.9F, 1.0F).setNormal(1.0F, 0.0F, 0.0F);
							  bufferBuilder.addVertex(cloudX + k + 1.0F - 9.765625E-4F, cloudY + height, cloudZ).setUv((cloudX + k + 0.5F) * du + dx, cloudZ * dv + dz).setColor(0.9F, 0.9F, 0.9F, 1.0F).setNormal(1.0F, 0.0F, 0.0F);
							  bufferBuilder.addVertex(cloudX + k + 1.0F - 9.765625E-4F, cloudY, cloudZ).setUv((cloudX + k + 0.5F) * du + dx, cloudZ * dv + dz).setColor(0.9F, 0.9F, 0.9F, 1.0F).setNormal(1.0F, 0.0F, 0.0F);
						  }
					  }
					  if (j > -1) {
						  for (int k = 0; k < 8; ++k) {
							  bufferBuilder.addVertex(cloudX, cloudY + height, cloudZ + k).setUv(cloudX * du + dx, (cloudZ + k + 0.5F) * dv + dz).setColor(0.8F, 0.8F, 0.8F, 1.0F).setNormal(0.0F, 0.0F, -1.0F);
							  bufferBuilder.addVertex(cloudX + 8.0F, cloudY + height, cloudZ + k).setUv((cloudX + 8.0F) * du + dx, (cloudZ + k + 0.5F) * dv + dz).setColor(0.8F, 0.8F, 0.8F, 1.0F).setNormal(0.0F, 0.0F, -1.0F);
							  bufferBuilder.addVertex(cloudX + 8.0F, cloudY, cloudZ + k).setUv((cloudX + 8.0F) * du + dx, (cloudZ + k + 0.5F) * dv + dz).setColor(0.8F, 0.8F, 0.8F, 1.0F).setNormal(0.0F, 0.0F, -1.0F);
							  bufferBuilder.addVertex(cloudX, cloudY, cloudZ + k).setUv(cloudX * du + dx, (cloudZ + k + 0.5F) * dv + dz).setColor(0.8F, 0.8F, 0.8F, 1.0F).setNormal(0.0F, 0.0F, -1.0F);
						  }
					  }
					  if (j <= 1) {
						  for (int k = 0; k < 8; ++k) {
							  bufferBuilder.addVertex(cloudX, cloudY + height, cloudZ + k + 1.0F - 9.765625E-4F).setUv(cloudX * du + dx, (cloudZ + k + 0.5F) * dv + dz).setColor(0.8F, 0.8F, 0.8F, 1.0F).setNormal(0.0F, 0.0F, 1.0F);
							  bufferBuilder.addVertex(cloudX + 8.0F, cloudY + height, cloudZ + k + 1.0F - 9.765625E-4F).setUv((cloudX + 8.0F) * du + dx, (cloudZ + k + 0.5F) * dv + dz).setColor(0.8F, 0.8F, 0.8F, 1.0F).setNormal(0.0F, 0.0F, 1.0F);
							  bufferBuilder.addVertex(cloudX + 8.0F, cloudY, cloudZ + k + 1.0F - 9.765625E-4F).setUv((cloudX + 8.0F) * du + dx, (cloudZ + k + 0.5F) * dv + dz).setColor(0.8F, 0.8F, 0.8F, 1.0F).setNormal(0.0F, 0.0F, 1.0F);
							  bufferBuilder.addVertex(cloudX, cloudY, cloudZ + k + 1.0F - 9.765625E-4F).setUv(cloudX * du + dx, (cloudZ + k + 0.5F) * dv + dz).setColor(0.8F, 0.8F, 0.8F, 1.0F).setNormal(0.0F, 0.0F, 1.0F);
						  }
					  }
				  }
			  }
		  } else if (cloudStatus == CloudStatus.FAST) {
			  for (int i = -32; i < 32; i += 32) {
				  for (int j = -32; j < 32; j += 32) {
					  bufferBuilder.addVertex(i, cloudY, j + 32).setUv(i * du + dx, (j + 32) * dv + dz).setColor(1.0F, 1.0F, 1.0F, 1.0F).setNormal(0.0F, -1.0F, 0.0F);
					  bufferBuilder.addVertex(i + 32, cloudY, j + 32).setUv((i + 32) * du + dx, (j + 32) * dv + dz).setColor(1.0F, 1.0F, 1.0F, 1.0F).setNormal(0.0F, -1.0F, 0.0F);
					  bufferBuilder.addVertex(i + 32, cloudY, j).setUv((i + 32) * du + dx, j * dv + dz).setColor(1.0F, 1.0F, 1.0F, 1.0F).setNormal(0.0F, -1.0F, 0.0F);
					  bufferBuilder.addVertex(i, cloudY, j).setUv(i * du + dx, j * dv + dz).setColor(1.0F, 1.0F, 1.0F, 1.0F).setNormal(0.0F, -1.0F, 0.0F);
				  }
			  }
		  }
      if (cloudBuffer != null)
        cloudBuffer.close();
		  cloudBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
		  cloudBuffer.bind();
		  cloudBuffer.upload(bufferBuilder.buildOrThrow());
    } else {
      cloudBuffer.bind();
    }
	}

  public static void renderClouds(CloudStatus cloudStatus, double altitude, double vx, double vz, int color) {
		if (cloudStatus == CloudStatus.OFF)
			return;
    RenderSystem.bindTexture(RenderSystem.getShaderTexture(0));
		int tw = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH) << 3;
		int th = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT) << 3;
		if (tw > 0 && th > 0) {
      Minecraft minecraft = Minecraft.getInstance();
		  Vec3 pos = minecraft.gameRenderer.getMainCamera().getPosition();
		  double factor = (ticks + partialTick) * 0.03D;
		  double x = (pos.x() + factor * -vx) / width;
		  double y = altitude + 0.33D - pos.y();
		  double z = (pos.z() + factor * -vz) / width + 0.33D;
		  x -= Mth.floor(x / tw) * tw;
		  z -= Mth.floor(z / th) * th;
		  float dx = (float) (x - Mth.floor(x));
		  float dy = (float) (y / height - Mth.floor(y / height)) * height;
		  float dz = (float) (z - Mth.floor(z));
      buildClouds(cloudStatus, x, y, z);
		  poseStack.pushPose();
			poseStack.mulPose(modelViewMatrix);
		  poseStack.scale(width, 1.0F, width);
		  poseStack.translate(-dx, dy, -dz);
		  Matrix4f matrix4f = poseStack.last().pose();
		  ShaderInstance shaderInstance = GameRenderer.getRendertypeCloudsShader();
      RenderSystem.setShaderColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, (color >>> 24) / 255.0F);
		  if (cloudStatus == CloudStatus.FANCY) {
			  RenderSystem.colorMask(false, false, false, false);
			  cloudBuffer.drawWithShader(matrix4f, projectionMatrix, shaderInstance);
			  RenderSystem.colorMask(true, true, true, true);
			  cloudBuffer.drawWithShader(matrix4f, projectionMatrix, shaderInstance);
		  } else if (cloudStatus == CloudStatus.FAST) {
			  RenderSystem.colorMask(true, true, true, true);
			  cloudBuffer.drawWithShader(matrix4f, projectionMatrix, shaderInstance);
		  }
		  VertexBuffer.unbind();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		  poseStack.popPose();
    }
	}

  @SubscribeEvent
  public static void cloudsSetup(FMLClientSetupEvent event) {
    try {
      Field field = DimensionSpecialEffectsManager.class.getDeclaredField("EFFECTS");
      field.setAccessible(true);
      for (DimensionSpecialEffects dimensionSpecialEffects : ((com.google.common.collect.ImmutableMap<ResourceLocation, DimensionSpecialEffects>) field.get(null)).values()) {
        Class<?> effects = dimensionSpecialEffects.getClass();
			  ((Set<Predicate<Object[]>>) effects.getField("CUSTOM_CLOUDS").get(null)).add(PREDICATE);
      }
    } catch (Exception e) {}
  }