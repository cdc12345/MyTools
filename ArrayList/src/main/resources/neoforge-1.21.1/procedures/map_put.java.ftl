<#if addTemplate??>
<@addTemplate file="utils/list/list_convert_value.java.ftl"/>
</#if>
${input$map}.put(${input$map_key},toSupportedType((${input$map_value}));