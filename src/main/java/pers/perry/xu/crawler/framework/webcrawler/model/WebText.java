package pers.perry.xu.crawler.framework.webcrawler.model;

import lombok.ToString;

@ToString
public class WebText extends WebResource {

	private static int MAX_WEB_RESOURCE_TEXT_LENGTH = 100000;
	String content;

	WebText(String name, String content) {
		super(name, null);
		if (content.length() >= MAX_WEB_RESOURCE_TEXT_LENGTH) {
			this.content = content.substring(0, MAX_WEB_RESOURCE_TEXT_LENGTH - 3) + "...";
		} else {
			this.content = content;
		}
	}

	WebText(String name, String pageUrl, String content) {
		super(name, pageUrl);
		if (content.length() >= MAX_WEB_RESOURCE_TEXT_LENGTH) {
			this.content = content.substring(0, MAX_WEB_RESOURCE_TEXT_LENGTH - 3) + "...";
		} else {
			this.content = content;
		}
	}
}
