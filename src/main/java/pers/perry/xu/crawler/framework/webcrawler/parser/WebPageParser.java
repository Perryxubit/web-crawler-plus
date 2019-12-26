package pers.perry.xu.crawler.framework.webcrawler.parser;

import java.util.List;

import pers.perry.xu.crawler.framework.webcrawler.model.WebMedia;
import pers.perry.xu.crawler.framework.webcrawler.model.WebPage;

public interface WebPageParser {

	/**
	 * extract the media data (e.g. picture, voice, video, etc) from the given web
	 * body element
	 */
	public List<WebMedia> getMediaDataList(WebPage page);

	/**
	 * extract the text from the given web body element
	 */
	public String getText(WebPage page);

	/**
	 * get the sub seed url list from the given Html body element
	 */
	public List<String> getSeedUrlsList(WebPage page);
}
