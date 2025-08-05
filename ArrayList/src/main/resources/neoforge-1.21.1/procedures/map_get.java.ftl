<#assign cls=generator.map(field$type,"supportedtypes",0)>
<@addTemplate file="utils/map/map_get.java.ftl"/>
/*@${cls}*/(getMapValue(${input$map},${input$map_key},${cls}.class,${generator.map(field$type,"supportedtypes",1)}))