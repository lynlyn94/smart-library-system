package com.library.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.backend.entity.BorrowRecord;
import com.library.backend.model.vo.BorrowRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {

    // 使用原生的 @Select 注解编写带动态条件的联表 SQL
    @Select("<script>" +
            "SELECT br.*, b.title as book_title, s.name as student_name " +
            "FROM borrow_record br " +
            "LEFT JOIN book b ON br.book_id = b.book_id " +
            "LEFT JOIN student s ON br.student_id = s.student_id " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND (br.book_id LIKE CONCAT('%', #{keyword}, '%') " +
            "         OR b.title LIKE CONCAT('%', #{keyword}, '%') " +
            "         OR br.student_id LIKE CONCAT('%', #{keyword}, '%')) " +
            "  </if>" +
            "  <if test='status != null and status != \"\"'>" +
            "    AND br.status = #{status} " +
            "  </if>" +
            "</where>" +
            "ORDER BY br.borrow_id ASC " +
            "</script>")
    Page<BorrowRecordVO> selectRecordPage(Page<BorrowRecordVO> page,
                                          @Param("keyword") String keyword,
                                          @Param("status") String status);
}