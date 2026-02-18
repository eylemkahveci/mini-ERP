package com.minierp.mini_erp.services;

import com.minierp.mini_erp.entities.User;
import com.minierp.mini_erp.exceptions.ResourceNotFoundException;
import com.minierp.mini_erp.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // SecurityConfig’teki bean

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE
    public User createUser(User user) {
        // Şifreyi asla plain text saklama
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // READ - tüm kullanıcılar
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // READ - ID'ye göre
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + id));
    }

    // UPDATE
    public User updateUser(Long id, User updated) {
        User existing = getUserById(id);
        existing.setUsername(updated.getUsername());

        // Yeni şifre gönderildiyse encode et, gönderilmediyse eskisini koru
        if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updated.getPassword()));
        }

        existing.setRole(updated.getRole());
        return userRepository.save(existing);
    }

    // DELETE
    public void deleteUser(Long id) {
        User existing = getUserById(id);
        userRepository.delete(existing);
    }
}