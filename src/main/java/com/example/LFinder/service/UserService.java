package com.example.LFinder.service;

import com.example.LFinder.model.User;
import com.example.LFinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registro simple existente
    public User registerUser(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El usuario ya existe, colega.");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        return userRepository.save(user);
    }

    // Registro usado en HomeController (con imagen y fecha de registro)
    public User registerUserWithImage(String username, String rawPassword, MultipartFile imagen) throws IOException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El usuario ya existe, colega.");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        String imagenBase64 = Base64.getEncoder().encodeToString(imagen.getBytes());
        user.setProfilePicture(imagenBase64);
        user.setRegistrationDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Obtener imagen de perfil para UserController
    public String getProfilePicture(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getProfilePicture() != null) {
            return "data:image/jpeg;base64," + userOpt.get().getProfilePicture();
        }
        return "/images/usuario.png";
    }

    // Actualizar imagen de perfil para UserController
    public void updateProfilePicture(String username, String base64Image) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setProfilePicture(base64Image);
            userRepository.save(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}

