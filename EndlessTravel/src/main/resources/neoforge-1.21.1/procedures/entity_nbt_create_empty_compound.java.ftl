<-- unchecked -->
<#assign tagSplit = opt.removeParentheses(input$tagName)?replace("\"","")?split(".")>
${input$entity}.getPersistentData()<#list tagSplit as tag><#if tag?has_next>.getCompound("${tag}")<#else>.put("${tag}",new CompoundTag())</#if></#list>;