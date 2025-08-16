<#if addTemplate??>
<@addTemplate file="utils/list/list_convert_value.java.ftl"/>
${input$map}.put(${input$map_key},toSupportedType(${input$map_value}));
<#else>
${input$map}.put(${input$map_key},comp.toSupportedType(${input$map_value}));
</#if>