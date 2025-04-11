<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onEntityAttacked(LivingDamageEvent.Pre event) {
			AtomicDouble amount = new AtomicDouble(event.getOriginalDamage());
			<#assign dependenciesCode><#compress>
				<@procedureDependenciesCode dependencies, {
					"x": "event.getEntity().getX()",
					"y": "event.getEntity().getY()",
					"z": "event.getEntity().getZ()",
					"world": "event.getEntity().level()",
					"amount": "amount",
					"entity": "event.getEntity()",
					"damagesource": "event.getSource()",
					"sourceentity": "event.getSource().getEntity()",
					"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
			event.setNewDamage(amount.floatValue());
	}