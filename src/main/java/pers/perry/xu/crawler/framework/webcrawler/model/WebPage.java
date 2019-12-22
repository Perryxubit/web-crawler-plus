package pers.perry.xu.crawler.framework.webcrawler.model;

import org.jsoup.nodes.Element;

import lombok.Data;

@Data
public class WebPage {
	protected String webUrl;
	protected Element webHead;
	protected String webTitle;
	protected Element webBody;
}
