package com.tcz.listentogether.datas;

//import com.tcz.listentogether.models.Song;
import com.tcz.listentogether.models.Song;
import com.tcz.listentogether.models.SongInQueue;

public class SongData {

    private long songId;
    private long queueId;

    private String name;
    private long views;
    private long queuePosition;

    private AlbumData album;
    private AuthorData author;

    public SongData(SongInQueue songInQueue) {
        this.songId = songInQueue.getSong().getId();
        this.queueId = songInQueue.getId();

        this.name = songInQueue.getSong().getName();
        this.views = songInQueue.getSong().getViews();
        this.queuePosition = songInQueue.getQueuePosition();

        this.album = new AlbumData(songInQueue.getSong().getAlbum());
        this.author = new AuthorData(songInQueue.getSong().getAlbum().getAuthor());
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public long getQueueId() {
        return queueId;
    }

    public void setQueueId(long queueId) {
        this.queueId = queueId;
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

    public AlbumData getAlbum() {
        return album;
    }

    public void setAlbum(AlbumData albumData) {
        this.album = albumData;
    }

    public AuthorData getAuthor() {
        return author;
    }

    public void setAuthor(AuthorData authorData) {
        this.author = authorData;
    }

    public long getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(long queuePosition) {
        this.queuePosition = queuePosition;
    }
}
