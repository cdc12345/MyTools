<#-- ${parameter$itemstack},${parameter$attributesmodifiers} -->
<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onItemAttributeModifierRegistered(ItemAttributeModifierEvent event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
						"attributesmodifiers": "event.getModifiers()",
            			"itemstack": "event.getItemStack()",
            			"event": "event"
            }/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}