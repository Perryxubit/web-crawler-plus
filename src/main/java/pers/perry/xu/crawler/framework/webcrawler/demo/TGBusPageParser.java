package pers.perry.xu.crawler.framework.webcrawler.demo;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;

import pers.perry.xu.crawler.framework.webcrawler.parser.WebPageParser;

public class TGBusPageParser extends DemoPageParser implements WebPageParser {

	@Override
	public List<String> getPicturesUrlsList(Element bodyElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitText(Element bodyElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getSeedUrlsList(Element bodyElement) {
		String baseStr = bodyElement.getElementById("latestNews").toString();
		List<String> list = new ArrayList<String>();
		list.add("https://www.tgbus.com/news/[0-9]+");
		return getMatchingList(list, baseStr);
	}

}
