package com.gcc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gcc.reggie.common.R;
import com.gcc.reggie.dto.DishDto;
import com.gcc.reggie.entity.Category;
import com.gcc.reggie.entity.Dish;
import com.gcc.reggie.service.CategoryService;
import com.gcc.reggie.service.DishFlavorService;
import com.gcc.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name){
        Page<Dish> dishPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage,queryWrapper);


        //之前的page到这一部就结束了，但DishController的list页面有个比较特殊的需求是它会引用category里面的name属性，
        //而dish对象是没有category name属性的，所以需要通过DishDto去获取
        Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
         /*
         思路： 1,先把获得的dish page的全部记录拿出来，通过getRecords方法可以获得
               2, 遍历dish page的每一个元素，通过每个dish元素的categoryId来检索获得一个category对象
               3, 在遍历dish page的时候新建一个dishdto对象，然后把dish对象拷贝进dishdto对象里，从而获得除categoryName之外的全部其他属性
                  最后再通过第2步检索出的category对象获取到categoryName，然后把这个categoryName set进每个dishdto对象
               4，最后把所有dishdto对象转换成一个list集合，然后把这个集合set进 DishDto的page里面，从而得到一个具有完整属性的page
          */
         dishDtoPage.setRecords(dishPage.getRecords().stream().map(item -> {
             DishDto dishDto = new DishDto();
             BeanUtils.copyProperties(item,dishDto);
             //每个dish都有一个categoryId属性
             Category category = categoryService.getById(item.getCategoryId());
             if (category != null){
                 dishDto.setCategoryName(category.getName());
             }
             return dishDto;
         }).collect(Collectors.toList()));
        return R.success(dishDtoPage);
    }

    @PostMapping
    public R<String> add(@RequestBody DishDto dishDto){
        log.info("dish:{}",dishDto);
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }


    /*
    根据id查询对应的菜品信息和口味信息
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        if (dishDto == null){
            return R.error("该用户不存在");
        }
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品信息成功");
    }

    @GetMapping("/list")
    public R<List<Dish>> getDishList(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1); //1是正常售卖，0是下架了
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);
        return R.success(dishList);
    }

}
