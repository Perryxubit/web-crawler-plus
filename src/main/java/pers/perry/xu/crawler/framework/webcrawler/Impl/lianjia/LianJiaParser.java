package pers.perry.xu.crawler.framework.webcrawler.Impl.lianjia;

import lombok.extern.log4j.Log4j;
import org.jsoup.nodes.Element;
import pers.perry.xu.crawler.framework.webcrawler.Impl.lianjia.model.Apartment;
import pers.perry.xu.crawler.framework.webcrawler.Impl.lianjia.model.Status;
import pers.perry.xu.crawler.framework.webcrawler.demo.DemoPageParser;
import pers.perry.xu.crawler.framework.webcrawler.model.WebMedia;
import pers.perry.xu.crawler.framework.webcrawler.model.WebPage;
import pers.perry.xu.crawler.framework.webcrawler.parser.WebPageParser;
import pers.perry.xu.crawler.framework.webcrawler.utils.DateFormatter;
import pers.perry.xu.crawler.framework.webcrawler.utils.Logging;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j
public class LianJiaParser extends DemoPageParser implements WebPageParser {

    private String sheetToCreate;
    private String excelFileName;
    private Set<String> duplicateKeyChecker;

    LianJiaParser(String sheetToCreate, String excelFileName) {
        this.sheetToCreate = sheetToCreate;
        this.excelFileName = excelFileName;
        this.duplicateKeyChecker = new HashSet<>();
    }

    @Override
    public List<WebMedia> getMediaDataList(WebPage page) { return null; }

    @Override
    public List<String> getSeedUrlsList(WebPage page) {
        return null;
    }

    @Override
    public String getText(WebPage page) {
        StringBuilder str = new StringBuilder(200);
        List<Apartment> apartmentList = new ArrayList<>();
        AtomicInteger idx = new AtomicInteger();
        page.getWebBody().getElementsByClass("info clear").forEach(element -> {
            Apartment apartment = getApartmentFromElement(element);
//            System.out.println("@@index: " + idx + " title: " + apartment.getTitle());
            idx.getAndIncrement();
            apartmentList.add(apartment);
            str.append(apartment.toString());
        });

//        System.out.println("@@ Page:" + page.getWebUrl());
//        apartmentList.forEach(apartment -> System.out.println("@@ KEY:" + apartment.getApartmentKey()));

        log.info(Logging.format("[LianJia] Running crawler for sheet name:  {}", sheetToCreate));
        //北京朝阳房产 (满五唯一/有电梯/300_500万/50_90平米)
        CrawlerUtils.appendData2Excel(apartmentList, this.sheetToCreate, this.excelFileName, duplicateKeyChecker);
        return str.toString();
    }



    private Apartment getApartmentFromElement(Element element) {
        String title = element.getElementsByClass("title").text();
        String address = element.getElementsByClass("flood").text();
        String info = element.getElementsByClass("address").get(0).getElementsByClass("houseInfo").text();
        String followInfo = element.getElementsByClass("followInfo").text();
        Element tagElement = element.getElementsByClass("tag") == null ? null : element.getElementsByClass("tag").get(0);

        // property link
        // <a class="" href="https://bj.lianjia.com/ershoufang/101109114090.html" target="_blank" data-log_index="1" data-el="ershoufang" data-housecode="101109114090" data-is_focus="" data-sl="">南北通透两居室，格局方正，采光好</a>
        Element titleElement = element.getElementsByClass("title").get(0);
        String key  = titleElement.child(0).attr("href");
        Apartment apartment = new Apartment();
        apartment.setPropertyLink(key);

        //北京新天地三期 1室1厅 北
        apartment.setTitle(
                title.replace("+", " ")
                        .replaceAll( "\\s+", " ")
                        .replace(" ", ", ")
                        .replace("，", ", ")
        );

        //三源里街 - 亮马桥
        apartment.setLocationCommunity(address.substring(0, address.indexOf("-")).trim());
        apartment.setLocationAddress(address.substring(address.indexOf("-") + 1).trim());

        //1室1厅 | 61.84平米 | 北 | 精装 | 顶层(共28层) | 2009年建 | 板塔结合
        // 0     | 1        | 2  | 3   | 4           | 5        | 6
        apartment.setDetails(info);

        String[] infoList = info.split("\\|");
        String roomInfo = infoList[0].trim();
        int idx = 0;
        if(roomInfo.contains("室")) {
            idx = roomInfo.indexOf("室");
            apartment.setLivingroomNumber(Integer.parseInt(roomInfo.substring(0, idx)));
            idx += 1;
        }
        if(roomInfo.contains("房间")) {
            idx = roomInfo.indexOf("房间");
            apartment.setLivingroomNumber(Integer.parseInt(roomInfo.substring(0, idx)));
            idx += 2;
        }
        if( idx > 0 && roomInfo.contains("厅")) {
            apartment.setParlourNumber(Integer.parseInt(roomInfo.substring(roomInfo.indexOf("室") + 1, roomInfo.indexOf("厅"))));
        } else {
            apartment.setParlourNumber(0);
        }

        apartment.setArea(Double.parseDouble(infoList[1].trim().replace("平米", "")));
        apartment.setOrientations(infoList[2].trim());
        apartment.setDecoration(infoList[3].trim());
        apartment.setFloorInfo(infoList[4].trim());
        String age = infoList[5].trim();
        if(age.contains("年")) {
            apartment.setAge(Integer.parseInt(age.substring(0, age.indexOf("年"))));
        }

        //16人关注 / 5天以前发布
        apartment.setFollowedNumber(Integer.parseInt(followInfo.substring(0, followInfo.indexOf("人")).trim()));
        apartment.setReleasedTime(followInfo.substring(followInfo.indexOf("/") + 1).trim());

        //358万
        String totalPrice = element.getElementsByClass("priceInfo").get(0).getElementsByClass("totalPrice").text();
        apartment.setTotalPrice(Double.parseDouble(totalPrice.replace("万","")));
        //单价57892元/平米
        String unitPrice = element.getElementsByClass("priceInfo").get(0).getElementsByClass("unitPrice").text();
        apartment.setUnitPrice(Double.parseDouble(unitPrice.replace("单价","").replace("元/平米","")));

        Status statusProperty = Status.None;
        if(tagElement.getElementsByClass("taxfree") != null) {
            statusProperty = Status.Year5;
        } else if (tagElement.getElementsByClass("five") != null) {
            statusProperty = Status.Year2;
        }
        apartment.setStatus(statusProperty);

//        System.out.println(apartment.toString());
        return apartment;
    }
}
