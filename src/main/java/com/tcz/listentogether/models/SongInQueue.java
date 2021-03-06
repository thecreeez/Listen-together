package com.tcz.listentogether.models;

import javax.persistence.*;

@Entity
public class SongInQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long lobbyId;
    private Long queuePosition;

    @OneToOne(targetEntity = Song.class, cascade = CascadeType.MERGE, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name="song_id")
    private Song song;

    public SongInQueue() {
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public Long getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(Long queuePosition) {
        this.queuePosition = queuePosition;
    }
}
