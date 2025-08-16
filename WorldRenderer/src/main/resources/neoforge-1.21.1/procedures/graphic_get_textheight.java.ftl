<#if input$scale == "/*@int*/1">
  (Minecraft.getInstance().font.lineHeight * Minecraft.getInstance().getWindow().getGuiScale())
<#else>
  (Minecraft.getInstance().font.lineHeight * Minecraft.getInstance().getWindow().getGuiScale() * ${input$scale})
</#if>