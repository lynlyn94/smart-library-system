package com.library.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.backend.entity.Review;
import com.library.backend.model.dto.ReviewDTO;
import com.library.backend.model.vo.ReviewVO;

public interface ReviewService extends IService<Review> {
    Page<ReviewVO> getReviewPage(Integer current, Integer size, String keyword, Integer score);
    void addReview(ReviewDTO reviewDTO);
}