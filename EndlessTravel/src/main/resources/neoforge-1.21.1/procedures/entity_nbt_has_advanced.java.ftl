<#-- ${input$entity},${input$tagName} -->
<#--  -->
<#--  -->
<#assign tagSplit = opt.removeParentheses(input$tagName)?replace("\"","")?split(".")>
(${input$entity}.getPersistentData()<#list tagSplit as tag><#if tag?has_next>.getCompound("${tag}")<#else>.contains("${tag}")</#if></#list>)