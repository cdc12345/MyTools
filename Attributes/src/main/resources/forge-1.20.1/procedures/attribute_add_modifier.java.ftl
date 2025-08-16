<#-- ${input$unique_id},${input$amount} -->
<#--  -->
<#-- ${field$attribute},${field$operation} -->
if (event instanceof ItemAttributeModifierEvent _itemAttributeModifierEvent){
		<#if field$slot == "ANY">
		if (_itemAttributeModifierEvent.getSlotType() == EquipmentSlot.${field$slot}){
		</#if>
			_itemitemAttributeModifierEvent.addModifier(Attributes.${field$attribute},new AttributeModifier(${input$unique_id},${input$amount},
					AttributeModifier.Operation.${field$operation}));
		<#if field$slot == "ANY">}</#if>
}
