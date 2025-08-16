<#include "procedures.java.ftl">

<#assign dependenciesCode><#compress>
  <@procedureDependenciesCode dependencies, {
    "dimension": "level.dimension()",
    "entity": "entity",
    "partialTick": "minecraft.getPartialTick()",
    "ticks": "++ticks",
    "x": "pos.x()",
    "y": "pos.y()",
    "z": "pos.z()",
    "world": "level",
    "event": "event"
  }/>
</#compress></#assign>

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ${name}Procedure {
  <#if dependenciesCode?contains("ticks")>private static int ticks = 0;</#if>

  @SubscribeEvent
  public static void updateWorldTick(TickEvent.ClientTickEvent event) {
    if (event.phase != TickEvent.Phase.START)
      return;
    Minecraft minecraft = Minecraft.getInstance();
    ClientLevel level = minecraft.level;
    Entity entity = minecraft.gameRenderer.getMainCamera().getEntity();
    if (level != null && entity != null) {
      Vec3 pos = entity.getPosition(minecraft.getPartialTick());
      execute(event<#if dependenciesCode?has_content>, </#if>${dependenciesCode});
    }
  }