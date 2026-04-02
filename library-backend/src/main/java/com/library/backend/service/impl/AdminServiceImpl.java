package com.library.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.backend.entity.Admin;
import com.library.backend.service.AdminService;
import com.library.backend.mapper.AdminMapper;
import org.springframework.stereotype.Service;

/**
* @author qianxingzhigui
* @description 针对表【admin(管理员信息表)】的数据库操作Service实现
* @createDate 2026-03-26 10:47:17
*/
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
    implements AdminService{

}




