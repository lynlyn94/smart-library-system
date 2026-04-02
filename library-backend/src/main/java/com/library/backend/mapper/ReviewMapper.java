package com.library.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.backend.entity.Review;
import com.library.backend.model.vo.ReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    @Select("<script>" +
            "SELECT r.*, b.title as book_title, s.name as student_name " +
            "FROM review r " +
            "LEFT JOIN book b ON r.book_id = b.book_id " +
            "LEFT JOIN student s ON r.student_id = s.student_id " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND (r.content LIKE CONCAT('%', #{keyword}, '%') " +
            "         OR b.title LIKE CONCAT('%', #{keyword}, '%') " +
            "         OR s.name LIKE CONCAT('%', #{keyword}, '%')) " +
            "  </if>" +
            "  <if test='score != null'>" +
            "    AND r.score = #{score} " +
            "  </if>" +
            "</where>" +
            "ORDER BY r.review_date DESC " +
            "</script>")
    Page<ReviewVO> selectReviewPage(Page<ReviewVO> page,
                                    @Param("keyword") String keyword,
                                    @Param("score") Integer score);
}