package com.tcz.listentogether.controllers;

import com.tcz.listentogether.TokenCookie;
import com.tcz.listentogether.models.User;
import com.tcz.listentogether.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/auth/login")
    public String login(Model model) {
        return "auth/login";
    }

    @PostMapping("/post/login")
    public String loginPost(Model model, @RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        Optional<User> userOptional = userRepository.findByName(username);

        if (userOptional == null) {
            System.out.println("Пользователя нет");
            return "auth/error";
        }

        if (!userOptional.get().getPassword().equals(password)) {
            System.out.println("Пароль неверный!");
            return "auth/error";
        }

        TokenCookie tokenCookie = new TokenCookie(UUID.randomUUID().toString());

        userOptional.get().setToken(tokenCookie.getCookie().getValue());
        userRepository.save(userOptional.get());

        response.addCookie(tokenCookie.getCookie());

        return "redirect:/";
    }

    @GetMapping("/auth/register")
    public String register(Model model) {
        return "auth/register";
    }

    @PostMapping("/post/register")
    public String registerPost(Model model, @RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        Optional<User> userOptional = userRepository.findByName(username);

        if (userOptional == null)
            return "auth/error";

        TokenCookie tokenCookie = new TokenCookie(UUID.randomUUID().toString());
        response.addCookie(tokenCookie.getCookie());

        User user = new User(username, password, tokenCookie.getCookie().getValue());
        userRepository.save(user);

        return "redirect:/";
    }

    @GetMapping("/auth/leave")
    public String leave(Model model, @CookieValue(value = "token", defaultValue = "null")String token, HttpServletResponse response) {
        TokenCookie tokenCookie = new TokenCookie(null);
        response.addCookie(tokenCookie.getCookie());
        return "redirect:/";
    }
}
