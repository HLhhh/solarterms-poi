package com.csyl.poi.pmodel;


import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ScannSheet {

    public static Map<String, Class<? extends BaseMatch>> getStringClassMap(String packageName) throws ClassNotFoundException, URISyntaxException {
        Map<String, Class<? extends BaseMatch>> classMap = new HashMap<>(2);

        String packageDirName = packageName
                .replace('.', '/');

        URL resource = Application.class.getClassLoader().getResource(packageDirName);
        File file = new File(resource.toURI());
        if (file.isDirectory()) {
            File[] tempList = file.listFiles();
            for (File itemF : tempList) {
                String className = itemF.getName();
                if (!className.contains("$")) {
                    className = className.substring(0, className.length() - 6);
                    // 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className);
                    DataMatch annotation = aClass.getAnnotation(DataMatch.class);
                    if (annotation != null && aClass.getSuperclass().equals(BaseMatch.class)) {
                        classMap.put(annotation.value(), (Class<? extends BaseMatch>) aClass);
                    }
                }
            }
        }
        return classMap;
    }
}
