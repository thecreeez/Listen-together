package com.tcz.listentogether;

import com.tcz.listentogether.models.Lobby;
import com.tcz.listentogether.models.Song;
import com.tcz.listentogether.models.SongInQueue;
import com.tcz.listentogether.repo.LobbyRepository;
import com.tcz.listentogether.repo.SongInQueueRepository;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;

public class LobbyAudioController {

    Lobby lobby;
    LobbyRepository lobbyRepository;
    SongInQueueRepository songInQueueRepository;

    public LobbyAudioController(Lobby lobby, LobbyRepository lobbyRepository, SongInQueueRepository songInQueueRepository) {
        this.lobby = lobby;
        this.lobbyRepository = lobbyRepository;
        this.songInQueueRepository = songInQueueRepository;
    }

    public void update() {
        if (lobby.getCurrentSong() == null) {
            System.out.println("Текущей песни нет...");
            nextSong();
        } else {
            long currTime = 0;

            if (lobby.getMaxTime() == null)
                lobby.setMaxTime(getDurationSong(lobby.getCurrentSong().getSong()));

            if (lobby.getTime() != null)
                currTime = lobby.getTime();

            lobby.setTime(currTime+500L);

            System.out.println("Прогресс песни: Максимум времени: "+lobby.getMaxTime()+" Текущее время: "+lobby.getTime());

            if (lobby.getTime() >= lobby.getMaxTime())
                nextSong();
        }

        lobbyRepository.save(lobby);
    }

    private void nextSong() {
        SongInQueue nextSong = getNextSong();

        if (nextSong != null) {
            lobby.setCurrentSong(nextSong);
            lobby.setMaxTime(getDurationSong(lobby.getCurrentSong().getSong()));
            System.out.println("Смена песни на "+nextSong.getSong().getName()+". Длина: "+lobby.getMaxTime());
        } else {
            lobby.setPlaying(false);
            lobby.setMaxTime(null);
            lobby.setCurrentSong(null);
            System.out.println("Смена песни не вышла... следующей песни нет!");
        }

        lobby.setTime(0l);
    }

    private long getDurationSong(Song song) {
        long duration = -1;

        try {
            AudioFile audioFile = AudioFileIO.read(new File(song.getPath()));

            duration = (long) (audioFile.getAudioHeader().getPreciseTrackLength() * 1000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }

        return duration;
    }

    private SongInQueue getNextSong() {
        long queuePositionCurrentSong = -1;
        if (lobby.getCurrentSong() != null)
            queuePositionCurrentSong = lobby.getCurrentSong().getQueuePosition();

        SongInQueue out = null;

        System.out.println("Поиск следующей песни... Всего в очереди: "+lobby.getSongsList().size());
        for (int i = 0; i < lobby.getSongsList().size(); i++) {
            if (queuePositionCurrentSong > lobby.getSongsList().get(i).getQueuePosition() + 10)
                songInQueueRepository.delete(lobby.getSongsList().get(i));

            if (queuePositionCurrentSong < lobby.getSongsList().get(i).getQueuePosition() &&
               (out == null || out.getQueuePosition() > lobby.getSongsList().get(i).getQueuePosition())) {
                out = lobby.getSongsList().get(i);
            }
        }

        if (out != null)
            System.out.println("Следующая песня: "+out.getSong().getName());
        return out;
    }

    private long getMaxQueuePosition() {
        long maxQueuePosition = 1;

        for (int i = 0; i < lobby.getSongsList().size(); i++) {
            if (maxQueuePosition < lobby.getSongsList().get(i).getQueuePosition()) {
                maxQueuePosition = lobby.getSongsList().get(i).getQueuePosition();
            }
        }

        return maxQueuePosition;
    }

    public boolean playstop() {
        System.out.println(lobby.getCurrentSong());

        if (lobby.getCurrentSong() == null) {
            nextSong();
        }

        if (lobby.getCurrentSong() != null) {
            System.out.println("Состояние лобби "+lobby.getCode()+" : isPlaying: "+lobby.isPlaying()+" -> "+!lobby.isPlaying());

            lobby.setPlaying(!lobby.isPlaying());
        }

        lobbyRepository.save(lobby);
        return lobby.isPlaying();
    }

    public void skipSong() {
        System.out.println("В лобби "+lobby.getCode()+" пропущена песня "+lobby.getCurrentSong().getSong().getName());

        nextSong();
    }

    public void addSongToList(Song song) {
        System.out.println("В лобби "+lobby.getCode()+" добавлена новая песня "+song.getName()+". Позиция в очереди: "+(getMaxQueuePosition()+1));

        SongInQueue songInQueue = new SongInQueue();

        songInQueue.setSong(song);
        songInQueue.setLobbyId(lobby.getId());
        songInQueue.setQueuePosition(getMaxQueuePosition()+1);

        songInQueueRepository.save(songInQueue);
    }
}
