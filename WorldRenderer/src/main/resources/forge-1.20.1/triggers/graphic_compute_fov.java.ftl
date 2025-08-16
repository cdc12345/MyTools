<#include "procedures.java.ftl">
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ${name}Procedure {
  public static ViewportEvent.ComputeFov provider = null;

  public static void setFOV(double fov) {
    provider.setFOV(fov);
  }

  @SubscribeEvent
  public static void computeFOV(ViewportEvent.ComputeFov event) {
    provider = event;
    ClientLevel level = Minecraft.getInstance().level;
    Entity entity = provider.getCamera().getEntity();
    if (level != null && entity != null) {
      Vec3 entPos = entity.getPosition((float) provider.getPartialTick());
      <#assign dependenciesCode><#compress>
        <@procedureDependenciesCode dependencies, {
          "dimension": "level.dimension()",
          "entity": "entity",
          "fov": "provider.getFOV()",
          "partialTick": "provider.getPartialTick()",
          "x": "entPos.x()",
          "y": "entPos.y()",
          "z": "entPos.z()",
          "world": "level",
          "event": "provider"
        }/>
      </#compress></#assign>
      execute(provider<#if dependenciesCode?has_content>, </#if>${dependenciesCode});
    }
  }