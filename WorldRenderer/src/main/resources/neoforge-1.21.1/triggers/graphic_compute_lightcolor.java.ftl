<#include "procedures.java.ftl">
@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ${name}Procedure {
  private static float skyLevel = 0.0F;
  private static float blockLevel = 0.0F;
  private static Vector3f skyColor = null;
  private static Vector3f blockColor = null;
  private static final Consumer<Object[]> CONSUMER = params -> {
    int pixelX = (Integer) params[5];
    int pixelY = (Integer) params[6];
    if (pixelX == 0 && pixelY == 0) {
      Minecraft minecraft = Minecraft.getInstance();
      Entity entity = minecraft.gameRenderer.getMainCamera().getEntity();
      if (entity != null) {
        ClientLevel level = minecraft.level;
        float partialTick = (Float) params[1];
        Vec3 pos = entity.getPosition(partialTick);
        <#assign dependenciesCode><#compress>
          <@procedureDependenciesCode dependencies, {
            "dimension": "level.dimension()",
            "entity": "entity",
					  "partialTick": "partialTick",
            "x": "pos.x()",
            "y": "pos.y()",
            "z": "pos.z()",
            "world": "level",
            "event": "null"
          }/>
        </#compress></#assign>
        execute(null<#if dependenciesCode?has_content>, </#if>${dependenciesCode});
      }
    }
    calculateColor((Vector3f) params[7], pixelX, pixelY);
  };

  private static float calculateBaseLevel(float level) {
		return level * level * (level * -2.0F + 3.0F);
	}

  private static void calculateColor(Vector3f lightColor, int pixelX, int pixelY) {
    if (pixelX == pixelY)
      return;
    if (pixelX > pixelY) {
      if (blockColor == null)
        return;
      if (blockLevel == 0.0F)
			  return;
      float level = Math.abs(calculateBaseLevel(pixelX / 15.0F) - calculateBaseLevel(pixelY / 15.0F)) * blockLevel;
      lightColor.set(
        Mth.clamp(Mth.lerp(level, lightColor.x(), blockColor.x()), 0.0F, 1.0F),
        Mth.clamp(Mth.lerp(level, lightColor.y(), blockColor.y()), 0.0F, 1.0F),
        Mth.clamp(Mth.lerp(level, lightColor.z(), blockColor.z()), 0.0F, 1.0F)
      );
    } else {
      if (skyColor == null)
        return;
      if (skyLevel == 0.0F)
			  return;
      float level = Math.abs(pixelX - pixelY) / 15.0F * skyLevel;
      lightColor.set(
        Mth.clamp(Mth.lerp(level, lightColor.x(), skyColor.x()), 0.0F, 1.0F),
        Mth.clamp(Mth.lerp(level, lightColor.y(), skyColor.y()), 0.0F, 1.0F),
        Mth.clamp(Mth.lerp(level, lightColor.z(), skyColor.z()), 0.0F, 1.0F)
      );
    }
	}

  public static void setBlockColor(int blockColor) {
    setBlockColor(1.0F, blockColor);
  }

  public static void setBlockColor(float level, int blockColor) {
    ${name}Procedure.blockLevel = Mth.clamp(level, 0.0F, 1.0F);
    ${name}Procedure.blockColor = new Vector3f((blockColor >> 16 & 255) / 255.0F, (blockColor >> 8 & 255) / 255.0F, (blockColor & 255) / 255.0F);
  }

  public static void setSkyColor(int skyColor) {
    setSkyColor(1.0F, skyColor);
  }

  public static void setSkyColor(float level, int skyColor) {
    ${name}Procedure.skyLevel = Mth.clamp(level, 0.0F, 1.0F);
    ${name}Procedure.skyColor = new Vector3f((skyColor >> 16 & 255) / 255.0F, (skyColor >> 8 & 255) / 255.0F, (skyColor & 255) / 255.0F);
  }

  @SubscribeEvent
  public static void lightColorSetup(FMLClientSetupEvent event) {
    try {
      Field field = DimensionSpecialEffectsManager.class.getDeclaredField("EFFECTS");
      field.setAccessible(true);
      for (DimensionSpecialEffects dimensionSpecialEffects : ((com.google.common.collect.ImmutableMap<ResourceLocation, DimensionSpecialEffects>) field.get(null)).values()) {
        Class<?> effects = dimensionSpecialEffects.getClass();
			  ((Set<Consumer<Object[]>>) effects.getField("CUSTOM_LIGHTS").get(null)).add(CONSUMER);
      }
    } catch (Exception e) {}
  }