package com.library.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.backend.entity.Student;
import com.library.backend.service.StudentService;
import com.library.backend.mapper.StudentMapper;
import org.springframework.stereotype.Service;

/**
* @author qianxingzhigui
* @description 针对表【student(学生信息表)】的数据库操作Service实现
* @createDate 2026-03-26 10:47:17
*/
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student>
    implements StudentService{

}




