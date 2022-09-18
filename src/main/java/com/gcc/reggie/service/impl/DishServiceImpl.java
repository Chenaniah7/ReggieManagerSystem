package com.gcc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gcc.reggie.dto.DishDto;
import com.gcc.reggie.entity.Dish;
import com.gcc.reggie.entity.DishFlavor;
import com.gcc.reggie.mapper.DishMapper;
import com.gcc.reggie.service.DishFlavorService;
import com.gcc.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;


    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //先把菜品信息保存，向数据库中插入新增的菜品信息
        this.save(dishDto);

        //把菜品id set进dishFlavor 里面
        List<DishFlavor> dishFlavorList = dishDto.getFlavors();
        dishFlavorList.forEach(dishFlavor -> {
            dishFlavor.setDishId(dishDto.getId());
        });
        //批量保存dishFlavor数据，向dishFlavor表中插入数据
        dishFlavorService.saveBatch(dishFlavorList);
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //先把菜品插入数据库，然后获取dishFlavor列表，遍历dishFlavor列表并给每个元素设置dishId
        //最后再把dishFlavor也批量插入数据库
        this.updateById(dishDto);

        List<DishFlavor> dishFlavorList = dishDto.getFlavors();
        dishFlavorList.forEach(dishFlavor -> {
            dishFlavor.setDishId(dishDto.getId());
        });
        dishFlavorService.updateBatchById(dishFlavorList);
    }


    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //从dish表查询菜品基本信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //从dishFlavor中查询口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }
}
