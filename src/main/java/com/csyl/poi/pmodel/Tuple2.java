package com.csyl.poi.pmodel;

/**
 * @author 霖
 */
public class Tuple2<T, R> {
    public final T a;
    public final R b;

    public Tuple2(T a, R b) {
        this.a = a;
        this.b = b;
    }

    public T getA() {
        return a;
    }

    public R getB() {
        return b;
    }
}
