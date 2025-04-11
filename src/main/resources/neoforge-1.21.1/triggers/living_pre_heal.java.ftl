<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onEntityHealed(LivingHealEvent event) {
		if (event != null && event.getEntity() != null) {
			AtomicDouble amount = new AtomicDouble(event.getAmount());
			<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"x": "event.getEntity().getX()",
				"y": "event.getEntity().getY()",
				"z": "event.getEntity().getZ()",
				"world": "event.getEntity().level()",
				"amount": "amount",
				"entity": "event.getEntity()",
				"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
			event.setAmount(amount.floatValue());
		}
	}