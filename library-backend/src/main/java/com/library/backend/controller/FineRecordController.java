package com.library.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.backend.common.Result;
import com.library.backend.model.vo.FineRecordVO;
import com.library.backend.service.FineRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fines")
@CrossOrigin
public class FineRecordController {

    @Autowired
    private FineRecordService fineRecordService;

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String status) {
        Page<FineRecordVO> page = fineRecordService.getFinePage(current, size, keyword, status);
        return Result.success(page);
    }

    @PutMapping("/pay/{fineId}")
    public Result pay(@PathVariable Integer fineId, @RequestParam String adminId) {
        try {
            fineRecordService.payFine(fineId, adminId);
            return Result.success("结清成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}