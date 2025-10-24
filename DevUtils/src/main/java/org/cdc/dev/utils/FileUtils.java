package org.cdc.dev.utils;

import net.mcreator.generator.GeneratorTokens;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class FileUtils {

	private static final Logger LOGGER = LogManager.getLogger(FileUtils.class);

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


	public static String getFileSha1(File file) {
		try (FileInputStream in = new FileInputStream(file)) {
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-1");
				byte[] buffer = new byte[1024 * 1024 * 10];

				int len;
				while ((len = in.read(buffer)) > 0) {
					digest.update(buffer, 0, len);
				}
				String sha1 = new BigInteger(1, digest.digest()).toString(16);
				int length = 40 - sha1.length();
				if (length > 0) {
					for (int i = 0; i < length; i++) {
						sha1 = "0" + sha1;
					}
				}
				return sha1;
			} catch (IOException | NoSuchAlgorithmException e) {
				LOGGER.error(e);
			}
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return null;
	}

	public static File getModifiersPath(Workspace workspace){
		var path = new File(workspace.getWorkspaceFolder(),"modifiers");
		if (!path.exists()){
			path.mkdir();
		}
		return path;
	}

	public static String getLanguageFile(Workspace workspace,String langKey, String configurationValue){
		String uppercaseLangName =
				langKey.split("_")[0] + "_" + langKey.split("_")[1].toUpperCase(Locale.ENGLISH);

		String fileName = GeneratorTokens.replaceTokens(workspace,
				configurationValue.replace("@langname", langKey).replace("@lang_NAME", uppercaseLangName));

		return fileName;
	}
}
