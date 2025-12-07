<#-- ${parameter$namespace},${parameter$path} -->
<#include "procedures.java.ftl">
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD) public class ${name}Procedure {
	@SubscribeEvent public static void onEventTriggered(Event event) {
			<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"path": "", 
			"namespace": ""
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});

	}