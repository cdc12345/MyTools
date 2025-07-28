<-- ${input$index} ${input$type} -->
<#assign cls=input$type?replace("\"","")>
<@addTemplate file="utils/list/list_get.java.ftl"/>
/*@${cls}*/(getListElement(${input$list},${opt.toInt(input$index)},${cls}.class,${input$defaultValue}))