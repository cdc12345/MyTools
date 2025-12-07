<#include "mcitems.ftl">
<#if input_id$element?starts_with("mcitem_all")>
${input$list}.set(${opt.toInt(input$index)},${mappedMCItemToItemStackCode(input$element,1)})
<#else>
${input$list}.set(${opt.toInt(input$index)},${input$element});
</#if>