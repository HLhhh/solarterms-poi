package com.csyl.poi.pmodel.util;

/**
 * @author 霖
 */
public class AssertUtil {

    public static <T> T notNull(T obj, String msg) {
        if (obj == null) {
            throw new CsylException(msg);
        }
        if (obj instanceof String && BusinessConstant.EMPTY.equals(obj)) {
            throw new CsylException(msg);
        }
        return obj;
    }
}
