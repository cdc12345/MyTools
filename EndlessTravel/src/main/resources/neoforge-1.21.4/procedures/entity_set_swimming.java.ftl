${input$entity}.setSwimming(${input$swimming});
<#if input$swimming == "true">
${input$entity}.setPose(Pose.SWIMMING);
</#if>