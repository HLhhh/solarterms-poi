package com.csyl.poi.pmodel;

import com.csyl.poi.pmodel.extraction.ClassExtraction;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * @author 霖
 */
public class Application {

    public static void main(String[] args) throws Exception {

        Class.forName("com.csyl.poi.pmodel.extraction.ExeclClassExtraction",
                true,
                systemClassLoader);
        Class.forName("com.csyl.poi.pmodel.extraction.CsvClassExtraction",
                true,
                systemClassLoader);

        String fileName = "test.csv";
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

        InputStream resourceAsStream = systemClassLoader.getResourceAsStream(new String(fileName.getBytes(StandardCharsets.UTF_8)));

        Collection<Test> adapter = ClassExtraction.adapter(fileName, resourceAsStream, Test.class);
        for (Test test : adapter) {
            System.out.println(test.a + test.b);
        }
    }

    public static class Test extends BaseMatch {
        @DataMatch("名称1")
        public String a;
        @DataMatch("名称2")
        public String b;
    }
}
