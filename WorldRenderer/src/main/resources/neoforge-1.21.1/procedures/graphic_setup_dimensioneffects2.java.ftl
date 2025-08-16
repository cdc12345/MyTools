<#if field$reference == "OVERWORLD">
  register(${input$effects}, createOverworldEffects(${input$whitelight}, ${input$ambientlight}, ${input$fog}));
<#elseif field$reference == "NETHER">
  register(${input$effects}, createNetherEffects(${input$whitelight}, ${input$ambientlight}, ${input$fog}));
<#elseif field$reference == "END">
  register(${input$effects}, createEndEffects(${input$whitelight}, ${input$ambientlight}, ${input$fog}));
</#if>