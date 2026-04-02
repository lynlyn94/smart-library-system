package com.library.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.backend.entity.BorrowRecord;
import com.library.backend.model.dto.BorrowDTO;
import com.library.backend.model.vo.BorrowRecordVO;

public interface BorrowRecordService extends IService<BorrowRecord> {
    void borrowBook(BorrowDTO borrowDTO);

    // 【新增】多表联查分页
    Page<BorrowRecordVO> getRecordPage(Integer current, Integer size, String keyword, String status);

    // 【新增】还书核心事务
    void returnBook(Integer recordId);
}