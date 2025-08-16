<#assign mode = "Minecraft.getInstance().options.getCloudsType()">
<#if field$mode == "FANCY">
  <#assign mode = "CloudStatus.FANCY">
<#elseif field$mode == "FAST">
  <#assign mode = "CloudStatus.FAST">
</#if>
renderClouds(${mode}, ${opt.toFloat(input$altitude)}, ${opt.toFloat(input$vx)}, ${opt.toFloat(input$vz)}, ${opt.toInt(input$color)});