package ru.yandex.practicum.catsgram.model;

public enum SortOrder {
    ASCENDING,
    DESCENDING;

    public static SortOrder from(String order) {
        return switch (order.toLowerCase()) {
            case "ascending", "asc" -> ASCENDING;
            default -> DESCENDING;
        };
    }
}