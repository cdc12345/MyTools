package org.cdc.temp.utils;

import net.mcreator.io.UserFolderManager;

import java.io.File;

public class PathUtils {
	public static String getPath(String wholePath){
		return wholePath.substring(wholePath.indexOf(':')+1);
	}

	public static File getCacheFile(){
		var cache = UserFolderManager.getFileFromUserFolder("cache");
		if (!cache.exists()){
			cache.mkdir();
		}
		return cache;
	}
}
