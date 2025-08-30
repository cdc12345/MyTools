if (${input$animal} instanceof Animal _animal){
<#if input_id$value == "math_plus_self">
	_animal.setInLoveTime(_animal.getInLoveTime() + ${opt.toInt(input$in_love_time)});
<#else>
	_animal.setInLoveTime(${opt.toInt(input$in_love_time)});
</#if>
}