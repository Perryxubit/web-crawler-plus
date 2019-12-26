package pers.perry.xu.crawler.framework.webcrawler.demo;

import java.util.ArrayList;
import java.util.List;

import pers.perry.xu.crawler.framework.webcrawler.model.MediaType;
import pers.perry.xu.crawler.framework.webcrawler.model.WebMedia;
import pers.perry.xu.crawler.framework.webcrawler.model.WebPage;

public class MmonlyPageParser extends DemoPageParser {

	@Override
	public List<String> getSeedUrlsList(WebPage page) {
		List<String> list = new ArrayList<String>();

		// add other pages for the same topic
		String contents = page.getWebBody().getElementsByClass("pages").get(0).toString();
//		String basePicUrl = page.getWebUrl().substring(0, page.getWebUrl().lastIndexOf(".html"));
		String basePicUrl2 = page.getWebUrl().substring(0, page.getWebUrl().lastIndexOf("/") + 1);
		int pageNr = Integer.parseInt(getStringBetween(contents, "<a>共", "页: </a>"));
		List<String> subPageNr = getReguExpMatching(contents, "(?<=<a href=\")[0-9_]+.html(?=\">)");
//		for (int i = 2; i <= pageNr && i >= 2; i++) {
//			list.add(basePicUrl + "_" + i + ".html");
//		}
		for (String str : subPageNr) {
			list.add(basePicUrl2 + str);
		}

		List<String> matchingList = new ArrayList<String>();
		matchingList.add("http://www.mmonly.cc/mmtp/xgmn/[0-9]+.html");
		List<String> res = getMatchingList(matchingList, page.getWebBody().toString());
		list.addAll(res);
		return list;
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
}
