package pers.perry.xu.crawler.framework.webcrawler.Impl.lianjia;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import pers.perry.xu.crawler.framework.webcrawler.Impl.lianjia.model.Apartment;
import pers.perry.xu.crawler.framework.webcrawler.utils.Logging;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Log4j
public class CrawlerUtils {

    public static boolean ignoringDuplicateKeys = false;

    private static String OUTPUT_DIR_PATH = "/properties/";
    private static String OUTPUT_FILE_PATH = LianJiaCrawler.WORKSPACE_DIR + OUTPUT_DIR_PATH;

    static {
        try {
            if(!Files.exists(Paths.get(OUTPUT_FILE_PATH))) {
                Files.createDirectories(Paths.get(OUTPUT_FILE_PATH));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportData2txt(List<Apartment> resultContent) {

        try {
            for (Apartment apartment : resultContent) {
                String outputFilePath = OUTPUT_FILE_PATH + apartment.getApartmentKey();

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {
                    bw.write(apartment.toString());
                    bw.newLine();
                    bw.flush();
                    log.info(Logging.format("[LianJia] Exported to txt file:  {}", outputFilePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createHeaderRow(HSSFSheet sheet) {
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("房产编号");
        header.createCell(1).setCellValue("标题");
        header.createCell(2).setCellValue("小区");
        header.createCell(3).setCellValue("地址");
        header.createCell(4).setCellValue("总价(万)");
        header.createCell(5).setCellValue("单位价格(万)");

        header.createCell(6).setCellValue("户型");
        header.createCell(7).setCellValue("面积");
        header.createCell(8).setCellValue("朝向");
        header.createCell(9).setCellValue("装修");
        header.createCell(10).setCellValue("楼层");
        header.createCell(11).setCellValue("年份");

        header.createCell(12).setCellValue("关注人数");
        header.createCell(13).setCellValue("发布时间");
        header.createCell(14).setCellValue("状态");
        header.createCell(15).setCellValue("链接");

        // change size
        int width = (int) (sheet.getColumnWidth(0) * 1.6);
        for (int i = 0; i <= 15; i++) {
            sheet.setColumnWidth(i, width);
        }
//        sheet.setColumnWidth(15, width*2);
    }

    private static HSSFWorkbook readSheet(String sheetName, String inputFilePath) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(inputFilePath));
        return workbook;
    }

    public static void appendData2Excel(List<Apartment> resultContent, String sheetName, String fileName, Set<String> duplicateKeyChecker) {
        try {
            String outputFilePath = OUTPUT_FILE_PATH + fileName + ".xls";

            HSSFWorkbook resultBook = null;
            if(!Files.exists(Paths.get(outputFilePath))) {
                resultBook = new HSSFWorkbook();
            } else {
                resultBook = readSheet(sheetName, outputFilePath);
            }

            // sheet name -> sheet data list
            Map<String, List<Apartment>> resultMap = new HashMap<String, List<Apartment>>();
            resultMap.put(sheetName, resultContent);

            // build each sheet
            int rowBaseIdx = 0;
            Iterator iter = resultMap.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next().toString();
                HSSFSheet sheet = resultBook.getSheet(key);
                if(sheet == null) {
                    sheet = resultBook.createSheet(key);
                    createHeaderRow(sheet);
                    rowBaseIdx = 1;
                    log.info(Logging.format("[LianJia] Add new sheet: {}", sheetName));
                } else {
                    rowBaseIdx = sheet.getPhysicalNumberOfRows();
                    log.info(Logging.format("[LianJia] Append existing sheet: {}", sheetName));
                }
                log.info(Logging.format("[LianJia] Append row baseline: {}", rowBaseIdx));

                List<Apartment> apartmentList = resultMap.get(key);
                for (int i = 0; i < apartmentList.size(); i++) {
                    Apartment apartment = apartmentList.get(i);

                    if(duplicateKeyChecker.contains(apartment.getApartmentKey())) {
                        log.warn(Logging.format("[LianJia] Warning: Ignored duplicate apartment key: {}", apartment.getApartmentKey()));
                        if(ignoringDuplicateKeys) {
                            continue;
                        }
                    }

                    int rowNum = rowBaseIdx + i;
                    HSSFRow row = sheet.createRow(rowNum);
                    row.createCell(0).setCellValue(apartment.getApartmentKey());
                    row.createCell(1).setCellValue(apartment.getTitle());
                    row.createCell(2).setCellValue(apartment.getLocationCommunity());
                    row.createCell(3).setCellValue(apartment.getLocationAddress());
                    row.createCell(4).setCellValue(apartment.getTotalPrice());
                    row.createCell(5).setCellValue(apartment.getUnitPrice());

                    row.createCell(6).setCellValue(apartment.getLivingroomNumber());
                    row.createCell(7).setCellValue(apartment.getArea());
                    row.createCell(8).setCellValue(apartment.getOrientations());
                    row.createCell(9).setCellValue(apartment.getDecoration());
                    row.createCell(10).setCellValue(apartment.getFloorInfo());
                    row.createCell(11).setCellValue(apartment.getAge());

                    row.createCell(12).setCellValue(apartment.getFollowedNumber());
                    row.createCell(13).setCellValue(apartment.getReleasedTime());
                    row.createCell(14).setCellValue(apartment.getStatus().toString());
                    row.createCell(15).setCellValue(apartment.getPropertyLink());

                    duplicateKeyChecker.add(apartment.getApartmentKey());
                }
            }

            try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
                resultBook.write(out);
            } catch (IOException e) {
                e.printStackTrace();
            }

            log.info(Logging.format("[LianJia] Exported to xls file: {}", outputFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
