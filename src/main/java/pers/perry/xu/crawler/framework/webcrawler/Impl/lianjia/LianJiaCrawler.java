package pers.perry.xu.crawler.framework.webcrawler.Impl.lianjia;

import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;
import pers.perry.xu.crawler.framework.webcrawler.utils.DateFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LianJiaCrawler {

    public static String WORKSPACE_DIR = "/Users/puxu/workspace/github/work/";

    private static int PAGE_LIMIT = 60;
    private static String CRAWLER_URL_PREFIX = "https://bj.lianjia.com/ershoufang/pg";
//    private static String CRAWLER_URL_POSTFIX = "l1l2a2a3p4p5rs";
    private static String CRAWLER_URL_POSTFIX = "mw1ie2f1f2f5l1l2l3a2a3p4p5rs";
    private static String CRAWLER_URL_DISTRICT = "%E6%9C%9D%E9%98%B3";

    public CrawlerConfiguration getCrawlerConfig() {
        CrawlerConfiguration configuration = new CrawlerConfiguration();
//        configuration.addSeed("https://bj.lianjia.com/ershoufang/l1l2a2a3p4p5rs%E6%9C%9D%E9%98%B3/");
        getSeedList().forEach((seed) -> {
            configuration.addSeed(seed);
        });

        configuration.setOutputMode(CrawlerConfiguration.DataOutputMode.DoNothing);
        configuration.setMaxThreadNumber(1); // Must be in single thread mode (non-thread safe output logics)
        configuration.setWorkSpace(WORKSPACE_DIR);
        configuration.setEnableCrawlingRecording(false);
        String timestamp = DateFormatter.formatDate(new Date(), "yyyyMMddHHmmss");
        configuration.setParser(new LianJiaParser(timestamp, "北京朝阳房产(满五_电梯_300-500万_50-90平米)"));
        return configuration;
    }

    private List<String> getSeedList() {
        List<String> list = new ArrayList<String>();
        for(int i = 1; i <= PAGE_LIMIT; i++) {
            list.add(CRAWLER_URL_PREFIX + i + CRAWLER_URL_POSTFIX + CRAWLER_URL_DISTRICT);
        }
        return list;
    }
}
