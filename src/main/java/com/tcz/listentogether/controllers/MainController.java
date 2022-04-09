package com.tcz.listentogether.controllers;

import com.tcz.listentogether.models.Lobby;
import com.tcz.listentogether.models.Song;
import com.tcz.listentogether.models.User;
import com.tcz.listentogether.repo.LobbyRepository;
import com.tcz.listentogether.repo.SongRepository;
import com.tcz.listentogether.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model,
                        @CookieValue(value = "lastLobby", defaultValue = "null")String lastLobby,
                        @CookieValue(value = "token", defaultValue = "null")String token
    ) {
        String username = "null";

        if (!token.equals("null")) {
            Optional<User> userOptional = userRepository.findByToken(token);

            if (userOptional != null)
                username = userOptional.get().getName();
        }
        model.addAttribute("username", username);

        int lobbies = 0;
        for (Lobby l: lobbyRepository.findAll()) {
            lobbies++;
        }
        model.addAttribute("lobbies", lobbies);

        int songs = 0;
        for (Song s: songRepository.findAll()) {
            songs++;
        }
        model.addAttribute("songs", songs);

        int users = 0;
        for (User u: userRepository.findAll()) {
            users++;
        }
        model.addAttribute("users", users);
        model.addAttribute("lobbyCookie", lastLobby);

        return "index";
    }
}