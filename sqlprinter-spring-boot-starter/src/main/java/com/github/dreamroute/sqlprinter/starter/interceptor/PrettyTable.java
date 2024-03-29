package com.github.dreamroute.sqlprinter.starter.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 控制台打印表格
 */
public class PrettyTable {
    /**
     * 数据
     */
    private final List<List<String>> rows = new ArrayList<>();
    /**
     * 列数量
     */
    private final int columnCount;
    /**
     * 列宽度
     */
    private final int[] columnWidth;

    public PrettyTable(String[] header) {
        columnCount = header.length;
        columnWidth = new int[columnCount];
        Arrays.fill(columnWidth, 0);
        addRow(header);
    }

    public void addRow(String[] row) {
        List<String> list = Arrays.stream(row).map(value -> value != null ? value : "").collect(Collectors.toList());
        rows.add(list);
        for (int i = 0; i < columnWidth.length; i++) {
            columnWidth[i] = Math.max(list.get(i).getBytes().length, columnWidth[i]);
        }
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        //边距
        int margin = 1;
        //总列宽+2*边距数量*列数+列分隔符数量-1
        int lineLength = Arrays.stream(columnWidth).sum() + margin * 2 * columnCount + (columnCount - 1);

        builder.append("|").append(fillChars('=', lineLength)).append("|\n");
        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            for (int j = 0; j < columnCount; j++) {
                String value = j < row.size() ? row.get(j) : "";
                builder.append('|').append(fillChars(' ', margin)).append(value);
                builder.append(fillChars(' ', columnWidth[j] - value.getBytes().length + margin));
            }
            builder.append("|\n");
            if (i == 0) {
                builder.append("|").append(fillChars('=', lineLength)).append("|\n");
            }
            if (i == rows.size() - 1) {
                builder.append("|").append(fillChars('=', lineLength)).append("|\n");
            }
        }
        return builder.toString();
    }

    private String fillChars(char c, int len) {
        char[] chArr = new char[len];
        Arrays.fill(chArr, c);
        return new String(chArr);
    }
}

