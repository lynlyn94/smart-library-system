package com.library.backend.model.dto;

import lombok.Data;

@Data // 这个注解是 Lombok 提供的，它会自动在后台帮你生成 get/set 方法
public class LoginDTO {
    private String username;
    private String password;
    private String role; // 约定：前端传 "admin" 代表管理员，"student" 代表学生
}