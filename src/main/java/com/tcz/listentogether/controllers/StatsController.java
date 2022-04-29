package com.tcz.listentogether.controllers;

import com.tcz.listentogether.models.Lobby;
import com.tcz.listentogether.models.Song;
import com.tcz.listentogether.models.User;
import com.tcz.listentogether.repo.LobbyRepository;
import com.tcz.listentogether.repo.SongRepository;
import com.tcz.listentogether.repo.UserRepository;
import com.tcz.listentogether.response.LobbyDataResponse;
import com.tcz.listentogether.response.SiteStatsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    @Autowired
    LobbyRepository lobbyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SongRepository songRepository;

    @GetMapping("/get/stats")
    public SiteStatsResponse getStats() {
        Iterable<Lobby> lobbies = lobbyRepository.findAll();
        Long lobbiesAmount = 0l;
        for (Lobby lobby : lobbies)
            lobbiesAmount++;

        Iterable<User> users = userRepository.findAll();
        Long usersAmount = 0l;
        for (User user : users)
            usersAmount++;

        Iterable<Song> songs = songRepository.findAll();
        Long songsAmount = 0l;
        for (Song song : songs)
            songsAmount++;

        SiteStatsResponse siteStatsResponse = new SiteStatsResponse(usersAmount,lobbiesAmount,songsAmount);
        return siteStatsResponse;
    }

    @GetMapping("/get/lobbyDebug/{lobbyCode}")
    public LobbyDataResponse getLobbyData(@PathVariable(value = "lobbyCode")String code) {
        return new LobbyDataResponse(lobbyRepository.findByCode(code).get());
    }
}
