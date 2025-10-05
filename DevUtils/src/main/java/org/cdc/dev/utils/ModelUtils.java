package org.cdc.dev.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModelUtils {
	public static String getJavaModelMappings(Path file) throws IOException {
		String content = Files.readString(file);
		if (content.contains("for Yarn")){
			return "Yarn";
		}
		if (content.contains("with Mojang mappings")){
			return "Mojang";
		}
		return "MCP";
	}
}
