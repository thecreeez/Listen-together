package com.tcz.listentogether.controllers;

import com.tcz.listentogether.enums.UserState;
import com.tcz.listentogether.handler.LobbyWebSocketHandler;
import com.tcz.listentogether.models.Lobby;
import com.tcz.listentogether.models.Song;
import com.tcz.listentogether.models.User;
import com.tcz.listentogether.repo.*;
import com.tcz.listentogether.response.LobbyDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
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

    @GetMapping("/get/lobbyData")
    public LobbyDataResponse lobbyData(Model model,
                                       @CookieValue(value = "token", defaultValue = "nonauth")String token
    ) {
        Optional<User> userOptional = userRepository.findByToken(token);

        if (userOptional.isEmpty())
            return null;

        if (userOptional.get().getLobbyId() == null)
            return null;

        Optional<Lobby> lobbyOptional = lobbyRepository.findById(userOptional.get().getLobbyId());

        if (lobbyOptional.isEmpty())
            return null;

        return new LobbyDataResponse(lobbyOptional.get());
    }

    @GetMapping("/lobby/{code}")
    public Iterable<Song> lobby(Model model,
                        @CookieValue(value = "token", defaultValue = "nonauth")String token,
                        @PathVariable(value = "code")String code,
                        @CookieValue(value = "lastLobby", defaultValue = "null")String lastLobby
    ) {
        Optional<Lobby> lobby = lobbyRepository.findByCode(code.toUpperCase());

        if (lobby.isEmpty())
            return null;

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

        return songs;
    }
}
