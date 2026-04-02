package com.library.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.backend.entity.FineRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.backend.model.vo.FineRecordVO;

/**
 * @author qianxingzhigui
 * @description 针对表【fine_record(罚款记录表)】的数据库操作Service
 * @createDate 2026-03-26 10:47:17
 */
public interface FineRecordService extends IService<FineRecord> {

    // 【新增】获取罚款分页列表
    Page<FineRecordVO> getFinePage(Integer current, Integer size, String keyword, String status);

    // 【新增】结清罚款
    void payFine(Integer fineId, String adminId);
}