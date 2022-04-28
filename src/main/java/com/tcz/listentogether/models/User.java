package com.tcz.listentogether.models;

import com.tcz.listentogether.enums.UserState;

import javax.persistence.*;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public User() {
    }

    public User(String name, String password, String token) {
        this.name = name;
        this.password = password;
        this.token = token;
    }

    private String name;
    private String password;
    private UserState state;
    private String token;
    private String simpSessionId;

    @OneToOne(targetEntity = Lobby.class, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name="lobbyId")
    private Lobby lobby;

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSimpSessionId() {
        return simpSessionId;
    }

    public void setSimpSessionId(String simpSessionId) {
        this.simpSessionId = simpSessionId;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }
}
