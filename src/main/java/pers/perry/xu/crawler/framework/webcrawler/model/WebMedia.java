package pers.perry.xu.crawler.framework.webcrawler.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class WebMedia extends WebResource {

	protected String mediaUrl;
	private MediaType mediaType;

	public WebMedia(String name, String mediaUrl, MediaType type) {
		super(name, null);
		this.mediaUrl = mediaUrl;
		this.mediaType = type;
	}

	public WebMedia(String name, String pageUrl, String mediaUrl, MediaType type) {
		super(name, pageUrl);
		this.mediaUrl = mediaUrl;
		this.mediaType = type;
	}

}
