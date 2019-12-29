package pers.perry.xu.crawler.framework.webcrawler.parser;

import java.util.List;

import pers.perry.xu.crawler.framework.webcrawler.model.WebMedia;
import pers.perry.xu.crawler.framework.webcrawler.model.WebPage;

/**
 * The Only interface you need to implement when using this web crawler
 * framework.
 *
 * You'll need to build your own business logics here, tell the WCP which
 * media/text/seed you want when crawling the web pages.
 *
 * Finally, please set it into configuration before starting crawler:
 * configuration.setParser(new WebPageParserImpl()).
 */
public interface WebPageParser {

	/**
	 * Put your own business logics here and return the Media Data list based on
	 * given WebPage object.
	 *
	 * @param the WebPage page object, contains: webUrl(String),
	 *            webHead(org.jsoup.nodes.Element), webTitle(String)and the
	 *            webBody(org.jsoup.nodes.Element);
	 *
	 * @return the extracted WebMedia list, WebMedia contains: media name (used as
	 *         media file name), mediaUrl(String, the resource URL), mediaType(e.g.
	 *         JPG, PNG, MP3, etc.), pageUrl(String, The web page URL).
	 */
	public List<WebMedia> getMediaDataList(WebPage page);

	/**
	 * Put your own business logics here and return the text string based on given
	 * WebPage object.
	 *
	 * @param the WebPage page object, contains: webUrl(String),
	 *            webHead(org.jsoup.nodes.Element), webTitle(String)and the
	 *            webBody(org.jsoup.nodes.Element);
	 *
	 * @return the extracted text string
	 */
	public String getText(WebPage page);

	/**
	 * Put your own business logics here and return the sub seeds URL list based on
	 * given WebPage object.
	 *
	 * @param the WebPage page object, contains: webUrl(String),
	 *            webHead(org.jsoup.nodes.Element), webTitle(String)and the
	 *            webBody(org.jsoup.nodes.Element);
	 *
	 * @return the URL list to be added as seeds
	 */
	public List<String> getSeedUrlsList(WebPage page);
}
