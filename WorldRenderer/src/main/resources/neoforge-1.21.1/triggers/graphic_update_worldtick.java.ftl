<#include "procedures.java.ftl">

<#assign dependenciesCode><#compress>
  <@procedureDependenciesCode dependencies, {
    "dimension": "level.dimension()",
    "entity": "entity",
    "partialTick": "partialTick",
    "ticks": "++ticks",
    "x": "pos.x()",
    "y": "pos.y()",
    "z": "pos.z()",
    "world": "level",
    "event": "event"
  }/>
</#compress></#assign>

@EventBusSubscriber(value = Dist.CLIENT)
public class ${name}Procedure {
  <#if dependenciesCode?contains("ticks")>private static int ticks = 0;</#if>

  @SubscribeEvent
  public static void updateWorldTick(ClientTickEvent.Pre event) {
    Minecraft minecraft = Minecraft.getInstance();
    ClientLevel level = minecraft.level;
    Entity entity = minecraft.gameRenderer.getMainCamera().getEntity();
    if (level != null && entity != null) {
      <#if dependenciesCode?contains("partialTick")>
        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        Vec3 pos = entity.getPosition(partialTick);
      <#else>
        Vec3 pos = entity.getPosition(minecraft.getTimer().getGameTimeDeltaPartialTick(false));
      </#if>
      execute(event<#if dependenciesCode?has_content>, </#if>${dependenciesCode});
    }
  }