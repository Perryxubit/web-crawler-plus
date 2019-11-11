package pers.perry.xu.crawler.framework.webcrawler.parser;

public interface PageParser {

	public String visitUrlPattern();

	public String visitPicturePattern();

	public String visitTextPattern();

	public String subUrlSeedPattern();
}
