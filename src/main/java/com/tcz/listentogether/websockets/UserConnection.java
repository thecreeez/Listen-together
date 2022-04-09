package com.tcz.listentogether.websockets;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.tcz.listentogether.enums.UserState;

public class UserConnection {
    private String name;
    private long uuid;

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    private UserState state;

    public UserConnection(String name, UserState state, long UUID) {
        this.name = name;
        this.state = state;
        this.uuid = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public String toString() {
        return name+"//"+state+"//"+uuid;
    }
}
