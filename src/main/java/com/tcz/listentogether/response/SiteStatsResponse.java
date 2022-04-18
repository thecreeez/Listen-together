package com.tcz.listentogether.response;

public class SiteStatsResponse {
    private long users;
    private long lobbies;
    private long songs;

    public SiteStatsResponse(long users, long lobbies, long songs) {
        this.users = users;
        this.lobbies = lobbies;
        this.songs = songs;
    }

    public long getUsers() {
        return users;
    }

    public long getLobbies() {
        return lobbies;
    }

    public long getSongs() {
        return songs;
    }
}
