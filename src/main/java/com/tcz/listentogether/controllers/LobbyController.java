package com.tcz.listentogether.controllers;

import com.tcz.listentogether.enums.UserState;
import com.tcz.listentogether.models.Lobby;
import com.tcz.listentogether.models.Song;
import com.tcz.listentogether.models.User;
import com.tcz.listentogether.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;


@Controller
public class LobbyController {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/lobby/{code}")
    public String lobby(Model model,
                        @CookieValue(value = "token", defaultValue = "nonauth")String token,
                        @PathVariable(value = "code")String code,
                        @CookieValue(value = "lastLobby", defaultValue = "null")String lastLobby
    ) {
        Optional<Lobby> lobby = lobbyRepository.findByCode(code.toUpperCase());

        if (lobby.isEmpty())
            return "index";

        String username = "null";
        if (!token.equals("null")) {
            Optional<User> userOptional = userRepository.findByToken(token);

            if (userOptional != null)
                username = userOptional.get().getName();
        }
        model.addAttribute("username", username);

        Iterable<Song> songs = songRepository.findAll();
        model.addAttribute("songs", songs);
        model.addAttribute("lobby", lobby.get());

        lobby.get().getUsers().forEach(user -> {
            if (user.getState() == null)
                user.setState(UserState.NULL);
        });

        model.addAttribute("lobbyCookie", lastLobby);

        return "lobby";
    }
}
