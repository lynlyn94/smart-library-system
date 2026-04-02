package com.library.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.backend.entity.Reservation;
import com.library.backend.model.vo.ReservationVO;

/**
 * @author qianxingzhigui
 * @description 针对表【reservation(图书预约表)】的数据库操作Service
 * @createDate 2026-03-26 10:47:17
 */
public interface ReservationService extends IService<Reservation> {

    // 【必须补充】获取预约记录分页列表
    Page<ReservationVO> getReservationPage(Integer current, Integer size, String keyword, String status);

    // 【必须补充】更新预约状态
    void updateStatus(Integer reservationId, String newStatus);

}