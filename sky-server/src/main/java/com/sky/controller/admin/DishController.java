package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能简述
 * 菜品相关接口
 * @author hssy
 * @version 1.0
 */


@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 菜品上传
     */
    @PostMapping
    @ApiOperation("菜品上传")
    public Result save(@RequestBody DishDTO dishDTO){

        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }
}
