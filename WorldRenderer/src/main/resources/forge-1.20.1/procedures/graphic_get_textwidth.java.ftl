<#if input$scale == "/*@int*/1">
  (Minecraft.getInstance().font.width(${input$texts}) * Minecraft.getInstance().getWindow().getGuiScale())
<#else>
  (Minecraft.getInstance().font.width(${input$texts}) * Minecraft.getInstance().getWindow().getGuiScale() * ${input$scale})
</#if>