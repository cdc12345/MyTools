<#include "mcitems.ftl">
<#if input_id$element?starts_with("mcitem")>
${input$list}.add(${mappedMCItemToItemStackCode(input$element,1)});
<#else>
<@addTemplate file="utils/list/list_convert_value.java.ftl"/>
${input$list}.add(toSupportedType(${input$element}));
</#if>

