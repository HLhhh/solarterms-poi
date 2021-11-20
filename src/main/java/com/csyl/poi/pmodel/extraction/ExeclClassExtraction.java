package com.csyl.poi.pmodel.extraction;

import com.csyl.poi.pmodel.AssertUtil;
import com.csyl.poi.pmodel.BaseMatch;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;

import static com.csyl.poi.pmodel.BusinessConstant.EMPTY;
import static com.csyl.poi.pmodel.BusinessConstant.INTEGER_STRING;

/**
 * @author 霖
 */
public class ExeclClassExtraction extends ClassExtraction {

    static {
        InstanceContext.put("x", new ExeclClassExtraction());
    }

    @Override
    <T extends BaseMatch> Collection<T> preadBySheet(TableAdapter.Adapter adapter, Class<T> tClass) throws Exception {
        Sheet sheet = AssertUtil.notNull(adapter.getSheetAt(), "sheet is null");
        return preadBySheet(sheet, tClass);
    }

    public static <T extends BaseMatch> Collection<T> preadBySheet(Sheet sheetAt, Class<T> tClass) throws InstantiationException, IllegalAccessException, IOException {

        Map<String, Field> valueFieldMap = extractHeader(tClass);
        Map<Integer, Field> indexFieldMap = ExeclClassExtraction.extractHaederX(sheetAt.getRow(0).iterator(), ExeclClassExtraction::getValue, valueFieldMap);

        int physicalNumberOfRows = sheetAt.getPhysicalNumberOfRows();
        Collection<T> collection = new ArrayList<>();
        for (int i = 1; i < physicalNumberOfRows; i++) {
            Row row = sheetAt.getRow(i);
            collection.add(ExeclClassExtraction.extractRowY(row.getPhysicalNumberOfCells(), index -> getValue(row.getCell(index)), tClass, indexFieldMap));
        }
        return collection;
    }

    private static String getValue(Cell cell) {
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case _NONE:
                throw new RuntimeException("undefind _NONE");
            case BLANK:
                return EMPTY;
            case ERROR:
                throw new RuntimeException(String.valueOf(cell.getErrorCellValue()));
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case NUMERIC:
                double numericCellValue = cell.getNumericCellValue();
                Matcher matcher = INTEGER_STRING.matcher(String.valueOf(numericCellValue));
                if (matcher.find()) {
                    return matcher.group();
                }
                return EMPTY;
            default:
                throw new RuntimeException("undefind");
        }
    }

}
