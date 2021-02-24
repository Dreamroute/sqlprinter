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
### 最新版本：[点击查看](https://search.maven.org/artifact/com.github.dreamroute/sqlprinter-spring-boot-starter)

--------------

> 描述：本插件主要是为了解决在开发调试过程中MyBatis在eclipse/idea控制台输出的sql中参数和sql语句分离的问题。生产环境一般不需要此插件（设置成false即可）。

----------
### 1. Spring Boot项目：
1. 版本2.0.0之后：在启动类上使用@EnableSQLPrinter即可开起（如果生产环境不希望显示sql，在application.yml/properties中配置sqlprinter.sql-show=false即可）

2. 过滤功能：对于有一些sql打印比较频繁，不希望展示在日志中，那么可以在application.yml/properties中配置中配置sqlprinter.filter数组（数组内容就是Mapper接口的方法名），如下：
    ```
    sqlprinter:
      sql-show: true
      filter:
        - com.github.dreamroute.sqlprinter.boot.mapper.UserMapper.selectById
        - com.github.dreamroute.sqlprinter.boot.mapper.UserMapper.selectAll
    ```
   那么selectById和selectById方法就不会打印sql了。

### 2. 使用方式：传统Spring MVC项目：在mybatis配置文件中加入如下配置，就完成了，生产环境不希望显示，在插件中增加属性sql-show=false即可。 ###
	<plugins>
		<plugin interceptor="com.github.dreamroute.sqlprinter.starter.interceptor.SqlPrinter">
		    <!-- 如果不希望现实，那么就加上下方的配置 -->
		    <property name="sql-show" value="false"/>
	    </plugin>
	</plugins>
----------

### 2. 效果： ###
> 之前：**insert into xxx (name, password) values (?, ?)**

> 之后：**insert into xxx (name, password) values ('tom', 123456)**

----------

### 3.插件说明： ###
	1. 本插件是为了开发过程中方便程序员观察sql的打印情况，特别是参数较多的sql，很直观清晰，可以直接复制sql在数据库中执行，非常友好。<br>
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
