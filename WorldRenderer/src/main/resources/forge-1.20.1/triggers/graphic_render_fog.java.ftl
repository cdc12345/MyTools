<#include "procedures.java.ftl">
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ${name}Procedure {
  public static ViewportEvent.RenderFog provider = null;

  public static void setDistance(float start, float end) {
    provider.setNearPlaneDistance(start);
    provider.setFarPlaneDistance(end);
    if (!provider.isCanceled()) {
      provider.setCanceled(true);
    }
  }

  public static void setShape(FogShape shape) {
    provider.setFogShape(shape);
    if (!provider.isCanceled()) {
      provider.setCanceled(true);
    }
  }

  @SubscribeEvent
  public static void renderFog(ViewportEvent.RenderFog event) {
    provider = event;
    if (provider.getMode() == FogRenderer.FogMode.FOG_TERRAIN) {
      ClientLevel level = Minecraft.getInstance().level;
      Entity entity = provider.getCamera().getEntity();
      if (level != null && entity != null) {
        Vec3 pos = entity.getPosition((float) provider.getPartialTick());
        <#assign dependenciesCode><#compress>
          <@procedureDependenciesCode dependencies, {
            "dimension": "level.dimension()",
            "entity": "entity",
            "end": "provider.getFarPlaneDistance()",
            "start": "provider.getNearPlaneDistance()",
            "partialTick": "provider.getPartialTick()",
            "x": "pos.x()",
            "y": "pos.y()",
            "z": "pos.z()",
            "world": "level",
            "event": "provider"
          }/>
        </#compress></#assign>
        execute(provider<#if dependenciesCode?has_content>, </#if>${dependenciesCode});
      }
    }
  }