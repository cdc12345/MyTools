if (${input$entity} instanceof LivingEntity _living){
<#if input_id$value == "math_plus_self">
		_living.setArrowCount(_living.getArrowCount() + ${input$value});
<#else>
		_living.setArrowCount(${input$value});
</#if>
}