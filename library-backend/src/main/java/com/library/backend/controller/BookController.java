package com.library.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.backend.common.Result;
import com.library.backend.entity.Book;
import com.library.backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@CrossOrigin // 允许跨域
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 1. 分页与高级检索
     */
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        // 支持模糊搜索：ISBN 或 书名 或 作者
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Book::getBookId, keyword)
                    .or()
                    .like(Book::getTitle, keyword)
                    .or()
                    .like(Book::getAuthor, keyword);
        }

        Page<Book> page = bookService.page(new Page<>(current, size), wrapper);
        return Result.success(page);
    }

    /**
     * 2. 录入新书
     */
    @PostMapping("/add")
    public Result add(@RequestBody Book book) {
        // 校验 ISBN 是否已经存在，防止主键冲突报错
        if (bookService.getById(book.getBookId()) != null) {
            return Result.error(400, "该 ISBN 已存在，请检查是否重复录入！");
        }
        bookService.save(book);
        return Result.success("新书录入成功");
    }

    /**
     * 3. 修改图书信息
     */
    @PutMapping("/update")
    public Result update(@RequestBody Book book) {
        bookService.updateById(book);
        return Result.success("图书信息修改成功");
    }

    /**
     * 4. 删除图书
     * 注意：这里的 id 是 ISBN（字符串）
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable String id) {
        bookService.removeById(id);
        return Result.success("图书删除成功");
    }
}