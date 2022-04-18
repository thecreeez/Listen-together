package com.tcz.listentogether.datas;

import com.tcz.listentogether.models.Song;
import com.tcz.listentogether.models.SongInQueue;

public class SongData {

    private long id;
    private String name;
    private long views;

    private AlbumData albumData;
    private AuthorData authorData;

    public SongData(Song song) {
        this.id = song.getId();
        this.name = song.getName();
        this.views = song.getViews();

        this.albumData = new AlbumData(song.getAlbum());
        this.authorData = new AuthorData(song.getAlbum().getAuthor());
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

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public AlbumData getAlbumData() {
        return albumData;
    }

    public void setAlbumData(AlbumData albumData) {
        this.albumData = albumData;
    }

    public AuthorData getAuthorData() {
        return authorData;
    }

    public void setAuthorData(AuthorData authorData) {
        this.authorData = authorData;
    }
}
