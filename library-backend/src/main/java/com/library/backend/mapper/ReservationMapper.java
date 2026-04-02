package com.library.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.backend.entity.Reservation;
import com.library.backend.model.vo.ReservationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

    @Select("<script>" +
            "SELECT r.*, b.title as book_title, s.name as student_name " +
            "FROM reservation r " +
            "LEFT JOIN book b ON r.book_id = b.book_id " +
            "LEFT JOIN student s ON r.student_id = s.student_id " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND (r.book_id LIKE CONCAT('%', #{keyword}, '%') " +
            "         OR b.title LIKE CONCAT('%', #{keyword}, '%') " +
            "         OR r.student_id LIKE CONCAT('%', #{keyword}, '%')) " +
            "  </if>" +
            "  <if test='status != null and status != \"\"'>" +
            "    AND r.status = #{status} " +
            "  </if>" +
            "</where>" +
            "ORDER BY FIELD(r.status, '等待', '可取', '已完成', '取消', '过期'), r.reservation_date DESC " +
            "</script>")
    Page<ReservationVO> selectReservationPage(Page<ReservationVO> page,
                                              @Param("keyword") String keyword,
                                              @Param("status") String status);
}