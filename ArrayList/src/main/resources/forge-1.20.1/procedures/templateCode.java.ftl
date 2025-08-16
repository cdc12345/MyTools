var comp = new Object(){
	private double toSupportedType(Number value) {
			return value.doubleValue();
	}
	private <E> E toSupportedType(E e){
			return e;
	}
	private <E> E getListElement(ArrayList<Object> objects,int index,Class<E> eClass,Object defaultValue){
		if (index < objects.size()){
			var element = objects.get(index);
			if (eClass.isInstance(element)){
				return eClass.cast(element);
			}
		}
		return eClass.cast(defaultValue);
	}
	private Class<?> getClass(String clas){
		try {
			return Class.forName(clas);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	private <E> E getMapValue(HashMap<String,Object> objects,String map_key,Class<E> eClass,Object defaultValue){
		if (objects.containsKey(map_key)){
			Object object = objects.getOrDefault(map_key, defaultValue);
			if (eClass.isInstance(object)) {
				return eClass.cast(object);
			}
		}
		return eClass.cast(defaultValue);
	}
};