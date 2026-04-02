package com.library.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.backend.common.Result;
import com.library.backend.model.vo.ReviewVO;
import com.library.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Integer score) {
        Page<ReviewVO> page = reviewService.getReviewPage(current, size, keyword, score);
        return Result.success(page);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            reviewService.removeById(id);
            return Result.success("评价删除成功");
        } catch (Exception e) {
            return Result.error(500, "删除失败");
        }
    }

    @PostMapping("/add")
    public Result addReview(@RequestBody com.library.backend.model.dto.ReviewDTO reviewDTO) {
        try {
            reviewService.addReview(reviewDTO);
            return Result.success("评价发布成功");
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

}