<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onPlayerFOV(ComputeFovModifierEvent event) {
			AtomicDouble amount = new AtomicDouble(event.getFovModifier());
			<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"x": "event.getPlayer().getX()",
				"y": "event.getPlayer().getY()",
				"z": "event.getPlayer().getZ()",
				"world": "event.getPlayer().level()",
				"amount": "amount",
				"entity": "event.getPlayer()",
				"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
            event.setNewFovModifier(amount.floatValue());
	}