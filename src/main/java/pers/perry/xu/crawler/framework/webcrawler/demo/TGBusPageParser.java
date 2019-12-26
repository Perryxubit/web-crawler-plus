package pers.perry.xu.crawler.framework.webcrawler.demo;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;

import pers.perry.xu.crawler.framework.webcrawler.model.WebMedia;
import pers.perry.xu.crawler.framework.webcrawler.parser.WebPageParser;

public class TGBusPageParser extends DemoPageParser implements WebPageParser {

	@Override
	public List<WebMedia> getMediaDataList(Element bodyElement) {
		List<WebMedia> list = new ArrayList<WebMedia>();
//		list.add(new WebMedia("", "", MediaType.Picture));
		return list;
	}

	@Override
	public String getText(Element bodyElement) {
		return null;
	}

	@Override
	public List<String> getSeedUrlsList(Element bodyElement) {
		String baseStr = bodyElement.getElementById("relateNews").toString();
		List<String> list = new ArrayList<String>();
		list.add("https://www.tgbus.com/news/[0-9]+");
		return getMatchingList(list, baseStr);
	}

}
