<#assign cls=generator.map(field$type,"supportedtypes",0)>
/*@${cls}*/(new Object() {
	private <E> E getListElement(ArrayList<Object> objects, int index, Class<E> eClass,
			Object defaultValue) {
		if (index < objects.size()) {
			var element = objects.get(index);
			if (eClass.isInstance(element)) {
				return eClass.cast(element);
			}
		}
		return eClass.cast(defaultValue);
	}
}.getListElement(${input$list},${opt.toInt(input$index)},${cls}.class,${generator.map(field$type,"supportedtypes",1)}))