package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
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
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkEmailDuplicate(User newUser) {
        boolean emailExists = users.values()
                .stream()
                .anyMatch(existingUser -> existingUser.getEmail().equals(newUser.getEmail()) &&
                        !existingUser.getId().equals(newUser.getId()));

        if (emailExists) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("ID должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getUsername() != null && !newUser.getUsername().isBlank()) {
                oldUser.setUsername(newUser.getUsername());
            }
            if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
                checkEmailDuplicate(newUser);
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getPassword() != null && !newUser.getPassword().isBlank()) {
                oldUser.setPassword(newUser.getPassword());
            }
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    protected Optional<User> findUserById(Long id) {
       return Optional.ofNullable(users.get(id));
    }
}
