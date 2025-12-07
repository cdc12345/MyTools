<#if input_id$value?starts_with("mcitem_all")>
(${input$list}.lastIndexOf(${mappedMCItemToItemStackCode(input$value,1)}))
<#else>
(${input$list}.lastIndexOf(${input$value}))
</#if>