package com.library.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.backend.common.Result;
import com.library.backend.entity.Student;
import com.library.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@CrossOrigin
public class StudentController {

    @Autowired
    private StudentService studentService;

    // 1. 分页检索
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Student::getStudentId, keyword)
                    .or()
                    .like(Student::getName, keyword);
        }
        Page<Student> page = studentService.page(new Page<>(current, size), wrapper);
        return Result.success(page);
    }

    // 2. 录入新读者
    @PostMapping("/add")
    public Result add(@RequestBody Student student) {
        if (studentService.getById(student.getStudentId()) != null) {
            return Result.error(400, "该学号已存在！");
        }
        // 默认密码处理可以放在这里或前端，目前前端已传
        studentService.save(student);
        return Result.success("读者录入成功");
    }

    // 3. 更新信息 / 冻结解冻
    @PutMapping("/update")
    public Result update(@RequestBody Student student) {
        studentService.updateById(student);
        return Result.success("信息更新成功");
    }

    // 4. 删除账号
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable String id) {
        studentService.removeById(id);
        return Result.success("读者账号已删除");
    }
}