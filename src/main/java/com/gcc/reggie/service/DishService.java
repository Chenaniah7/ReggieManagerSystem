package com.gcc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gcc.reggie.dto.DishDto;
import com.gcc.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    void updateWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);
}
