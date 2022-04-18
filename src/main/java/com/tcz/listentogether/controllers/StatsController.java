package com.tcz.listentogether.controllers;

import com.tcz.listentogether.response.SiteStatsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    @GetMapping("/get/stats")
    public SiteStatsResponse getStats() {
        SiteStatsResponse siteStatsResponse = new SiteStatsResponse(3,4,5);
        return siteStatsResponse;
    }
}
