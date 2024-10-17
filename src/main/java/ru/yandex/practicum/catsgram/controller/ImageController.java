package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;
import ru.yandex.practicum.catsgram.service.ImageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/posts/{postId}/images")
    public List<Image> getPostImages(@PathVariable("postId") long postId) {
        return imageService.getPostImages(postId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/posts/{postId}/images")
    public List<Image> addPostImages(@PathVariable("postId") long postId,
                                     @RequestParam("image") List<MultipartFile> files) {
        return imageService.saveImages(postId, files);
    }

    @GetMapping(value = "/images/{imageId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE) // ответ содержит бинарные данные
    public ResponseEntity<byte[]> downloadImage(@PathVariable long imageId) {
        ImageData imageData = imageService.getImageData(imageId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition( //контент должен быть загружен как файл
                ContentDisposition.attachment() //задаёт режим "attachment", который заставляет браузер инициировать скачивание файла
                        .filename(imageData.getName()) // указывает имя файла, под которым он будет сохранён на клиенте
                        .build()
        );

        return new ResponseEntity<>(imageData.getData(), headers, HttpStatus.OK);
    }
}
