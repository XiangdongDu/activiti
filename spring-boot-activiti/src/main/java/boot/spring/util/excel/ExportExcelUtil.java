package boot.spring.util.excel;

import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * @Author duxiangdong
 * @Date 2022/1/25 19:52
 * @Version 1.0
 * excel导出
 */
public class ExportExcelUtil<T> {
    private static final Logger logger = LoggerFactory.getLogger(ExportExcelUtil.class);

    public ByteArrayOutputStream exportExcel2007(List<String> list) {
        //声明一个工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        for (int k = 0; k < list.size(); k++) {

            //TODO 业务

            //生成一个表格
            XSSFSheet sheet = workbook.createSheet();
            //设置sheet名称
            workbook.setSheetName(k, "ddd" + list.get(k));
            //设置表格默认列宽20个字节
            sheet.setDefaultColumnWidth(20);
            //生成一个样式
            XSSFCellStyle style = workbook.createCellStyle();
            //设置这些样式
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            //生成一个字体
            XSSFFont font = workbook.createFont();
            font.setFontName("宋体");
            font.setColor(new XSSFColor(Color.BLACK));

            font.setFontHeightInPoints((short) 11);
            font.setBold(true);//是否加粗
            //把字体应用到当前到样式
            style.setFont(font);
            //生成并设置另一个样式
            XSSFCellStyle style2 = workbook.createCellStyle();
            //设置这些样式
            style2.setAlignment(HorizontalAlignment.CENTER);
            style2.setVerticalAlignment(VerticalAlignment.CENTER);
            //生成一个字体
            XSSFFont font2 = workbook.createFont();
            //把字体应用到当前到样式
            style2.setFont(font2);

            //产生表格标题行
            XSSFRow row = sheet.createRow(0);
            XSSFCell cellHeader;

            for (int i = 0; i < list.size(); i++) {
                cellHeader = row.createCell(i);
                cellHeader.setCellStyle(style);
                cellHeader.setCellValue(new XSSFRichTextString(list.get(i)));

            }
            int index = 0;
            XSSFCell cell;
            for (int i = 0; i < list.size(); i++) {
                index++;
                row = sheet.createRow(index);
                for (int j = 0; j < list.size(); j++) {
                    cell = row.createCell(j);
                    cell.setCellStyle(style2);
                    String cellValue = list.get(j);
                    cell.setCellValue(cellValue);
                }
            }
        }


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } catch (IOException e) {
            logger.error("邮件发送失败{[]}" + e);
            e.printStackTrace();
        }
        return bos;
    }


}
