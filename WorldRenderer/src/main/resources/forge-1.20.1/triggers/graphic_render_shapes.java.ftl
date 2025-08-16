<#include "procedures.java.ftl">
<#assign return = generator.procedureNamesToObjects(name).get(0).getReturnValueType(generator.getWorkspace())>
<#if return != "shape">
@Mod.EventBusSubscriber(value = Dist.CLIENT)
</#if>
public class ${name}Procedure {
  private static BufferBuilder bufferBuilder = null;
  private static VertexBuffer vertexBuffer = null;
  private static VertexFormat.Mode mode = null;
  private static VertexFormat format = null;
  <#if return != "shape">
  private static PoseStack poseStack = null;
  private static Matrix4f projectionMatrix = null;
  private static boolean worldCoordinate = true;
  private static Vec3 offset = Vec3.ZERO;
  private static int currentStage = 0;
  private static int targetStage = 0; // NONE: 0, SKY: 1, WORLD: 2
  </#if>

  private static void add(double x, double y, double z, int color) {
    add(x, y, z, 0.0F, 0.0F, color);
  }

  private static void add(double x, double y, double z, float u, float v, int color) {
    if (bufferBuilder == null || !bufferBuilder.building())
      return;
    if (format == DefaultVertexFormat.POSITION_COLOR) {
      bufferBuilder.vertex(x, y, z).color(color).endVertex();
    } else if (format == DefaultVertexFormat.POSITION_TEX_COLOR) {
      bufferBuilder.vertex(x, y, z).uv(u, v).color(color).endVertex();
    }
  }

  private static boolean begin(VertexFormat.Mode mode, VertexFormat format, boolean update) {
    if (${name}Procedure.bufferBuilder == null || !${name}Procedure.bufferBuilder.building()) {
      if (update)
        clear();
      if (${name}Procedure.vertexBuffer == null) {
        if (format == DefaultVertexFormat.POSITION_COLOR) {
          ${name}Procedure.mode = mode;
          ${name}Procedure.format = format;
          ${name}Procedure.bufferBuilder = Tesselator.getInstance().getBuilder();
          ${name}Procedure.bufferBuilder.begin(mode, DefaultVertexFormat.POSITION_COLOR);
          return true;
        } else if (format == DefaultVertexFormat.POSITION_TEX_COLOR) {
          ${name}Procedure.mode = mode;
          ${name}Procedure.format = format;
          ${name}Procedure.bufferBuilder = Tesselator.getInstance().getBuilder();
          ${name}Procedure.bufferBuilder.begin(mode, DefaultVertexFormat.POSITION_TEX_COLOR);
          return true;
        }
      }
    }
    return false;
  }

  private static void clear() {
    if (vertexBuffer != null) {
      vertexBuffer.close();
      vertexBuffer = null;
    }
  }

  private static void end() {
    if (bufferBuilder == null || !bufferBuilder.building())
      return;
    if (vertexBuffer != null)
      vertexBuffer.close();
    vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
		vertexBuffer.bind();
		vertexBuffer.upload(bufferBuilder.end());
    VertexBuffer.unbind();
  }

  <#if return != "shape">
  private static void offset(double x, double y, double z) {
    offset = new Vec3(x, y, z);
  }

  private static void release() {
    targetStage = 0;
  }
  </#if>

  private static VertexBuffer shape() {
    return vertexBuffer;
  }

  <#if return != "shape">
  private static void system(boolean worldCoordinate) {
    ${name}Procedure.worldCoordinate = worldCoordinate;
  }

  private static boolean target(int targetStage) {
    if (targetStage == currentStage) {
      ${name}Procedure.targetStage = targetStage;
      return true;
    }
    return false;
  }

  private static void renderShape(VertexBuffer vertexBuffer, double x, double y, double z, float yaw, float pitch, float roll, float xScale, float yScale, float zScale, int color) {
    if (currentStage == 0 || currentStage != targetStage)
      return;
    if (poseStack == null || projectionMatrix == null)
      return;
    if (vertexBuffer == null)
      return;
    float i, j, k;
    if (worldCoordinate) {
      Vec3 pos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
      i = (float) (x - pos.x());
      j = (float) (y - pos.y());
      k = (float) (z - pos.z());
    } else {
      i = (float) x;
      j = (float) y;
      k = (float) z;
    }
    poseStack.pushPose();
    poseStack.translate(i, j, k);
    poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
    poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
    poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
    poseStack.scale(xScale, yScale, zScale);
    poseStack.translate(offset.x(), offset.y(), offset.z());
    RenderSystem.setShaderColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, (color >>> 24) / 255.0F);
    vertexBuffer.bind();
    vertexBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, vertexBuffer.getFormat().hasUV(0) ? GameRenderer.getPositionTexColorShader() : GameRenderer.getPositionColorShader());
    VertexBuffer.unbind();
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    poseStack.popPose();
  }

  @SubscribeEvent
	public static void renderLevel(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
      currentStage = 1;
			RenderSystem.depthMask(false);
			renderShapes(event);
      RenderSystem.enableCull();
      RenderSystem.depthMask(true);
      currentStage = 0;
		} else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
      currentStage = 2;
			RenderSystem.depthMask(true);
			renderShapes(event);
      RenderSystem.enableCull();
      RenderSystem.depthMask(true);
      currentStage = 0;
		}
	}

  private static void renderShapes(RenderLevelStageEvent event) {
    Minecraft minecraft = Minecraft.getInstance();
    ClientLevel level = minecraft.level;
    Entity entity = minecraft.gameRenderer.getMainCamera().getEntity();
    if (level != null && entity != null) {
      poseStack = event.getPoseStack();
      projectionMatrix = event.getProjectionMatrix();
      Vec3 pos = entity.getPosition(event.getPartialTick());
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      <#assign dependenciesCode><#compress>
        <@procedureDependenciesCode dependencies, {
          "dimension": "level.dimension()",
          "entity": "entity",
          "partialTick": "event.getPartialTick()",
          "ticks": "event.getRenderTick()",
          "x": "pos.x()",
          "y": "pos.y()",
          "z": "pos.z()",
          "world": "level",
          "event": "event"
        }/>
      </#compress></#assign>
      execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
		  RenderSystem.enableDepthTest();
    }
  }
  </#if>