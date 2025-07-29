<@addTemplate file="utils/list/list_get.java.ftl"/>
<#if input_id$type == "text">
<#assign cls=input$type?replace("\"","")>
/*@${cls}*/(getListElement(${input$list},${opt.toInt(input$index)},${cls}.class,${input$defaultValue}))
<#else>
<@addTemplate file="utils/list/util_get_class.java.ftl"/>
(getListElement(${input$list},${opt.toInt(input$index)},getClass(${input$type}),${input$defaultValue}))
</#if>
