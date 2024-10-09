package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @GetMapping
    public Collection<Post> findAll(@RequestParam(defaultValue = "desc") String sort,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "0") int from) {
        if (size <= 0) {
            size = 10;
        }
        if (from < 0) {
            from = 0;
        }
        return postService.findAll(SortOrder.from(sort), size, from);
    }

    @GetMapping("/{postId}")
    public Post getPostById(@PathVariable String postId) {
        return postService.getPostById(postId);
    }

    @PostMapping
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}