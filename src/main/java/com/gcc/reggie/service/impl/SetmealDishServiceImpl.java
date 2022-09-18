package com.gcc.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gcc.reggie.entity.SetmealDish;
import com.gcc.reggie.mapper.SetmealDishMapper;
import com.gcc.reggie.mapper.SetmealMapper;
import com.gcc.reggie.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
