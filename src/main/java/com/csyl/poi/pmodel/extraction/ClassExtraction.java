package com.csyl.poi.pmodel.extraction;

import com.csyl.poi.pmodel.BaseMatch;
import com.csyl.poi.pmodel.DataMatch;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author 霖
 */
public abstract class ClassExtraction {

    protected static Map<String, ClassExtraction> InstanceContext = new HashMap<>(2);

    static {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        try {
            Class.forName("com.csyl.poi.pmodel.extraction.ExeclClassExtraction",
                    true,
                    systemClassLoader);
            Class.forName("com.csyl.poi.pmodel.extraction.CsvClassExtraction",
                    true,
                    systemClassLoader);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param adapter
     * @param tClass
     * @param <T>
     * @return
     */
    abstract <T extends BaseMatch> Collection<T> preadBySheet(TableAdapter.Adapter adapter, Class<T> tClass) throws Exception;

    public static <T extends BaseMatch> Collection<T> adapter(String fileName, InputStream inputStream, Class<T> tClass) throws Exception {
        TableAdapter.Adapter adapter = TableAdapter.adapter(fileName, inputStream);
        String strategy = adapter.getStrategy();
        return InstanceContext.get(strategy).preadBySheet(adapter, tClass);
    }

    /**
     * 将继承自 {@link BaseMatch} 经过反射获取他们的 {@link Field}
     * <p>与属性上的 {@link DataMatch#value()} 做成map的映射</p>
     *
     * @param aClass {@link BaseMatch} 的子类
     * @return
     */
    public static Map<String, Field> extractHeader(Class<? extends BaseMatch> aClass) {
        Field[] declaredFields = aClass.getDeclaredFields();
        LinkedHashMap<String, Field> map = new LinkedHashMap<>();
        for (Field field : declaredFields) {
            DataMatch annotation = field.getAnnotation(DataMatch.class);
            if (annotation == null) {
                continue;
            }

            String value = annotation.value();
            map.put(value, field);
        }
        return map;
    }


    /**
     * 实例化aclass参数
     *
     * @param rowCount    row一行的总数
     * @param rowFuntion  获取row中的value的动作
     * @param aclass      {@link BaseMatch}的子类
     * @param yAndFileMap readHead x 坐标 与 {@link Field}的映射
     * @param <T>         extends BaseMatch
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseMatch> T extractRowY(Integer rowCount, Function<Integer, String> rowFuntion,
                                                      Class<? extends BaseMatch> aclass,
                                                      Map<Integer, Field> yAndFileMap) throws IllegalAccessException, InstantiationException {
        BaseMatch baseMatch = aclass.newInstance();
        for (int i = 0; i < rowCount; i++) {
            String value = rowFuntion.apply(i);
            Field f = yAndFileMap.get(i);
            if (f == null) continue;
            f.setAccessible(true);
            f.set(baseMatch, value);
        }
        return (T) baseMatch;
    }


    /**
     * 根据表格头 映射出一个 x坐标 与 {@link Field}的映射
     *
     * @param tIterator     表格头
     * @param valueFieldMap {@link DataMatch#value()}与{@link Field} 的影射
     * @return yAndFileMap
     */
    public static <T> Map<Integer, Field> extractHaederX(Iterator<T> tIterator, Function<T, String> action, Map<String, Field> valueFieldMap) {
        Map<Integer, Field> yAndFileMap = new HashMap<>(16);
        AtomicInteger count = new AtomicInteger();
        while (tIterator.hasNext()) {
            T item = tIterator.next();
            Field field = valueFieldMap.get(action.apply(item));
            int incIndex = count.getAndIncrement();
            if (field == null) continue;
            yAndFileMap.put(incIndex, field);
        }

        return Collections.unmodifiableMap(yAndFileMap);
    }


}
