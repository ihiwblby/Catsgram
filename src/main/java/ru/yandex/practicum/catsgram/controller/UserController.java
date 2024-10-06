package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        // проверяем выполнение необходимых условий
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new ConditionsNotMetException("Имя не может быть пустым");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new ConditionsNotMetException("Пароль не может быть пустым");
        }

        checkEmailDuplicate(user);
        // формируем дополнительные данные
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    // вспомогательный метод для проверки совпадающих имейлов
    private void checkEmailDuplicate(User newUser) {
        boolean emailExists = users.values()
                .stream()
                .anyMatch(existingUser -> existingUser.getEmail().equals(newUser.getEmail()) &&
                        !existingUser.getId().equals(newUser.getId())); // проверка, что email не используется другим пользователем

        if (emailExists) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        // проверяем, что ID указан
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        // проверяем, что пользователь с данным ID существует
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            // обновляем имя пользователя, если оно указано и не пустое
            if (newUser.getUsername() != null && !newUser.getUsername().isBlank()) {
                oldUser.setUsername(newUser.getUsername());
            }
            // если новый email не равен null и он отличается от старого, проверяем на дубликаты
            if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
                checkEmailDuplicate(newUser);
                oldUser.setEmail(newUser.getEmail()); // обновляем email, если он изменился
            }
            // обновляем пароль, если он указан и не пустой
            if (newUser.getPassword() != null && !newUser.getPassword().isBlank()) {
                oldUser.setPassword(newUser.getPassword());
            }
            // возвращаем обновлённого пользователя
            return oldUser;
        }
        // если пользователь с данным ID не найден
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }
}
