package org.liquid.convenient.utils;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Providers {
	public static MCreatorTabs getTabs(MCreator origin){
		try {
			return origin.getTabs();
		} catch (Throwable throwable){
			Method method;
			try {
				method = origin.getClass().getMethod("getMCreatorTabs");
				return (MCreatorTabs) method.invoke(origin);
			} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}

		}
	}

	public static Object tryFindWorkspacePanel(MCreator mcreator){
		Object object;
		try {
			var field = mcreator.getClass().getField("mv");
			object = field.get(mcreator);
		} catch (Exception ex) {
			object = Providers.getTabs(mcreator).getCurrentTab().getContent();
		}
		return object;
	}


}
