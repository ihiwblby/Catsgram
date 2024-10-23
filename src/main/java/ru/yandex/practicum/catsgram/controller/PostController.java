package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
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
                                    @RequestParam(defaultValue = "0") int from) throws ParameterNotValidException {
        SortOrder sortOrder = SortOrder.from(sort);
        if(sortOrder == null) {
            throw new ParameterNotValidException("sort", "Получено: " + sort + " должно быть: ask или desc");
        }
        if (size <= 0) {
            throw new ParameterNotValidException("size", "Размер должен быть больше нуля");
        }
        if (from < 0) {
            throw new ParameterNotValidException("from", "Начало выборки должно быть положительным числом");
        }

        return postService.findAll(sortOrder, size, from);
    }

    @GetMapping("/{postId}")
    public Post getPostById(@PathVariable String postId) {
        return postService.getPostById(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}