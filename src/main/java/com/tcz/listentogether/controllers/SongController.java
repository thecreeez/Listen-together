package com.tcz.listentogether.controllers;

import com.tcz.listentogether.models.Lobby;
import com.tcz.listentogether.models.User;
import com.tcz.listentogether.repo.LobbyRepository;
import com.tcz.listentogether.repo.SongRepository;
import com.tcz.listentogether.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Optional;

@RestController
public class SongController {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public LobbyRepository lobbyRepository;

    @GetMapping("/songs/getCurrentSong")
    public ResponseEntity<FileSystemResource> streamCurrentSong(@CookieValue(value = "token", defaultValue = "nonauth")String token) {
        Optional<User> userOptional = userRepository.findByToken(token);

        if (userOptional.isEmpty())
            return null;

        if (userOptional.get().getLobbyId() == null)
            return null;

        Optional<Lobby> lobby = lobbyRepository.findById(userOptional.get().getLobbyId());

        if (lobby.isEmpty())
            return null;


        if (lobby.get().getCurrentSong() == null)
            return null;

        System.out.println("Пользователь "+userOptional.get().getName()+" получил песню "+lobby.get().getCurrentSong().getSong().getName()+"."+" Путь: "+lobby.get().getCurrentSong().getSong().getPath());

        FileSystemResource resource = new FileSystemResource(lobby.get().getCurrentSong().getSong().getPath());

        MediaType mediaType = MediaTypeFactory
                .getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        ContentDisposition disposition = ContentDisposition
                .inline()
                .filename(/*lobbyOptional.get().getCurrentSong().getSong().getAlbum().getAuthor().getName()+" - "+*/lobby.get().getCurrentSong().getSong().getName())
                .build();
        headers.setContentDisposition(disposition);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
