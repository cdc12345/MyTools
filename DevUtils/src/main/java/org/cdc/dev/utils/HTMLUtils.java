package org.cdc.dev.utils;

import static net.mcreator.util.HtmlUtils.unescapeHtml;

public class HTMLUtils {
	public static String html2text(String input) {
		input = input.replaceAll("<head[\\s\\S]*?</head>", "")
				.replaceAll("(?i)<br[^>]* */?>", "\n").replaceAll("<.*?>", "").replaceAll(" +", " ").replaceAll("\n\n","\n");
		return unescapeHtml(input).trim();
	}
}
