package org.cdc.temp.utils;

public class PathUtils {
	public static String getPath(String wholePath){
		return wholePath.substring(wholePath.indexOf(':')+1);
	}
}
