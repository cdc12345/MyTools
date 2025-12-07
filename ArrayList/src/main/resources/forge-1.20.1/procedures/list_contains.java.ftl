<#include "mcitems.ftl">
<#if input_id$value?starts_with("mcitem")>
(${input$list}.contains(${mappedMCItemToItemStackCode(input$value,1)}))
<#else>
(${input$list}.contains(${input$value}))
</#if>