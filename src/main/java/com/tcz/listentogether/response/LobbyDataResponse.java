package com.tcz.listentogether.response;

import com.tcz.listentogether.datas.UserData;
import com.tcz.listentogether.models.Lobby;
import com.tcz.listentogether.models.Song;
import com.tcz.listentogether.models.SongInQueue;
import com.tcz.listentogether.models.User;

import java.util.ArrayList;

public class LobbyDataResponse {

    private ArrayList<UserData> users = new ArrayList<>();
    //private ArrayList<SongData> songsInQueue = new ArrayList<>();
    private boolean isPlaying;
    private long time = 0;

    public LobbyDataResponse(Lobby lobby) {
        System.out.println("lobby data: users: "+lobby.getUsers().size()+". songs in queue: "+lobby.getSongsList().size());

        for (User user : lobby.getUsers()) {
            this.users.add(new UserData(user));
        }
/*
        for (SongInQueue songInQueue : lobby.getSongsList()) {
            this.songsInQueue.add(new SongData(songInQueue.getSong()));
        }*/
        this.isPlaying = lobby.isPlaying();

        if (lobby.getTime() != null)
            this.time = lobby.getTime();
    }

    public ArrayList<UserData> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserData> users) {
        this.users = users;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
