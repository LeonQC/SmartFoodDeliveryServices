package com.chris.controller;

import com.chris.vo.Result;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/merchants/emps")
public class EmployeeController {

    @DeleteMapping
    public Result delete(Integer[] ids) {

        return Result.success();
    }
}
