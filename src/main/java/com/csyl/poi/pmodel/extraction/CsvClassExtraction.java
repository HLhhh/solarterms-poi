package com.csyl.poi.pmodel.extraction;

import com.csvreader.CsvReader;
import com.csyl.poi.pmodel.ClassExtraction;
import com.csyl.poi.pmodel.util.AssertUtil;
import com.csyl.poi.pmodel.BaseMatch;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author 霖
 */
public class CsvClassExtraction extends ClassExtraction {

    static {
        InstanceContext.put("csv", new CsvClassExtraction());
    }

    @Override
    <T extends BaseMatch> Collection<T> preadBySheet(TableAdapter.Adapter adapter, Class<T> tClass) throws Exception {

        String[] heads = AssertUtil.notNull(adapter.getHeads(), "heads is null");
        CsvReader reader = AssertUtil.notNull(adapter.getReader(), "reader is null");

        return CsvClassExtraction.preadBySheet(heads, reader, tClass);
    }

    public static <T extends BaseMatch> Collection<T> preadBySheet(String[] heads, CsvReader reader, Class<T> tClass)
            throws InstantiationException, IllegalAccessException, IOException {

        Map<String, Field> valueFieldMap = extractHeader(tClass);
        Map<Integer, Field> indexFieldMap = extractHaederX(Arrays.stream(heads).iterator(), head -> head, valueFieldMap);

        Collection<T> collection = new ArrayList<>();
        while (reader.readRecord()) {
            int headerCount = reader.getHeaderCount();
            collection.add(extractRowY(headerCount, index -> {
                try {
                    return reader.get(index);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }, tClass, indexFieldMap));
        }
        return collection;
    }
}
