package com.gcc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gcc.reggie.common.R;
import com.gcc.reggie.entity.Category;
import com.gcc.reggie.entity.Dish;
import com.gcc.reggie.entity.Setmeal;
import com.gcc.reggie.mapper.CategoryMapper;
import com.gcc.reggie.service.CategoryService;
import com.gcc.reggie.service.DishService;
import com.gcc.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService{

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        //检查有没有关联菜品分类
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //如果某个食物的categoryId 等于传进来的id，说明这个菜单分类已经关联了商品
        queryWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(queryWrapper);

        if (dishCount>0){
            throw new RuntimeException("当前分类下已关联了菜品，无法删除");
        }

        //检查有没有关联套餐分类
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);

        if (setmealCount>0){
            throw new RuntimeException("当前分类下已关联了菜品，无法删除");
        }

        super.removeById(id);
    }
}
