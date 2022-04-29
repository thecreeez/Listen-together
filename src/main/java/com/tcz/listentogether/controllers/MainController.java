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

import java.util.HashMap;
import java.util.Map;
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
        HashMap<String, String> data = new HashMap<>();

        data.put("isAuth", "false");

        if (!token.equals("null")) {
            Optional<User> userOptional = userRepository.findByToken(token);

            if (userOptional != null) {
                data.put("username", userOptional.get().getName());
                data.put("isAuth", "true");

                if (userOptional.get().getLobbyId() != null) {
                    Optional<Lobby> lobbyOptional = lobbyRepository.findById(userOptional.get().getLobbyId());

                    if (!lobbyOptional.isEmpty()) {
                        data.put("lobbyCode", lobbyOptional.get().getCode());
                    }
                }
            }
        }

        data.put("testValue", "123");

        model.addAttribute("data", data);

        return "index";
    }
}