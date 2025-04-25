<#include "procedures.java.ftl">
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD) public class ${name}Procedure {
	@SubscribeEvent public static void onCreativeTabBuild(BuildCreativeModeTabContentsEvent event) {
			<#assign dependenciesCode><#compress>
				<@procedureDependenciesCode dependencies, {
				    "tab": "event.getTabKey().location().getPath()",
					"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}