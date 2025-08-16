<#if addTemplate??>
<@addTemplate file="utils/map/map_get.java.ftl"/>
</#if>
<#if input_id$type == "text">
	<#if addTemplate??>
	<#assign cls=input$type?replace("\"","")>
	/*@${cls}*/(getMapValue(${input$map},${input$map_key},${cls}.class,${input$defaultValue}))
	<#else>
	/*@${cls}*/(comp.getMapValue(${input$map},${input$map_key},${cls}.class,${input$defaultValue}))
	</#if>
<#else>
	<#if addTemplate??>
	<@addTemplate file="utils/list/util_get_class.java.ftl"/>
	(getMapValue(${input$map},${input$map_key},getClass(${input$type}),${input$defaultValue}))
	<#else>
	(comp.getMapValue(${input$map},${input$map_key},getClass(${input$type}),${input$defaultValue}))
	</#if>
</#if>
