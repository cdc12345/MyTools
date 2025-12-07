<#include "mcitems.ftl">
<#if input_id$value?starts_with("mcitem")>
${input$list}.add(${input$index},${mappedMCItemToItemStackCode(input$element,1)});
<#else>
<@addTemplate file="utils/list/list_convert_value.java.ftl"/>
${input$list}.add(${input$index},toSupportedType(${input$element}));
</#if>