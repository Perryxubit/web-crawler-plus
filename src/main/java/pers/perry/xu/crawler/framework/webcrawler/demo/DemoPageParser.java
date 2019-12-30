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
		// add other pages for the same topic
		String contents = page.getWebBody().getElementsByClass("pages").get(0).toString();
		String basePicUrl = page.getWebUrl().substring(0, page.getWebUrl().lastIndexOf("/") + 1);
		List<String> subPageNr = getReguExpMatching(contents, "(?<=<a href=\")[0-9_]+.html(?=\">)");
		for (String str : subPageNr) {
			list.add(basePicUrl + str);
		}
		// add other topics
		List<String> matchingList = new ArrayList<String>();
		matchingList.add("http://www.mmonly.cc/ktmh/dmmn/[0-9]+.html");
		List<String> res = getMatchingList(matchingList, page.getWebBody().toString());
		list.addAll(res);
		return list;
	}

	@Override
	public String getText(WebPage page) {
		return null;
	}

	@Override
	public List<WebMedia> getMediaDataList(WebPage page) {
		List<WebMedia> targetList = new ArrayList<WebMedia>();
		// add current pic
		String content = page.getWebBody().getElementById("big-pic").toString();
		String alt = getStringBetween(content, "<img alt=\"", "\" src=");
		String pic = getStringBetween(content, "src=\"", "\"></a>");
		String suffix = pic.substring(pic.lastIndexOf("/"), pic.lastIndexOf("."));
		targetList.add(new WebMedia(alt + suffix, pic, MediaType.JPG));
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
