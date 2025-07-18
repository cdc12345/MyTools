<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onLayerRender(RenderGuiLayerEvent.Pre event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"path": "event.getName().getPath()",
				"namespace": "event.getName().getNamespace()",
				"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}