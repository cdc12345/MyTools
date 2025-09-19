package net.nerdypuzzle.forgemixins.utils;

import net.nerdypuzzle.forgemixins.ui.IOptionComponentProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class JavaUtils {
	public static List<IOptionComponentProvider> readAllExposedProperty(File mixinJavaClas) throws IOException {
		var lines = Files.readAllLines(mixinJavaClas.toPath());
		boolean unique = false;
		String comment = null;

		for (String line: lines){
			String optimised = line.trim();
			if (optimised.startsWith("@Unique")){
				unique = true;
			}
		}
		return new ArrayList<>();
	}
}
