# solarterms-poi
apache-poi的适配

### 1.引入Jar包

> 本次的jar只给了jitpack网站托管

+ meavn配置如下

  + 配置镜像

  ```xml
      <repositories>
          <repository>
              <id>jitpack.io</id>
              <url>https://jitpack.io</url>
          </repository>
      </repositories>
  ```

  + 引入Meavn

  ```xml
          <dependency>
              <groupId>com.github.HLhhh</groupId>
              <artifactId>solarterms-poi</artifactId>
              <version>1.0.0</version>
          </dependency>
  ```

  

### 2.使用代码

```java
public static void main(String[] args) throws Exception {

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
```

