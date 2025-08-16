<#assign entity = generator.map(field$entity, "entities", 1)!"null">
<#if entity != "null">
  renderEntity(${entity}, ${input$x}, ${input$y}, ${input$z}, ${opt.toFloat(input$yaw)}, ${opt.toFloat(input$pitch)}, ${opt.toFloat(input$roll)}, ${opt.toFloat(input$scale)}, ${input$glowing});
</#if>