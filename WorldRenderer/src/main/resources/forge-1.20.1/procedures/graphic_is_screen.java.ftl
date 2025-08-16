<#if field$screen == "Ingame">
  (Minecraft.getInstance().screen == null)
<#else>
  (Minecraft.getInstance().screen instanceof ${generator.map(field$screen, "screens")})
</#if>