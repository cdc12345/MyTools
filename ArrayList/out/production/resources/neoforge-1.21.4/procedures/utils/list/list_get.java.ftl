private static <E> E getListElement(ArrayList<Object> objects,int index,Class<E> eClass,Object defaultValue){
	if (index < objects.size()){
		var element = objects.get(index);
		if (eClass.isInstance(element)){
			return eClass.cast(element);
		}
	}
	return eClass.cast(defaultValue);
}