package com.library.backend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.backend.entity.FineRecord;
import com.library.backend.mapper.FineRecordMapper;
import com.library.backend.model.vo.FineRecordVO;
import com.library.backend.service.FineRecordService;
import org.springframework.stereotype.Service;

@Service
public class FineRecordServiceImpl extends ServiceImpl<FineRecordMapper, FineRecord> implements FineRecordService {

    @Override
    public Page<FineRecordVO> getFinePage(Integer current, Integer size, String keyword, String status) {
        return this.baseMapper.selectFinePage(new Page<>(current, size), keyword, status);
    }

    @Override
    public void payFine(Integer fineId, String adminId) {
        FineRecord record = this.getById(fineId);
        if (record == null) throw new RuntimeException("找不到该罚单");
        if ("已缴纳".equals(record.getStatus())) throw new RuntimeException("该罚单已经结清");

        record.setStatus("已缴纳");
        record.setHandlerId(adminId); // 记录是哪个管理员收的钱
        this.updateById(record);
    }
}