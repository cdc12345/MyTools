<@addTemplate file="utils/map/map_get.java.ftl"/>
<#if input_id$type == "text">
<#assign cls=input$type?replace("\"","")>
/*@${cls}*/(getMapValue(${input$map},${input$map_key},${cls}.class,${input$defaultValue}))
<#else>
<@addTemplate file="utils/list/util_get_class.java.ftl"/>
(getMapValue(${input$map},${input$map_key},getClass(${input$type}),${input$defaultValue}))
</#if>
