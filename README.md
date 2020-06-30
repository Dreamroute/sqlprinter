# sqlprinter SQL打印插件

### MyBatis Simple SQL Print Plugin

![mybatis](http://mybatis.github.io/images/mybatis-logo.png)

### Get Start...
```
<dependency>
    <groupId>com.github.dreamroute</groupId>
    <artifactId>sqlprinter</artifactId>
    <version>latest version</version>
</dependency>
```

----------

## 0.Document: ##
En Doc: 暂无
	
----------

	描述：本插件主要是为了解决在开发调试过程中MyBatis在eclipse/idea控制台输出的sql中参数和sql语句分离的问题。生产环境一般不需要此插件（设置成false即可）。

----------
### 1. 使用方式：传统Spring项目：在mybatis配置文件中加入如下配置，就完成了。 ###
	<plugins>
		<plugin interceptor="com.mook.sqlprinter.interceptor.SqlPrinter"/>
	</plugins>
### 2. Spring Boot项目：
	@Configuration
	public class SqlPrinterConfig {

	    @Value("${sql-show:true}")
	    private String sqlType;

	    /**
	     * SQL打印，将sql中的问号（？）替换成真实值
	     */
	    @Bean
	    public SqlPrinter printer() {
		SqlPrinter printer = new SqlPrinter();
		Properties props = new Properties();
		props.setProperty("type", sqlType);
		printer.setProperties(props);
		return printer;
	    }

	}

----------

### 2. 对插件配置的说明： ###
	
上面对插件的配置默认是log4j和slf4j的debug输出，但是为了比较清晰，可以选择配置红色字体error的打印方式，但是会影响一定的性能。<br>
但是这在开发过程中很有用，推荐此方式。

	<plugins>
		<plugin interceptor="com.mook.sqlprinter.interceptor.SqlPrinter">
			<!-- sql-show不填则默认是true，如果不需要打印sql设置成false -->
			<property name="sql-show" value="true"/>
		</plugin>
	</plugins>

----------

### 3. 效果： ###
> 之前：**insert into xxx (name, password) values (?, ?)**

> 之后：**insert into xxx (name, password) values (tom, 123456)**

----------

### 4.插件说明： ###
	1. 本插件是为了开发过程中方便程序员观察sql的打印情况，特别是参数较多的sql，很直观清晰。<br>
	2. 本插件仅仅是打印sql，插件内部不会破坏mybatis的任何核心，也不会和任何其他插件造成冲突，可以放心使用。

----------

### 5.关于插件： ###
	如果您有什么建议或者意见，欢迎留言，也欢迎pull request，作者会将你优秀的思想加入到插件里面来，为其他人更好的解决问题。

----------
### 6.Demo ###
	1、在数据库中建立表，表名smart_user(可以按照你自己的)；
	2、表的字段为id(int)，name(varchar)，password(varchar)，version(bigint)；
	3、数据库连接信息在mybatis-config.xml文件中修改，改成你自己的数据库信息；
	4、直接运行com.mook.locker.misc.test.mapper下面的各个测试方法，观察控制台输出结果；

----------

### 7.关于作者： ###
	作者QQ：342252328
	作者邮箱：342252328@qq.com
