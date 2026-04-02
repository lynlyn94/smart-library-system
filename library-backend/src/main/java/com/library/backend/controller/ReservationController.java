package com.library.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.backend.common.Result;
import com.library.backend.model.vo.ReservationVO;
import com.library.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String status) {
        Page<ReservationVO> page = reservationService.getReservationPage(current, size, keyword, status);
        return Result.success(page);
    }

    @PutMapping("/status/{id}")
    public Result updateStatus(@PathVariable Integer id, @RequestParam String status) {
        try {
            reservationService.updateStatus(id, status);
            return Result.success("预约状态更新成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}