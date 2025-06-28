package com.chris.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryVO {

    private Long categoryId;
    private String name;
    private Integer sort;
    private Short status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
