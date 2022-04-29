package com.tcz.listentogether.controllers;

import com.tcz.listentogether.models.Lobby;
import com.tcz.listentogether.repo.LobbyRepository;
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

    @GetMapping("/get/stats")
    public SiteStatsResponse getStats() {
        SiteStatsResponse siteStatsResponse = new SiteStatsResponse(3,4,5);
        return siteStatsResponse;
    }

    @GetMapping("/get/lobbyDebug/{lobbyCode}")
    public LobbyDataResponse getLobbyData(@PathVariable(value = "lobbyCode")String code) {
        return new LobbyDataResponse(lobbyRepository.findByCode(code).get());
    }
}
