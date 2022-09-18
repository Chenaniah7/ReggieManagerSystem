package com.gcc.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gcc.reggie.dto.DishDto;
import com.gcc.reggie.dto.SetmealDto;
import com.gcc.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    public SetmealDto getWithSetmealDish(Long id);
}
