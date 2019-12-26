package pers.perry.xu.crawler.framework.webcrawler.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pers.perry.xu.crawler.framework.webcrawler.model.MediaType;
import pers.perry.xu.crawler.framework.webcrawler.model.WebMedia;
import pers.perry.xu.crawler.framework.webcrawler.model.WebPage;
import pers.perry.xu.crawler.framework.webcrawler.parser.WebPageParser;

public class DemoPageParser implements WebPageParser {

	@Override
	public List<String> getSeedUrlsList(WebPage page) {
		List<String> list = new ArrayList<String>();
//		list.add("http://www.mmonly.cc/mmtp/xgmn/[0-9]+.html");
		list.add("http://www.mmonly.cc/ktmh/qbrw/[0-9]+.html");
		return getMatchingList(list, page.getWebBody().toString());
	}

	@Override
	public String getText(WebPage page) {
		// 匹配exp前面的位置 (?=exp)
		// 匹配exp后面的位置 (?<=exp)
//		return "(?<=<img alt=\")[\\u4e00-\\u9fa5]+(?=\"\\s+src)";
		return null;
	}

	@Override
	public List<WebMedia> getMediaDataList(WebPage page) {
		// <img alt="小黄人大眼萌Q版人物萌图欣赏"
		// src="http://t1.hxzdhn.com/uploads/tu/201602/198/ghszy4mkx5v.jpg">
		List<WebMedia> targetList = new ArrayList<WebMedia>();
		String content = page.getWebBody().getElementById("big-pic").toString();

//		String pattern1 = "src=\"(.+?\\.jpg)\"";
//		String pattern2 = "(?<=<img alt=\")[\\u4e00-\\u9fa5]+(?=\"\\s+src)";
//		Pattern picPattern = Pattern.compile(pattern1);
//		Matcher webMatcher = picPattern.matcher(content);
//		while (webMatcher.find()) {
//			String res = webMatcher.group();
//			res = res.replace("src=", "").replace("\"", "");
//			Pattern titlePattern = Pattern.compile(pattern2);
//			Matcher titleMatcher = titlePattern.matcher(bodyElement.getElementById("big-pic").toString());
//			while (titleMatcher.find()) {
//				String alt = titleMatcher.group();
//				targetList.add(new WebMedia(alt, res, MediaType.JPG));
//			}
//		}
		String alt = getStringBetween(content, "<img alt=\"", "\" src=\"");
		String pic = getStringBetween(content, "src=\"", "\"></a>");
		targetList.add(new WebMedia(alt, pic, MediaType.JPG));

		return targetList;
	}

	protected String getStringBetween(String str, String prefix, String suffix) {
		if (!str.contains(prefix) || !str.contains(suffix))
			return null;
		int startPos = str.indexOf(prefix) + prefix.length();
		// abcdabb -> bc ab
		// ->dabb(1) -> 1+2=3
		int endPos = str.substring(startPos).indexOf(suffix) + startPos;
		str = str.substring(startPos, endPos);
		return str;
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

	protected List<String> getReguExpMatching(String str, String strPattern) {
		List<String> resList = new ArrayList<String>();
		Pattern pattern = Pattern.compile(strPattern);
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) { // find any matching
			resList.add(matcher.group());
		}
		return resList;
	}
}
