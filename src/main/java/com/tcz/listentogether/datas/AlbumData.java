package com.tcz.listentogether.datas;

import com.tcz.listentogether.models.Album;

public class AlbumData {

    private long id;
    private String name;

    public AlbumData(Album album) {
        this.id = album.getId();
        this.name = album.getName();
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
