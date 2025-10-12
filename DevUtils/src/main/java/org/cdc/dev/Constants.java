package org.cdc.dev;

import net.mcreator.io.UserFolderManager;

import java.io.File;

public final class Constants {
	public static final String JAVAPARSER_URL = "https://repo1.maven.org/maven2/com/github/javaparser/javaparser-core/3.27.1/javaparser-core-3.27.1.jar";

	public static File getCacheFile(){
		var cache = UserFolderManager.getFileFromUserFolder("cache");
		if (!cache.exists()){
			cache.mkdir();
		}
		return cache;
	}
}
