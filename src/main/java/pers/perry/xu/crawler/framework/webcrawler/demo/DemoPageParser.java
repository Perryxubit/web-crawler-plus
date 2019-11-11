package pers.perry.xu.crawler.framework.webcrawler.demo;

import pers.perry.xu.crawler.framework.webcrawler.parser.PageParser;

public class DemoPageParser implements PageParser {

	public String visitUrlPattern() {
		return "http://www.mmonly.cc/mmtp/xgmn/[0-9]+.html";
	}

	public String visitPicturePattern() {
//		http://www.mmonly.cc/mmtp/xgmn/
		return "http://www.mmonly.cc/mmtp/xgmn/[0-9]+.html";
	}

	public String visitTextPattern() {
//		return "<a[^>]*>([^<]*)</a>";
		return "http://www.mmonly.cc/mmtp/xgmn/[0-9]+.html";
	}

	public String subUrlSeedPattern() {
		// TODO Auto-generated method stub
		return null;
	}

}
