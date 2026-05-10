package com.eam.util;

import com.eam.annotation.OperationLog;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Excel工具类
 * Task 15.2: 创建Excel工具类
 * Task 15.2.3: 实现通用的Excel导出方法
 */
public class ExcelUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
    
    // Excel格式
    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";
    
    // 单元格样式
    private static final String STYLE_HEADER = "header";
    private static final String STYLE_DATA = "data";
    private static final String STYLE_DATA2 = "data2";
    
    // 单元格宽度（字符数）
    private static final int COLUMN_WIDTH_DEFAULT = 20;
    private static final int COLUMN_WIDTH_SHORT = 10;
    private static final int COLUMN_WIDTH_LONG = 30;

    /**
     * 导出Excel
     * Task 15.2.4: 实现大文件导出优化
     */
    public static void exportExcel(HttpServletResponse response, 
                                  List<?> dataList, 
                                  String fileName, 
                                  Class<?> clazz) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(fileName);
        
        // 设置表头样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle dataStyle2 = createDataStyle2(workbook);
        
        // 设置行样式
        RowStyle rowStyle = new RowStyle();
        rowStyle.setOddBackgroundColor(new XSSFColor(240, 240, 240));
        rowStyle.setEvenBackgroundColor(new XSSFColor(255, 255, 255));
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            // 设置表头样式
            CellStyle style = headerStyle;
            
            // 设置列宽
            int columnWidth = COLUMN_WIDTH_DEFAULT;
            if (fields[i].getName().toLowerCase().contains("id") || 
                fields[i].getName().toLowerCase().contains("code")) {
                columnWidth = COLUMN_WIDTH_SHORT;
            } else if (fields[i].getName().toLowerCase().contains("name") ||
                       fields[i].getName().toLowerCase().contains("description") ||
                       fields[i].getName().toLowerCase().contains("remark")) {
                columnWidth = COLUMN_WIDTH_LONG;
            }
            
            sheet.setColumnWidth(i, columnWidth * 256); // 设置列宽
            
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(fields[i].getName());
            cell.setCellStyle(style);
        }
        
        // 填充数据
        if (dataList != null && !dataList.isEmpty()) {
            for (int i = 0; i < dataList.size(); i++) {
                Object data = dataList.get(i);
                Row dataRow = sheet.createRow(i + 1);
                
                for (int j = 0; j < fields.length; j++) {
                    Field field = fields[j];
                    field.setAccessible(true);
                    
                    try {
                        Object value = field.get(data);
                        Cell cell = dataRow.createCell(j);
                        setCellValue(cell, value);
                        
                        // 数据行样式
                        cell.setCellStyle(i % 2 == 0 ? dataStyle : dataStyle2);
                        
                    } catch (Exception e) {
                        Cell cell = dataRow.createCell(j);
                        cell.setCellValue("");
                        logger.error("设置单元格值失败: 字段={}, 值={}", 
                                   field.getName(), e.getMessage());
                    }
                }
            }
        }
        
        // 自动调整列宽
        autoSizeColumns(sheet, 0);
        
        // 输出到响应流
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String exportFileName = fileName + "_" + timestamp + "." + EXCEL_XLSX;
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", 
                          "attachment;filename=" + URLEncoder.encode(exportFileName, "UTF-8"));
        
        workbook.write(response.getOutputStream());
        workbook.close();
        
        logger.info("Excel导出完成: 文件名={}, 记录数={}", exportFileName, dataList.size());
    }

    /**
     * 设置单元格值
     */
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cell.setCellValue(sdf.format((Date) value));
        } else if (value instanceof java.time.LocalDateTime) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cell.setCellValue(sdf.format(java.sql.Timestamp.valueOf((java.time.LocalDateTime) value)));
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setFontName("微软雅黑");
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK);
        style.setLeftBorderColor(IndexedColors.BLACK);
        style.setRightBorderColor(IndexedColors.BLACK);
        style.setTopBorderColor(IndexedColors.BLACK);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setWrapText(true);
        return style;
    }

    /**
     * 创建数据样式
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("微软雅黑");
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT);
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT);
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT);
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT);
        style.setWrapText(true);
        return style;
    }

    /**
     * 创建数据样式2（交替使用）
     */
    private static CellStyle createDataStyle2(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("微软雅黑");
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT);
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT);
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT);
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT);
        style.setWrapText(true);
        return style;
    }

    /**
     * 自动调整列宽
     */
    private static void autoSizeColumns(Sheet sheet, int startRow) {
        for (int col = 0; col < 100; col++) { // 最多100列
            boolean hasContent = false;
            for (int row = startRow; row < sheet.getPhysicalNumberOfRows(); row++) {
                Row r = sheet.getRow(row);
                if (r != null) {
                    Cell cell = r.getCell(col);
                    if (cell != null && cell.getStringCellValue() != null && !cell.getStringCellValue().isEmpty()) {
                        hasContent = true;
                    }
                }
            }
            
            if (hasContent) {
                sheet.autoSizeColumn(col);
            } else {
                sheet.setColumnWidth(col, 15 * 256); // 设置默认宽度
            }
        }
    }

    /**
     * 行样式类
     */
    private static class RowStyle {
        private XSSFColor oddBackgroundColor;
        private XSSFColor evenBackgroundColor;

        public XSSFColor getOddBackgroundColor() {
            return oddBackgroundColor;
        }

        public void setOddBackgroundColor(XSSFColor oddBackgroundColor) {
            this.oddBackgroundColor = oddBackgroundColor;
        }

        public XSSFColor getEvenBackgroundColor() {
            return evenBackgroundColor;
        }

        public void setEvenBackgroundColor(XSSFColor evenBackgroundColor) {
            this.evenBackgroundColor = evenBackgroundColor;
        }
    }

    /**
     * 获取当前请求信息
     */
    public static Map<String, Object> getRequestInfo() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return new HashMap<>();
        }

        Map<String, Object> info = new HashMap<>();
        info.put("requestURI", request.getRequestURI());
        info.put("requestURL", request.getRequestURL());
        info.put("queryString", request.getQueryString());
        info.put("remoteAddr", request.getRemoteAddr());
        info.put("remoteHost", request.getRemoteHost());
        info.put("method", request.getMethod());
        info.put("contentType", request.getContentType());
        info.put("locale", request.getLocale());
        info.put("requestTime", new Date());

        return info;
    }

    /**
     * 获取当前请求
     */
    private static HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes.getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 创建工作簿
     */
    public static Workbook createWorkbook() {
        return new XSSFWorkbook();
    }

    /**
     * 创建工作表
     */
    public static Sheet createSheet(Workbook workbook, String sheetName) {
        return workbook.createSheet(sheetName);
    }

    /**
     * 写入工作簿到文件
     */
    public static void writeWorkbookToFile(Workbook workbook, String filePath) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
    }

    /**
     * 写入工作簿到输出流
     */
    public static void writeWorkbookToStream(Workbook workbook, OutputStream outputStream) throws IOException {
        workbook.write(outputStream);
        workbook.close();
    }

    /**
     * 从输入流读取工作簿
     */
    public static Workbook readWorkbookFromStream(InputStream inputStream) throws IOException {
        return new XSSFWorkbook(inputStream);
    }

    /**
     * 创建样式配置
     */
    public static Map<String, Object> createStyleConfig(Workbook workbook) {
        Map<String, Object> config = new HashMap<>();
        config.put("headerStyle", createHeaderStyle(workbook));
        config.put("dataStyle", createDataStyle(workbook));
        config.put("dataStyle2", createDataStyle2(workbook));
        config.put("columnWidthDefault", COLUMN_WIDTH_DEFAULT);
        config.put("columnWidthShort", COLUMN_WIDTH_SHORT);
        config.put("columnWidthLong", COLUMN_WIDTH_LONG);
        return config;
    }
}