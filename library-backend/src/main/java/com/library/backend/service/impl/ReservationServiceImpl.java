package com.library.backend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.backend.entity.Reservation;
import com.library.backend.mapper.ReservationMapper;
import com.library.backend.model.vo.ReservationVO;
import com.library.backend.service.ReservationService;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

    @Override
    public Page<ReservationVO> getReservationPage(Integer current, Integer size, String keyword, String status) {
        return this.baseMapper.selectReservationPage(new Page<>(current, size), keyword, status);
    }

    @Override
    public void updateStatus(Integer reservationId, String newStatus) {
        Reservation reservation = this.getById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("找不到该预约记录");
        }
        reservation.setStatus(newStatus);
        this.updateById(reservation);
    }
}