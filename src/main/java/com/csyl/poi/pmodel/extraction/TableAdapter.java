package com.csyl.poi.pmodel.extraction;

import com.csvreader.CsvReader;
import com.csyl.poi.pmodel.BusinessConstant;
import com.csyl.poi.pmodel.CsylException;
import com.csyl.poi.pmodel.Tuple2;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Objects;

/**
 * @author 霖
 */
public class TableAdapter {

    public enum FileSuffixEnum {
        /**
         * CSV
         */
        CSV("csv"),

        /**
         * XLS
         */
        XLS("xls"),

        /**
         * XLSX
         */
        XLSX("xlsx");

        private final String name;

        FileSuffixEnum(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static Adapter adapter(String fileName, InputStream inputStream) throws Exception {
        Adapter adapter = new Adapter();
        switch (fileSuffix(fileName)) {
            case CSV:
                Tuple2<String[], CsvReader> csvReaderTuple2 = toMathReaderObj(inputStream, new char[]{'\t', ',', '|'});
                adapter.setHeads(csvReaderTuple2.a);
                adapter.setReader(csvReaderTuple2.b);
                adapter.setStrategy("csv");
                return adapter;
            case XLS:
                try (HSSFWorkbook workbook = new HSSFWorkbook(Objects.requireNonNull(inputStream))) {
                    adapter.setSheetAt(workbook.getSheetAt(0));
                    adapter.setStrategy("x");
                    return adapter;
                }
            case XLSX:
                try (XSSFWorkbook workbook = new XSSFWorkbook(Objects.requireNonNull(inputStream))) {
                    adapter.setSheetAt(workbook.getSheetAt(0));
                    adapter.setStrategy("x");
                    return adapter;
                }
            default:
                throw new CsylException("暂不支持的格式");
        }
    }

    /**
     * 获取文件名的后缀
     *
     * @param fileName 文件名
     * @return 文件后缀枚举
     */
    private static FileSuffixEnum fileSuffix(String fileName) {
        String suffixName = fileName
                .substring(fileName.lastIndexOf(BusinessConstant.DOT))
                .toLowerCase(Locale.ROOT);

        if (BusinessConstant.CSV.equals(suffixName)) {
            return FileSuffixEnum.CSV;
        }
        if (BusinessConstant.XLSX.equals(suffixName)) {
            return FileSuffixEnum.XLSX;
        }
        if (BusinessConstant.XLS.equals(suffixName)) {
            return FileSuffixEnum.XLS;
        }

        throw new CsylException("文件名错误");
    }

    public static class Adapter {
        private String[] heads;
        private CsvReader reader;
        private Sheet sheetAt;
        private String strategy;

        public String[] getHeads() {
            return heads;
        }

        public void setHeads(String[] heads) {
            this.heads = heads;
        }

        public CsvReader getReader() {
            return reader;
        }

        public void setReader(CsvReader reader) {
            this.reader = reader;
        }

        public Sheet getSheetAt() {
            return sheetAt;
        }

        public void setSheetAt(Sheet sheetAt) {
            this.sheetAt = sheetAt;
        }

        public String getStrategy() {
            return strategy;
        }

        public void setStrategy(String strategy) {
            this.strategy = strategy;
        }
    }

    public static Tuple2<String[], CsvReader> toMathReaderObj(InputStream inputStream, char[] cs) throws Exception {
        ByteArrayOutputStream bos = getBOS(inputStream);
        ByteArrayInputStream in = new ByteArrayInputStream(bos.toByteArray());


        int p = (in.read() << 8) + in.read();

        String code;
        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }

        for (char c : cs) {
            in.reset();
            CsvReader csvReader = new CsvReader(new InputStreamReader(in, Charset.forName(code)), c);

            //验证一次是否有值
            csvReader.readHeaders();
            String[] headers = csvReader.getHeaders();
            if (headers.length > 1) {
                return new Tuple2<>(headers, csvReader);
            }
            csvReader.close();
        }

        throw new RuntimeException("解析失败");
    }

    /**
     * 也就是说，指向内存的流可以不用关闭，指向存储卡/硬盘的流一定要关闭。
     *
     * @param in
     * @return
     */
    private static ByteArrayOutputStream getBOS(InputStream in) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            BufferedInputStream br = new BufferedInputStream(in);
            for (int c = 0; (c = br.read()) != -1; ) {
                bos.write(c);
            }
            br.close();
        } catch (Exception ignored) {
        }
        return bos;
    }
}
