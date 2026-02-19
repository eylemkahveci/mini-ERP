package com.minierp.mini_erp.controllers;

import com.minierp.mini_erp.dto.UserDTO;
import com.minierp.mini_erp.entities.User;
import com.minierp.mini_erp.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        return ResponseEntity.ok(userService.createUser(user));
    }

    // READ - tüm kullanıcılar
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // READ - ID'ye göre
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                           @RequestBody UserDTO dto) {
        User updated = new User();
        updated.setUsername(dto.getUsername());
        updated.setPassword(dto.getPassword());
        updated.setRole(dto.getRole());
        return ResponseEntity.ok(userService.updateUser(id, updated));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
