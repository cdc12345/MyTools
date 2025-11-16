private static Class<?> getClass(String clas){
	try {
		return Class.forName(clas);
	} catch (ClassNotFoundException e) {
		throw new RuntimeException(e);
	}
}