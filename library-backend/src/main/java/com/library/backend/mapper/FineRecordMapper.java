package com.library.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.backend.entity.FineRecord;
import com.library.backend.model.vo.FineRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FineRecordMapper extends BaseMapper<FineRecord> {

    @Select("<script>" +
            "SELECT f.*, s.name as student_name, a.name as handler_name " +
            "FROM fine_record f " +
            "LEFT JOIN student s ON f.student_id = s.student_id " +
            "LEFT JOIN admin a ON f.handler_id = a.admin_id " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND (f.student_id LIKE CONCAT('%', #{keyword}, '%') " +
            "         OR s.name LIKE CONCAT('%', #{keyword}, '%')) " +
            "  </if>" +
            "  <if test='status != null and status != \"\"'>" +
            "    AND f.status = #{status} " +
            "  </if>" +
            "</where>" +
            "ORDER BY f.status ASC, f.generate_date DESC " + // 未缴纳的排前面
            "</script>")
    Page<FineRecordVO> selectFinePage(Page<FineRecordVO> page,
                                      @Param("keyword") String keyword,
                                      @Param("status") String status);
}