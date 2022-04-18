package com.tcz.listentogether.datas;

import com.tcz.listentogether.enums.UserState;
import com.tcz.listentogether.models.User;

public class UserData {

    private String name;
    private long id;
    private UserState state;

    public UserData(User user) {
        this.name = user.getName();
        this.id = user.getId();
        this.state = user.getState();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }
}
