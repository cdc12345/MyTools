<#-- ${parameter$tab} -->
<#include "procedures.java.ftl">
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD) public class ${name}Procedure {
	@SubscribeEvent public static void onEventTriggered(Event event) {
			<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"tab": ""
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});

	}