package org.cdc.temp.utils.wrap;

import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;

import java.util.List;

public class ModElementTypeLoaderWrap {

    private final Class<?> aClass;

    public ModElementTypeLoaderWrap(){
        aClass = ModElementTypeLoader.class;
    }

    public ModElementTypeLoaderWrap(ClassLoader classLoader) throws ClassNotFoundException {
        aClass = classLoader.loadClass("net.mcreator.element.ModElementTypeLoader");
    }

    @SuppressWarnings("unchecked")
	public List<ModElementType<?>> getREGISTERIES() throws NoSuchFieldException, IllegalAccessException {
        var field = aClass.getDeclaredField("REGISTRY");
        field.setAccessible(true);
        return (List<ModElementType<?>>) field.get(null);
    }

}
