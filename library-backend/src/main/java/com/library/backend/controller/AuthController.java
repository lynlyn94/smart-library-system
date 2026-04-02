package com.library.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.backend.common.JwtUtils;
import com.library.backend.common.Result;
import com.library.backend.entity.Admin;
import com.library.backend.entity.Student;
import com.library.backend.model.dto.LoginDTO;
import com.library.backend.service.AdminService;
import com.library.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController // 代表这个类里所有的接口都返回 JSON 格式数据
@RequestMapping("/api/auth") // 这个控制器下所有接口的统一前缀
@CrossOrigin // 允许跨域请求（因为我们的 Vue 前端在 5173 端口，后端在 8080 端口）
public class AuthController {

    // 自动注入我们刚刚用 MyBatisX 生成的 Service，不需要自己 new
    @Autowired
    private AdminService adminService;

    @Autowired
    private StudentService studentService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        String role = loginDTO.getRole();
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        // 1. 如果是管理员登录
        if ("admin".equals(role)) {
            // 构造查询条件：SELECT * FROM admin WHERE username = ? AND password = ?
            LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Admin::getAdminId, username)
                    .eq(Admin::getPassword, password);

            // 执行查询
            Admin admin = adminService.getOne(queryWrapper);
            if (admin != null) {
                return Result.success(JwtUtils.createToken(admin.getAdminId(), "admin"));
            } else {
                return Result.error(400, "管理员用户名或密码错误");
            }
        }

        // 2. 如果是学生登录
        else if ("student".equals(role)) {
            // 构造查询条件：SELECT * FROM student WHERE username = ? AND password = ?
            LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Student::getStudentId, username)
                    .eq(Student::getPassword, password);

            Student student = studentService.getOne(queryWrapper);
            if (student != null) {
                return Result.success(JwtUtils.createToken(student.getStudentId(), "student"));
            } else {
                return Result.error(400, "学生用户名或密码错误");
            }
        }

        return Result.error(400, "非法的角色类型");
    }
}