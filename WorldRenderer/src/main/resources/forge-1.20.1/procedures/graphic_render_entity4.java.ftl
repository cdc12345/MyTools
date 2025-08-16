<#assign entity = generator.map(field$entity, "entities", 1)!"null">
<#if entity != "null">
  renderEntity(world, ${entity}, ${input$x}, ${input$y}, ${input$depth}, ${opt.toFloat(input$yaw)}, ${opt.toFloat(input$pitch)}, ${opt.toFloat(input$roll)}, ${opt.toFloat(input$scale)}, ${input$modelonly});
</#if>