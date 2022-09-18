package com.gcc.reggie.dto;


import com.gcc.reggie.entity.Setmeal;
import com.gcc.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
