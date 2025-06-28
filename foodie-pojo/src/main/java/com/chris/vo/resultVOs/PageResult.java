package com.chris.vo.resultVOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T>{
    private Long total; // 总页数
    private List<T> rows; // 当前页数据
}
