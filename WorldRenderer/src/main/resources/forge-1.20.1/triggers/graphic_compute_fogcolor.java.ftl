<#include "procedures.java.ftl">
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ${name}Procedure {
  public static ViewportEvent.ComputeFogColor provider = null;

  public static void setColor(int color) {
    provider.setRed((color >> 16 & 255) / 255.0F);
    provider.setGreen((color >> 8 & 255) / 255.0F);
    provider.setBlue((color & 255) / 255.0F);
  }

  public static void setColor(float level, int color) {
    if (level <= 0.0F)
      return;
    if (level >= 1.0F) {
      provider.setRed((color >> 16 & 255) / 255.0F);
      provider.setGreen((color >> 8 & 255) / 255.0F);
      provider.setBlue((color & 255) / 255.0F);
    } else {
      level = Mth.clamp(level, 0.0F, 1.0F);
      provider.setRed(Mth.clamp(Mth.lerp(level, Mth.clamp(provider.getRed(), 0.0F, 1.0F), (color >> 16 & 255) / 255.0F), 0.0F, 1.0F));
      provider.setGreen(Mth.clamp(Mth.lerp(level, Mth.clamp(provider.getGreen(), 0.0F, 1.0F), (color >> 8 & 255) / 255.0F), 0.0F, 1.0F));
      provider.setBlue(Mth.clamp(Mth.lerp(level, Mth.clamp(provider.getBlue(), 0.0F, 1.0F), (color & 255) / 255.0F), 0.0F, 1.0F));
    }
  }

  @SubscribeEvent
  public static void computeFogColor(ViewportEvent.ComputeFogColor event) {
    provider = event;
    ClientLevel level = Minecraft.getInstance().level;
    Entity entity = provider.getCamera().getEntity();
    if (level != null && entity != null) {
      Vec3 entPos = entity.getPosition((float) provider.getPartialTick());
      <#assign dependenciesCode><#compress>
        <@procedureDependenciesCode dependencies, {
          "dimension": "level.dimension()",
          "entity": "entity",
          "red": "provider.getRed() * 255.0F",
          "green": "provider.getGreen() * 255.0F",
          "blue": "provider.getBlue() * 255.0F",
          "partialTick": "provider.getPartialTick()",
          "x": "entPos.x()",
          "y": "entPos.y()",
          "z": "entPos.z()",
          "world": "level",
          "event": "provider"
        }/>
      </#compress></#assign>
      execute(provider<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
    }
  }