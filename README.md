# sqlprinter SQL打印插件

### MyBatis Simple SQL Print Plugin

![mybatis](http://mybatis.github.io/images/mybatis-logo.png)

### Get Start...
```
<dependency>
    <groupId>com.github.dreamroute</groupId>
    <artifactId>sqlprinter-spring-boot-starter</artifactId>
    <version>latest version</version>
</dependency>
```

----------
### 最新版本：[点击查看](https://central.sonatype.com/artifact/com.github.dreamroute/sqlprinter-spring-boot-starter)

--------------

> 描述：本插件主要是为了解决输出的sql中参数是问号'?'形式不易观察，而使用真实值替换掉问号'?'。生产环境如果不需要此插件（设置成false即可）。<br>
> 如果应用中使用了mybatis plus，那么打印sql没问题，但是格式化可能不成功，不影响业务，这是由于mybatis plus操蛋的改动了mybatis的插件接口

------------
### 兼容性
如果你项目中使用了类似`mybatis plus`这种框架，优先使用mybatis plus依赖的mybatis，排除本插件的依赖，如下：
```xml
<dependency>
   <groupId>com.github.dreamroute</groupId>
   <artifactId>sqlprinter-spring-boot-starter</artifactId>
   <version>xxx.version</version>
   <exclusions>
       <exclusion>
           <artifactId>mybatis</artifactId>
           <groupId>org.mybatis</groupId>
       </exclusion>
   </exclusions>
</dependency>
```

----------
### 使用方式，Spring Boot项目：
1. 版本2.0.0之后：在启动类上使用@EnableSQLPrinter即可开起（如果生产环境不希望显示sql，在application.yml/properties中配置`sqlprinter.show=false`即可）
2. 是否格式化SQL，默认不格式化，可以配置(`sqlprinter.format = true`)来格式化SQL，对于一些比较特殊的SQL，如果格式化失败，那么会打印未被格式化时的sql，这时候会打印错误日志，
   但是不会对业务造成任何影响，如果在乎错误日志，觉得错误日志不好看，那么可以关闭格式化功能
   >    格式化的好处：1. 系统打印的SQL很整齐，风格一致；2. 由于mybatis使用动态标签，如果不格式化，那么打印的SQL会移除不满足条件的动态标签，显得SQL很凌乱
3. 过滤功能：对于有一些sql打印比较频繁，不希望展示在日志中，那么可以在application.yml/properties中配置中配置sqlprinter.filter数组（数组内容就是Mapper接口的方法名），如下：
    ```
    sqlprinter:
      show: true
      filter:
        - com.github.dreamroute.sqlprinter.boot.mapper.UserMapper.selectById
        - com.github.dreamroute.sqlprinter.boot.mapper.UserMapper.selectAll
    ```
   那么selectById和selectAll方法就不会打印sql了。
4. 对于查询sql，显示查询结果，整条效果如下：
```
==> com.github.dreamroute.sqlprinter.boot.mapper.UserMapper.selectUserByIds
SELECT *
FROM smart_user
WHERE id IN (1, 2)
```
5. 配置项：
```
sqlprinter.show-result = true/false（是否显示查询结果，默认true）
```
```
|==========================================================|
| id | name       | password | version | birthday | gender |
|==========================================================|
| 1  | w.dehai    | null     | 0       | null     | null   |
| 2  | Dreamroute | null     | 0       | null     | null   |
|==========================================================|
```

----------

### 2. 效果： ###
> 之前：**insert into xxx (name, password) values (?, ?)**

> 之后：**insert into xxx (name, password) values ('tom', '123456')**

----------

### 2.1. 自定义显示内容
1. 在插件打印SQL的时候，对于有些特殊数据类型，可能插件默认打印方式不符合你的要求，比如日期类型Date默认打印的就是Date类型
调用`toString`方法的结果，类似这样`Tue Sep 07 16:25:28 CST 2021`，而你需要的是`2021-09-07 16:25:028.673`，如果从控制台或者日志文件中直接复制带有这种`Tue Sep 07 16:25:28 CST 2021`时间的sql去数据库执行，很有可能会报错，
这时你就可以自定义日期类型的打印格式，打印成`2021-09-07 16:25:028.673`这种易读并且可以直接用于执行的格式。
2. 对于`Date`参数，希望打印`yyyy-MM-dd HH:mm:ss.SSS`类型的日期
3. 创建日期转换器类，实现`ValueConverter`接口：
```java
public class DateConverter implements ValueConverter {
    @Override
    public Object convert(Object value) {
        if (value instanceof Date) {
            value = DateUtil.format((Date) value, "yyyy-MM-dd HH:mm:ss");
        }
        return value;
    }
}
```
4. 在`@EnableSQLPrinter`的属性`converters`中加入即可，比如`@EnableSQLPrinter(converters = {DateConverter.class, EnumConverter.class})`
5. 此时你的属性为`Date`的字段打印的就是`2021-09-07 16:25:028.673`这种格式的了
6. 框架已经内置提供了两个现成的转换工具，你可以直接使用，分别是`日期`和`枚举`值转换工具，如果满足你的需求就用，不满足就自定义，在def包下：
   1. com.github.dreamroute.sqlprinter.starter.converter.def.DateConverter
   2. com.github.dreamroute.sqlprinter.starter.converter.def.EnumConverter

### 3.插件说明： ###
	1. 本插件是为了方便程序员观察真实sql的打印情况(问号'?'已经被真实值替换)，特别是参数较多的sql，很直观清晰，可以直接复制sql在数据库中执行，非常友好。<br>
	2. 本插件仅仅是打印sql，插件内部不会破坏mybatis的任何核心，也不会和任何其他插件造成冲突，可以放心使用。

----------

### 4.关于插件： ###
	如果您有什么建议或者意见，欢迎留言，也欢迎pull request，作者会将你优秀的思想加入到插件里面来，为其他人更好的解决问题。

----------
### 5.Demo ###
	本项目可以直接pull到本地执行单元测试观察效果

----------

### 7.关于作者： ###
	作者QQ：342252328
	作者邮箱：342252328@qq.com
