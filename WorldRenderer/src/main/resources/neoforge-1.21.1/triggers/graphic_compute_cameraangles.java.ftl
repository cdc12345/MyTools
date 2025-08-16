<#include "procedures.java.ftl">
@EventBusSubscriber(value = Dist.CLIENT)
public class ${name}Procedure {
  public static ViewportEvent.ComputeCameraAngles provider = null;

  public static void setAngles(float yaw, float pitch, float roll) {
    provider.setYaw(yaw);
    provider.setPitch(pitch);
    provider.setRoll(roll);
  }
  
  @SubscribeEvent
  public static void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
    provider = event;
    ClientLevel level = Minecraft.getInstance().level;
    Entity entity = provider.getCamera().getEntity();
    if (level != null && entity != null) {
      Vec3 entPos = entity.getPosition((float) provider.getPartialTick());
      <#assign dependenciesCode><#compress>
        <@procedureDependenciesCode dependencies, {
          "dimension": "level.dimension()",
          "entity": "entity",
          "yaw": "provider.getYaw()",
          "pitch": "provider.getPitch()",
          "roll": "provider.getRoll()",
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