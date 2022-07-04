package com.github.dreamroute.sqlprinter.starter.anno;

/**
 * 类型转换器
 *
 * @author w.dehai.2021/9/7.15:27
 */
public interface ValueConverter {

    /**
     * 将参数类型的值转成你希望在sql中显示的值，比如value是Date类型，而在sql中希望展示成yyyy-MM-dd HH:mm:ss类型，就在convert方法中实现此逻辑，大概是这样：
     * <pre>
     * public class DateConverter implements ValueConverter {
     *     &#64;Override
     *     public Object convert(Object value) {
     *         if (value instanceof Date) {
     *             value = DateUtil.format((Date) value, "yyyy-MM-dd HH:mm:sss.SSS");
     *         }
     *         return value;
     *     }
     * }
     * </pre>
     *
     * @param value 参数值
     * @return 返回显示值
     */
    Object convert(Object value);

}
