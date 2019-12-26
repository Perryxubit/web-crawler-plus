package pers.perry.xu.crawler.framework.webcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebResource {
	protected String name;
	protected String pageUrl;
}
