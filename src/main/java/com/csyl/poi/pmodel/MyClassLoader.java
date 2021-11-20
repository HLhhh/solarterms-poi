package com.csyl.poi.pmodel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

public class MyClassLoader extends ClassLoader {

    public void findClass(Class aClass) {
        Field[] fields = aClass.getFields();

    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 加载D盘根目录下指定类名的class
        String clzDir = "D:\\" + File.separatorChar
                + name.replace('.', File.separatorChar) + ".class";
        byte[] classData = getClassData(clzDir);

        if (classData == null) {
            throw new ClassNotFoundException();
        } else {
            return defineClass(name, classData, 0, classData.length);
        }
    }

    private byte[] getClassData(String path) {
        try (InputStream ins = new FileInputStream(path);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {

            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            int bytesNumRead = 0;
            while ((bytesNumRead = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesNumRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
