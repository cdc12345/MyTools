<#-- ${parameter$itemstack},${parameter$attributesmodifiers} -->
<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent private static void onItemAttributeModifierGet(ItemAttributeModifierEvent event){
         <#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"attributesmodifiers": "event.getModifiers()",
			"itemstack": "event.getItemStack()",
			"event": "event"
			}
			/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}