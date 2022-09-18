package com.gcc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gcc.reggie.common.R;
import com.gcc.reggie.entity.Employee;
import com.gcc.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        // 1,将密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2, 根据用户名userName查询数据库中是否存在该用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee empAfterSearch = employeeService.getOne(queryWrapper);
        if (empAfterSearch == null){
            return  R.error("用户名不存在，登陆失败");
        }
        // 3,校验密码
        //切记这里数据库存的密码是加密过后的
        if (!empAfterSearch.getPassword().equals(password)) {
            return R.error("密码错误，请重新输入");
        }

        // 4,查看员工状态是否可用
        if (empAfterSearch.getStatus() == 0){
            return R.error("账户已被禁用");
        }

        // 5,登录成功后，把用户id放进session
        request.getSession().setAttribute("employee",empAfterSearch.getId());
        return R.success(empAfterSearch);

    }


    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
         //清理Session中保存的当前登录员工的id
         request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    @PostMapping
    public R<String> add(HttpServletRequest request, @RequestBody Employee employee){
        log.info("即将新增一个员工");
//        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
//        Employee emp = employeeService.getOne(lambdaQueryWrapper);
//
//        if (emp != null){
//            return R.error("该用户已存在");
//        }

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));


        employeeService.save(employee);
        log.info("员工{}已成功添加",employee.getUsername());
        return R.success("登陆成功");
    }

    /*
    员工信息分页查询
     */
    @GetMapping("/page")
    public R<Page<Employee>> pages(int page, int pageSize, String name){
        /*1，这一块的代码原理不太理解，但很明显这些全都是MP封装好的方法，所以目前就先记住这些用法做到初步了解，后续有需要再深入研究
          构造分页构造器 这里的Page需要带上引用类型不然会有警告
          2，关于分页查询，单页显示数量，前后翻页，跳转页面等操作其实都是MP封装自动完成的，只要学会用法就行
        要注意的是MybatisPlusConfig这个类必须要配置正确，类要加上Configuration注解，方法要加上Bean注解
         */
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, lambdaQueryWrapper);

        return  R.success(pageInfo);
    }

    /*
    根据id修改员工信息
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        Long empId = (Long)request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("员工信息编辑成功");
    }


    /**
     * 通过员工id来查询员工的信息
     * @param id
     * @return
     */
    //注意这里路径参数的命名要和数据库的字段一样，不然会映射失败
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee emp = employeeService.getById(id);
        if (emp == null){
            return R.error("该用户不存在");
        }
        return R.success(emp);
    }

}
