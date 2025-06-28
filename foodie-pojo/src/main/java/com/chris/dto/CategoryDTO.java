package com.chris.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用于新增，更新菜品分类信息的数据传输对象
 */
@Data
public class CategoryDTO {
    @NotNull
    private String name;
    @NotNull
    private Integer sort;

}
