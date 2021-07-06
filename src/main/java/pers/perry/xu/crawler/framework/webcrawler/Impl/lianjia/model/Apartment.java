package pers.perry.xu.crawler.framework.webcrawler.Impl.lianjia.model;

import lombok.Data;

@Data
public class Apartment {
    //https://bj.lianjia.com/ershoufang/101110765908.html
    private String propertyLink = ""; // key

    private String title = "";
    private String locationCommunity = "";
    private String locationAddress = "";
    private Double totalPrice = 0.0; // 万
    private Double unitPrice = 0.0;  // 元/平米

    private String details = "";
    private Integer livingroomNumber = 0;
    private Integer parlourNumber = 0;
    private Double area = 0.0; // 平米
    private String orientations = "";
    private String decoration = "";
    private String floorInfo = "";
    private Integer age = 0; // 年
    private String buildingInfo = "";

    private Integer followedNumber = 0;
    private String releasedTime = "";
    private Status status = Status.None;

    public String getApartmentKey() {
        return this.propertyLink.substring(propertyLink.lastIndexOf("/")+1).replace(".html", "");
    }
}
