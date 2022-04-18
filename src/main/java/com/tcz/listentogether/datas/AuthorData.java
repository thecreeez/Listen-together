package com.tcz.listentogether.datas;

import com.tcz.listentogether.models.Author;

public class AuthorData {

    private long id;
    private String name;

    public AuthorData(Author author) {
        this.id = author.getId();
        this.name = author.getName();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
