package pers.perry.xu.crawler.framework.webcrawler.model;

import org.jsoup.nodes.Element;

public class WebPage {
	protected String webUrl;
	protected Element webHead;
	protected String webTitle;
	protected Element webBody;

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public Element getWebHead() {
		return webHead;
	}

	public void setWebHead(Element webHead) {
		this.webHead = webHead;
	}

	public String getWebTitle() {
		return webTitle;
	}

	public void setWebTitle(String webTitle) {
		this.webTitle = webTitle;
	}

	public Element getWebBody() {
		return webBody;
	}

	public void setWebBody(Element webBody) {
		this.webBody = webBody;
	}

}
