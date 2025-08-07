package com.chris.vo.resultVOs;

import lombok.Data;

import java.util.List;
import java.util.Collections;
import java.util.function.Function;

@Data
public class ScrollResult<T, V extends Comparable<? super V>> {
    private V lastValue;
    private Long lastId;
    private List<T> rows;

    private ScrollResult(List<T> rows, V lastValue, Long lastId) {
        this.rows     = rows;
        this.lastValue= lastValue;
        this.lastId   = lastId;
    }

    /**
     * 静态工厂：接收 rows + 两个提取函数
     *
     * @param rows            本页数据
     * @param valueExtractor  如何从一条记录拿到排序字段值
     * @param idExtractor     如何从一条记录拿到唯一 ID
     */
    public static <T, V extends Comparable<? super V>>
    ScrollResult<T, V> of(
            List<T> rows,
            Function<T, V> valueExtractor,
            Function<T, Long> idExtractor
    ) {
        if (rows == null || rows.isEmpty()) {
            return new ScrollResult<>(Collections.emptyList(), null, null);
        }
        T last = rows.get(rows.size() - 1);
        V   lv  = valueExtractor.apply(last);
        Long li = idExtractor.apply(last);
        return new ScrollResult<>(rows, lv, li);
    }
}
