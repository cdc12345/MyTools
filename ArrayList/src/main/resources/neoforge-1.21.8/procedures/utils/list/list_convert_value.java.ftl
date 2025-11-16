	private static double toSupportedType(Number value) {
		return value.doubleValue();
	}

	private static <E> E toSupportedType(E e){
		return e;
	}