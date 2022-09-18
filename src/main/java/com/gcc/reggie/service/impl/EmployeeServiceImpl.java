package com.gcc.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gcc.reggie.entity.Employee;
import com.gcc.reggie.mapper.EmployeeMapper;
import com.gcc.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    //分别传入对应的mapper接口和实体类,然后实现对应的service接口
}
