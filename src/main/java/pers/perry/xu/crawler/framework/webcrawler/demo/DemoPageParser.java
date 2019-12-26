package pers.perry.xu.crawler.framework.webcrawler.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;

import pers.perry.xu.crawler.framework.webcrawler.model.WebMedia;
import pers.perry.xu.crawler.framework.webcrawler.parser.WebPageParser;

public class DemoPageParser implements WebPageParser {

	@Override
	public List<String> getSeedUrlsList(Element bodyElement) {
		List<String> list = new ArrayList<String>();
		list.add("http://www.mmonly.cc/mmtp/xgmn/[0-9]+.html");
		return getMatchingList(list, bodyElement.toString());
	}

	@Override
	public String getText(Element bodyElement) {
		// 匹配exp前面的位置 (?=exp)
		// 匹配exp后面的位置 (?<=exp)
		return "(?<=<img alt=\")[\\u4e00-\\u9fa5]+(?=\"\\s+src)";
	}

	@Override
	public List<WebMedia> getMediaDataList(Element bodyElement) {
//		List<String> targetList = new ArrayList<String>();
//		String pattern = "src=\"(.+?\\.jpg)\"";
//		Pattern webPattern = Pattern.compile(pattern);
//		Matcher webMatcher = webPattern.matcher(bodyElement.toString());
//		while (webMatcher.find()) {
//			String res = webMatcher.group();
//			res = res.replace("src=", "").replace("\"", "");
//			targetList.add(res);
//		}
//		return targetList;
		return null;
	}

	protected List<String> getMatchingList(List<String> patterns, String html) {
		List<String> targetList = new ArrayList<String>();
		for (String pattern : patterns) {
			// get matching url for each pattern, and add into queue
			Pattern webPattern = Pattern.compile(pattern);
			Matcher webMatcher = webPattern.matcher(html);
			while (webMatcher.find()) {
				targetList.add(webMatcher.group());
			}
		}
		return targetList;
	}
}
