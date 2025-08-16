<#if addTemplate??>
<@addTemplate file="utils/list/list_get.java.ftl"/>
</#if>
<#if input_id$type == "text">
<#assign cls=input$type?replace("\"","")>
/*@${cls}*/(getListElement(${input$list},${opt.toInt(input$index)},${cls}.class,${input$defaultValue}))
<#else>
<#if addTemplate??>
<@addTemplate file="utils/list/util_get_class.java.ftl"/>
(getListElement(${input$list},${opt.toInt(input$index)},getClass(${input$type}),${input$defaultValue}))
<#else>
(comp.getListElement(${input$list},${opt.toInt(input$index)},getClass(${input$type}),${input$defaultValue}))
</#if>
</#if>
