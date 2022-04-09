package com.tcz.listentogether.controllers;

import com.tcz.listentogether.TokenCookie;
import com.tcz.listentogether.models.Album;
import com.tcz.listentogether.models.Author;
import com.tcz.listentogether.models.Song;
import com.tcz.listentogether.models.User;
import com.tcz.listentogether.repo.AlbumRepository;
import com.tcz.listentogether.repo.AuthorRepository;
import com.tcz.listentogether.repo.SongRepository;
import com.tcz.listentogether.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Controller
public class UploadController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SongRepository songRepository;

    @GetMapping("/upload")
    public String login(Model model, @CookieValue(value = "lastLobby", defaultValue = "null")String lastLobby) {

        return "upload";
    }

    @PostMapping("/post/addSong")
    public String addSong(Model model,
                          @CookieValue(value = "token")String token,
                          @RequestParam String name,
                          @RequestParam String album,
                          @RequestParam String author,
                          @RequestParam("file") MultipartFile file
                          )  {
        Optional<User> userOptional = userRepository.findByToken(token);
        if (userOptional.isEmpty()) {
            System.out.println("Пользователя нет");
            return "auth/login";
        }

        if (file.isEmpty()) {
            System.out.println("Файл пуст");
            return "auth/login";
        }

        byte[] bytes = new byte[0];
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            e.getMessage();
        }

        Optional<Author> authorOptional = authorRepository.findByName(author);
        if (authorOptional.isEmpty()) {
            Author newAuthor = new Author(author);
            authorRepository.save(newAuthor);
            authorOptional = authorRepository.findByName(author);
        }

        Optional<Album> albumOptional = albumRepository.findByName(album);
        if (albumOptional.isEmpty() || albumOptional.get().getAuthor().getName() != authorOptional.get().getName()) {
            Album newAlbum = new Album(authorOptional.get().getId(), album);
            albumRepository.save(newAlbum);
            albumOptional = albumRepository.findByName(album);
        }

        Optional<Song> songOptional = songRepository.findByName(name);
        if (!songOptional.isEmpty()) {
            if (songOptional.get().getAlbum().getName() == album) {
                System.out.println("Песня с таким названием уже есть в альбоме!");
            }
        }

        String path = "songs/"+UUID.randomUUID()+".mp3";

        Song song = new Song(albumOptional.get().getId(), name, path);
        songRepository.save(song);

        try {
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(new File(path)));
            stream.write(bytes);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "redirect:/";
    }



    @PostMapping("/post/upload")
    public String loginPost(Model model,
                            @CookieValue(value = "token", defaultValue = "nonauth")String token
    ) {
        Optional<User> userOptional = userRepository.findByToken(token);

        if (userOptional == null) {
            System.out.println("Пользователя нет");
            return "auth/error";
        }



        return "redirect:/";
    }
}
