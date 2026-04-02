package com.library.backend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.backend.entity.Review;
import com.library.backend.mapper.ReviewMapper;
import com.library.backend.model.dto.ReviewDTO;
import com.library.backend.model.vo.ReviewVO;
import com.library.backend.service.ReviewService;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {
    @Override
    public Page<ReviewVO> getReviewPage(Integer current, Integer size, String keyword, Integer score) {
        return this.baseMapper.selectReviewPage(new Page<>(current, size), keyword, score);
    }
    @Override
    public void addReview(ReviewDTO dto) {
        // 校验：是否已经评价过？
        long count = this.count(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Review>()
                .eq("borrow_id", dto.getBorrowId()));
        if (count > 0) {
            throw new RuntimeException("你已经对这次借阅进行过评价，请勿重复提交哦！");
        }

        Review review = new Review();
        review.setBorrowId(dto.getBorrowId());
        review.setBookId(dto.getBookId());
        review.setStudentId(dto.getStudentId());
        review.setScore(dto.getScore());
        review.setContent(dto.getContent());
        review.setReviewDate(new java.util.Date());

        this.save(review);
    }
}