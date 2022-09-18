package com.gcc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gcc.reggie.common.R;
import com.gcc.reggie.dto.DishDto;
import com.gcc.reggie.dto.SetmealDto;
import com.gcc.reggie.entity.Setmeal;
import com.gcc.reggie.entity.SetmealDish;
import com.gcc.reggie.service.SetmealDishService;
import com.gcc.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<SetmealDish> setmealDishPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(name), SetmealDish::getName, name);
        setmealDishService.page(setmealDishPage, queryWrapper);

        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(setmealDishPage, setmealDtoPage, "records");
        setmealDtoPage.setRecords(setmealDishPage.getRecords().stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Setmeal setmeal = setmealService.getById(item.getSetmealId());
            setmealDto.setCategoryName(setmeal.getName());
            return setmealDto;
        }).collect(Collectors.toList()));

        return R.success(setmealDtoPage);
    }

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @DeleteMapping
    public R<String> deleteBySetmealDishId(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        List<Setmeal> setmealList = new ArrayList<>();
        ids.forEach(id -> {
            queryWrapper.eq(Setmeal::getId, id);
            Setmeal setmeal =setmealService.getById(queryWrapper);
            setmeal.setStatus(status);
            setmealList.add(setmeal);
        });
        setmealService.updateBatchById(setmealList);
        return R.success("状态修改成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getWithSetmealDish(id);
        return R.success(setmealDto);
    }
}
