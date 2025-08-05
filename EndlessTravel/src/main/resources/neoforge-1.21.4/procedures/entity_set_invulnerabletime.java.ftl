<#if input_id$value == "math_plus_self">
${input$entity}.invulnerableTime += ${input$value};
<#else>
${input$entity}.invulnerableTime = ${input$value};
</#if>