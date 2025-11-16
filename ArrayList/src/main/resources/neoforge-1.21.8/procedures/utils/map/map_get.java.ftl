private static <E> E getMapValue(HashMap<String,Object> objects,String map_key,Class<E> eClass,Object defaultValue){
	if (objects.containsKey(map_key)){
		Object object = objects.getOrDefault(map_key, defaultValue);
		if (eClass.isInstance(object)) {
			return eClass.cast(object);
		}
	}
	return eClass.cast(defaultValue);
}