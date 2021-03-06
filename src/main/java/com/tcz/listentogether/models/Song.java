package com.tcz.listentogether.models;

import javax.persistence.*;

@Entity
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int views;

    private String name;
    private String path;

    public Song() {
    }

    public Song(Album album, String name, String path) {
        this.album = album;
        this.name = name;
        this.path = path;
    }

    @OneToOne(targetEntity = Album.class, cascade = CascadeType.ALL)
    @JoinColumn(name="album_id")
    private Album album;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
