if (event instanceof ItemAttributeModifierEvent _itemAttributeModifierEvent){
			_itemAttributeModifierEvent.addModifier(Attributes.${field$attribute},new AttributeModifier(${input$unique_id},${input$amount},
					AttributeModifier.Operation.${field$operation}),EquipmentSlotGroup.${field$slot});
}
