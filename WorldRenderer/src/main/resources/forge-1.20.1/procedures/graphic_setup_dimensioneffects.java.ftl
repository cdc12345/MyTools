<#if field$reference == "OVERWORLD">
  register(${input$effects}, createOverworldEffects(false, false, ${input$fog}));
<#elseif field$reference == "NETHER">
  register(${input$effects}, createNetherEffects(false, true, ${input$fog}));
<#elseif field$reference == "END">
  register(${input$effects}, createEndEffects(true, false, ${input$fog}));
</#if>