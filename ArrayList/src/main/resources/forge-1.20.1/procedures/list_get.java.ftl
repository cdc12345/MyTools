<#assign cls=generator.map(field$type,"supportedtypes",0)>
<#if addTemplate??>
<@addTemplate file="utils/list/list_get.java.ftl"/>
/*@${cls}*/(getListElement(${input$list},${opt.toInt(input$index)},${cls}.class,${generator.map(field$type,"supportedtypes",1)}))
<#else>
/*@${cls}*/(comp.getListElement(${input$list},${opt.toInt(input$index)},${cls}.class,${generator.map(field$type,"supportedtypes",1)}))
</#if>
