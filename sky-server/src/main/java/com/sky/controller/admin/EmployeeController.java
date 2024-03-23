package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api( tags = "员工相关的接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "员工登出")
    public Result<String> logout() {
        return Result.success();
    }


    /*
    * 新增员工的方法
    *
    *  */
    @PostMapping()
    @ApiOperation("新增员工")
    public Result<String> save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工:{}",employeeDTO.getId());
        employeeService.insert(employeeDTO);
        return Result.success();
    }

    /**
     * 用于查询员工数据
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("用户分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询，参数为:{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 用于启用禁用用户
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用用户")
    public Result startOrStop(@PathVariable Integer status, long id){
        log.info("启用禁用用户,{},{}",status, id);
        employeeService.startOrStop(status, id);
        return Result.success();
    }


    /**
     * 根据id返回一个employee
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id获得单个用户")
    public Result<Employee> getById(@PathVariable long id){
        log.info("返回单个用户,{}",id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }


    /**
     * 修改用户
     * @param employeeDTO
     * @return
     */
    @PutMapping()
    @ApiOperation("更新用户")
    public Result<String> update(@RequestBody EmployeeDTO employeeDTO){
        log.info("用户更新,{}",employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
}
