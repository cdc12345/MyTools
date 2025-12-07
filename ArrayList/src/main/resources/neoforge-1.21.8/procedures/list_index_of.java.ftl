<#if input_id$value?starts_with("mcitem_all")>
(${input$list}.indexOf(${mappedMCItemToItemStackCode(input$value,1)}))
<#else>
(${input$list}.indexOf(${input$value}))
</#if>