package com.library.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 学生信息表
 */
@Data // Lombok 神器：自动生成所有的 get/set/toString 方法
@TableName("student") // 告诉 MyBatis-Plus 这个类对应数据库的 student 表
public class Student {

    /**
     * 学号 (主键)
     */
    @TableId("student_id")
    private String studentId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 登录密码（加密存储）
     */
    private String password;

    /**
     * 性别 (已从 Object 修正为 String，方便接收 'M' 或 'F')
     */
    private String gender;

    /**
     * 院系
     */
    private String department;

    /**
     * 专业
     */
    private String major;

    /**
     * 年级
     */
    private Integer grade;

    /**
     * 班级 (核心修改点：变量名改为 className 避开关键字，通过注解映射数据库的 class 字段)
     */
    @TableField("class")
    private String className;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 账户状态 (已从 Object 修正为 String)
     */
    private String accountStatus;

    /**
     * 最大借阅量
     */
    private Integer maxBorrow;

    /**
     * 当前借阅量
     */
    private Integer currentBorrow;

}