package ru.yandex.practicum.catsgram.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final String error;
}
