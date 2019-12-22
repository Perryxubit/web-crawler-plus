package pers.perry.xu.crawler.framework.webcrawler.parser;

import java.util.List;

import org.jsoup.nodes.Element;

public interface WebPageParser {

	/**
	 * get the pictures list from the given Html body element
	 */
	public List<String> getPicturesUrlsList(Element bodyElement);

	public String visitText(Element bodyElement);

	/**
	 * get the sub seed url list from the given Html body element
	 */
	public List<String> getSeedUrlsList(Element bodyElement);
}
