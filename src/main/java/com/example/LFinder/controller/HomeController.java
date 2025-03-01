package com.example.LFinder.controller;

import com.example.LFinder.model.User;
import com.example.LFinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home() {
        return "redirect:/login.html";
    }

    @GetMapping("/signup")
    public String signup() {
        return "redirect:/signup.html";
    }

    @GetMapping("/index")
    public String index(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("user", username);
        return "index";
    }

    @PostMapping("/sign-up")
    public String processSignUp(@RequestParam String user,
                                @RequestParam String password,
                                @RequestParam("imagen") MultipartFile imagen,
                                RedirectAttributes redirectAttributes) {
        try {
            // Convertir imagen a Base64
            String imagenBase64 = Base64.getEncoder().encodeToString(imagen.getBytes());

            // Crear y configurar el nuevo usuario
            User newUser = new User();
            newUser.setUsername(user);
            newUser.setPassword(passwordEncoder.encode(password)); // Contraseña cifrada
            newUser.setProfilePicture(imagenBase64);
            newUser.setRegistrationDate(LocalDateTime.now());

            // Guardar el usuario en la base de datos
            userRepository.save(newUser);

            // Mensaje de éxito para mostrar en login (opcional)
            redirectAttributes.addFlashAttribute("successMessage", "Registro exitoso. Inicia sesión.");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al registrar usuario.");
        }

        // Redirigir al usuario a la página de login después del registro
        return "redirect:/";
    }

}
