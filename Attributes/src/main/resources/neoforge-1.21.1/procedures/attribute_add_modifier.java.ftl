if (event instanceof ItemAttributeModifierEvent _itemAttributeModifierEvent){
			_itemAttributeModifierEvent.addModifier(${generator.map(field$attribute, "attributes")},${input$modifier},EquipmentSlotGroup.${field$slot});
}
