package com.gcc.reggie.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gcc.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;


public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
