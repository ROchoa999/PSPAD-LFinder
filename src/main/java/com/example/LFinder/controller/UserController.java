package com.example.LFinder.controller;

import com.example.LFinder.model.User;
import com.example.LFinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/profile-picture")
    public String getUserProfilePicture(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent() && userOpt.get().getProfilePicture() != null) {
            return "data:image/jpeg;base64," + userOpt.get().getProfilePicture();
        }
        return "/images/usuario.png"; // Imagen por defecto si no hay imagen
    }

    @PostMapping("/update-profile") // Se mantiene el nombre original
    public String updateProfile(@RequestParam("profilePicture") String base64Image,
                                Authentication authentication) {
        if (base64Image == null || base64Image.isBlank()) {
            return ""; // No hacer nada si no se proporciona una imagen
        }

        String currentUsername = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(currentUsername);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setProfilePicture(base64Image);
            userRepository.save(user);
            return "Imagen de perfil actualizada correctamente";
        }

        return "";
    }

}
