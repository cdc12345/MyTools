<#-- ${input$unique_id},${input$amount} -->
<#--  -->
<#-- ${field$attribute},${field$operation} -->
if (event instanceof ItemAttributeModifierEvent _itemAttributeModifierEvent){
		<#if field$slot != "ANY">
		if (_itemAttributeModifierEvent.getSlotType() == EquipmentSlot.${field$slot}){
		</#if>
			_itemAttributeModifierEvent.addModifier(${generator.map(field$attribute, "attributes")},${input$modifier});
		<#if field$slot != "ANY">}</#if>
}
