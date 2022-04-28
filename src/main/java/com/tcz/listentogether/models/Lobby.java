package com.tcz.listentogether.models;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
public class Lobby {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String code;
    private Long time;
    private Long maxTime;
    private boolean isPlaying;

    @OneToOne(targetEntity = SongInQueue.class, cascade = CascadeType.ALL)
    @JoinColumn(name="current_song_id")
    private SongInQueue currentSong;

    @OneToMany(targetEntity = User.class, cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name="id")
    private List<User> users;

    @OneToMany(targetEntity = SongInQueue.class, cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name="lobbyId", referencedColumnName = "id")
    @OrderBy("queuePosition")
    private List<SongInQueue> songsList;

    public Lobby() {
    }

    public List<SongInQueue> getSongsList() {
        return songsList;
    }

    public void setSongsList(List<SongInQueue> songsList) {
        this.songsList = songsList;
    }

    public Long getId() {
        return id;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public SongInQueue getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(SongInQueue currentSong) {
        this.currentSong = currentSong;
    }

    public Long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Long maxTime) {
        this.maxTime = maxTime;
    }
}
