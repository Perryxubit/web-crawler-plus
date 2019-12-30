package pers.perry.xu.crawler.framework.webcrawler.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pers.perry.xu.crawler.framework.webcrawler.model.WebMedia;
import pers.perry.xu.crawler.framework.webcrawler.model.WebPage;
import pers.perry.xu.crawler.framework.webcrawler.parser.WebPageParser;

public class TGBusPageParser extends DemoPageParser implements WebPageParser {

	static int index = 0;

	@Override
	public List<WebMedia> getMediaDataList(WebPage page) {
		List<WebMedia> list = new ArrayList<WebMedia>();
		// TODO: to be added in future in case we need it.
		return list;
	}

	@Override
	public String getText(WebPage page) {
		// download text between <p> and </p>
		String mainContent = page.getWebBody().getElementsByClass("article-main-contentraw").get(0).toString();
		Pattern webPattern = Pattern.compile("(?<=<p>).*?(?=</p>)");
		Matcher webMatcher = webPattern.matcher(mainContent);
		StringBuilder res = new StringBuilder();
		while (webMatcher.find()) {
			res.append(webMatcher.group());
		}
		return res.toString();
	}

	@Override
	public List<String> getSeedUrlsList(WebPage page) {
		// crawling all relate news
		String baseStr = page.getWebBody().getElementById("relateNews").toString();
		List<String> list = new ArrayList<String>();
		list.add("https://www.tgbus.com/news/[0-9]+");
		return getMatchingList(list, baseStr);
	}
}
