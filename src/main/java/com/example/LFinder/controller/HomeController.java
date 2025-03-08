package com.example.LFinder.controller;

import com.example.LFinder.model.User;
import com.example.LFinder.repository.UserRepository;
import com.example.LFinder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login.html";
    }

    @GetMapping("/signup")
    public String signup() {
        return "redirect:/signup.html";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", username);
            model.addAttribute("profilePicture", "data:image/jpeg;base64," + user.getProfilePicture());
            model.addAttribute("registrationDate", user.getRegistrationDate());
        } else {
            model.addAttribute("user", "Desconocido");
            model.addAttribute("profilePicture", "/images/usuario.png");
            model.addAttribute("registrationDate", "No disponible");
        }
        return "profile";
    }

    @GetMapping("/index")
    public String index(Model model, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            model.addAttribute("user", username);
            model.addAttribute("profilePicture", "data:image/jpeg;base64," + userOpt.get().getProfilePicture());
        } else {
            model.addAttribute("profilePicture", "/images/usuario.png");
        }
        return "index";
    }

    @PostMapping("/sign-up")
    public String processSignUp(@RequestParam String user,
                                @RequestParam String password,
                                @RequestParam("imagen") MultipartFile imagen) {
        try {
            Optional<User> registeredUser = userService.registerUserWithImage(user, password, imagen);
            if (registeredUser.isPresent()) {
                // Registro exitoso: redirige a la página de login
                return "redirect:/login.html";
            } else {
                // El usuario ya existe: redirige a la página de registro con parámetro de error
                return "redirect:/signup.html?error=true";
            }
        } catch (IOException e) {
            e.printStackTrace();
            // En caso de error de IO, se redirige con error
            return "redirect:/signup.html?error=true";
        }
    }

}
