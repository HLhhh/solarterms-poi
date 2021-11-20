package com.csyl.poi.pmodel;

import java.util.regex.Pattern;

/**
 * 业务
 *
 * @author 霖
 */
public class BusinessConstant {
    public static final String DOT = ".";
    public static final String CSV = ".csv";
    public static final String XLS = ".xls";
    public static final String XLSX = ".xlsx";
    public static final String EMPTY = "";
    public static final Pattern INTEGER_STRING = Pattern.compile("^[0-9]*");
}
