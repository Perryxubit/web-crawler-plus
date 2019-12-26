package pers.perry.xu.crawler.framework.webcrawler.parser;

import java.util.List;

import org.jsoup.nodes.Element;

import pers.perry.xu.crawler.framework.webcrawler.model.WebMedia;

public interface WebPageParser {

	/**
	 * extract the media data (e.g. picture, voice, video, etc) from the given web
	 * body element
	 */
	public List<WebMedia> getMediaDataList(Element bodyElement);

	/**
	 * extract the text from the given web body element
	 */
	public String getText(Element bodyElement);

	/**
	 * get the sub seed url list from the given Html body element
	 */
	public List<String> getSeedUrlsList(Element bodyElement);
}
