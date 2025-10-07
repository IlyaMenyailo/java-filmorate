package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("friendshipDbStorage") FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public List<User> getAllUsers() {
        List<User> users = userStorage.getAll();
        users.forEach(this::loadFriendsForUser);
        return users;
    }

    public User getUserById(Long id) {
        User user = userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
        loadFriendsForUser(user);
        return user;
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        getUserById(user.getId()); // Проверяем существование
        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);

        friendshipStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);

        friendshipStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        getUserById(userId); // Проверяем существование пользователя

        Set<Long> friendsIds = friendshipStorage.getFriends(userId);

        return friendsIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        getUserById(userId);
        getUserById(otherId);

        Set<Long> commonFriendIds = friendshipStorage.getCommonFriends(userId, otherId);

        return commonFriendIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    private void loadFriendsForUser(User user) {
        Set<Long> friendsIds = friendshipStorage.getFriends(user.getId());
        user.setFriends(friendsIds);
    }
}