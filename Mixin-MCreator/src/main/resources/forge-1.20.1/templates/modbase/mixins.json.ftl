{
  "required": true,
  "package": "${package}.mixins",
  "compatibilityLevel": "JAVA_17",
  "refmap": "mixins.${modid}.refmap.json",
  "mixins": [
    <#list w.getElementsOfType("newmixin") as element>
        <#assign elem=element.getGeneratableElement()>
        <#if elem.isClient??>
            <#if elem.isClient == false>
            <#if elem.mixins??>
    "${elem.mixins}"<#sep>,
            </#if>
            </#if>
        </#if>
    </#list>

  ],
  "client": [
    <#list w.getElementsOfType("newmixin") as element>
        <#assign elem=element.getGeneratableElement()>
        <#if elem.isClient??>
            <#if elem.isClient == true>
            <#if elem.mixins??>
    "${elem.mixins}"<#sep>,
            </#if>
            </#if>
        </#if>
    </#list>

  ],
  "minVersion": "0.8"
}