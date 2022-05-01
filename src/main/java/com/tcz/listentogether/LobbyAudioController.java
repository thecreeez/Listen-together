package com.tcz.listentogether;

import com.tcz.listentogether.enums.QueueState;
import com.tcz.listentogether.enums.UserState;
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

import javax.el.Expression;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class LobbyAudioController {

    private Lobby lobby;
    private LobbyRepository lobbyRepository;
    private SongInQueueRepository songInQueueRepository;

    private boolean isQueueEmpty;
    private boolean isSongEnded;


    Long historySaveSize = 5l;

    public LobbyAudioController(Lobby lobby, LobbyRepository lobbyRepository, SongInQueueRepository songInQueueRepository) {
        this.lobby = lobby;
        this.lobbyRepository = lobbyRepository;
        this.songInQueueRepository = songInQueueRepository;

        this.isQueueEmpty = lobby.getCurrentSong() == null;
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

            lobby.setTime(currTime+500l);

            if (lobby.getTime() >= lobby.getMaxTime())
                nextSong();
        }

        lobbyRepository.save(lobby);
    }

    public boolean playstop() {
        if (lobby.getCurrentSong() == null)
            startCurrentSong();

        if (lobby.getCurrentSong() != null) {
            System.out.println("Состояние лобби "+lobby.getCode()+" : isPlaying: "+lobby.isPlaying()+" -> "+!lobby.isPlaying());

            lobby.setPlaying(!lobby.isPlaying());
        }

        lobbyRepository.save(lobby);
        return lobby.isPlaying();
    }

    public QueueState changeQueueState() {
        int currentQueuePos = lobby.getQueueState().ordinal();

        QueueState[] states = QueueState.values();

        if (currentQueuePos >= states.length - 1)
            currentQueuePos = 0;
        else
            currentQueuePos++;

        lobby.setQueueState(states[currentQueuePos]);
        lobbyRepository.save(lobby);

        return states[currentQueuePos];
    }

    public void startCurrentSong() {
        if (lobby.getSongsList().size() < 1)
            return;

        System.out.println("Смена текущей песни на "+lobby.getSongsList().get(0).getSong().getName());
        lobby.setCurrentSong(getSongsListAsHashMap().get(0l));
        lobby.setTime(0l);
        lobby.setMaxTime(getDurationSong(lobby.getCurrentSong().getSong()));

        lobbyRepository.save(lobby);
    }

    private void setCurrentSongByQueue(long queue) {
        HashMap<Long, SongInQueue> songsList = getSongsListAsHashMap();

        if (songsList.get(queue) == null)
            return;

        lobby.setCurrentSong(songsList.get(queue));
        lobbyRepository.save(lobby);
    }

    public void setCurrentSongFromQueue(long songInQueueId) {
        Optional<SongInQueue> songInQueueOptional = songInQueueRepository.findById(songInQueueId);

        if (songInQueueOptional.isEmpty() || songInQueueOptional.get().getLobbyId() != lobby.getId()) {
            System.out.println("Неверно задана новая песня!");
            return;
        }

        System.out.println("Смещение всей очереди на "+(-songInQueueOptional.get().getQueuePosition()));
        offsetAllQueue(-songInQueueOptional.get().getQueuePosition());

        startCurrentSong();
    }

    // ОПАСНО!!!
    private void offsetAllQueue(long offsetSize) {
        for (int i = 0; i < lobby.getSongsList().size(); i++) {
            SongInQueue songInQueueToMove = lobby.getSongsList().get(i);
            songInQueueToMove.setQueuePosition(songInQueueToMove.getQueuePosition() + offsetSize);

            songInQueueRepository.save(songInQueueToMove);
        }
    }

    private void nextSong() {
        System.out.println("Запущено переключение песни...");
        isSongEnded = true;

        HashMap<Long, SongInQueue> songInQueueHashMap = getSongsListAsHashMap();

        if (lobby.getQueueState() == QueueState.REPEAT_ONE) {
            startCurrentSong();
            return;
        }

        if (songInQueueHashMap.get(1l) == null) {
            switch (lobby.getQueueState()) {
                case NO_REPEAT: {
                    lobby.setPlaying(false);
                    this.isQueueEmpty = true;
                    System.out.println("Следующей песни в очереди нет...");
                    break;
                }
                case REPEAT_ALL: {
                    offsetAllQueue(-lobby.getSongsList().get(0).getQueuePosition());
                    startCurrentSong();
                    break;
                }
            }
            lobbyRepository.save(lobby);
            return;
        }

        System.out.println("Смещение всех песен в позиции на -1...");
        songInQueueHashMap.forEach((aLong, songInQueue) -> {
            System.out.println("Песня "+songInQueue.getId()+" смещена: "+songInQueue.getQueuePosition()+" -> "+(songInQueue.getQueuePosition()-1));
            songInQueue.setQueuePosition(songInQueue.getQueuePosition() - 1);

            if (songInQueue.getQueuePosition() < -historySaveSize)
                songInQueueRepository.delete(songInQueue);
            else
                songInQueueRepository.save(songInQueue);

            if (songInQueue.getQueuePosition() == 0)
                lobby.setCurrentSong(songInQueue);
        });

        System.out.println("Смена песни на "+lobby.getCurrentSong().getId());

        lobby.setMaxTime(getDurationSong(lobby.getCurrentSong().getSong()));
        lobby.setTime(0l);

        lobbyRepository.save(lobby);
    }

    private void prevSong() {
        System.out.println("Запущено переключение песни...");
        isSongEnded = true;

        HashMap<Long, SongInQueue> songInQueueHashMap = getSongsListAsHashMap();

        if (songInQueueHashMap.get(-1l) == null) {
            System.out.println("Предыдущей песни в очереди нет...");
            return;
        }

        System.out.println("Смещение всех песен в позиции на 1...");
        songInQueueHashMap.forEach((aLong, songInQueue) -> {
            System.out.println("Песня "+songInQueue.getId()+" смещена: "+songInQueue.getQueuePosition()+" -> "+(songInQueue.getQueuePosition()+1));
            songInQueue.setQueuePosition(songInQueue.getQueuePosition() + 1);

            if (songInQueue.getQueuePosition() == 0)
                lobby.setCurrentSong(songInQueue);
        });

        System.out.println("Смена песни на "+lobby.getCurrentSong().getId());

        lobby.setMaxTime(getDurationSong(lobby.getCurrentSong().getSong()));
        lobby.setTime(0l);

        lobbyRepository.save(lobby);
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

    private long getNextQueuePosition() {
        if (lobby.getSongsList().size() == 0)
            return 1;

        return lobby.getSongsList().get(lobby.getSongsList().size()-1).getQueuePosition()+1;
    }

    public void skipSong() {
        System.out.println("В лобби "+lobby.getCode()+" пропущена песня "+lobby.getCurrentSong().getSong().getName());

        nextSong();
    }

    public void backwardSong() {
        if (lobby.getTime() > 5000) {
            lobby.setTime(0l);
            lobbyRepository.save(lobby);
            return;
        }

        prevSong();
    }

    public void addSongToList(Song song) {
        System.out.println("В лобби "+lobby.getCode()+" добавлена новая песня "+song.getName()+". Позиция в очереди: "+(getNextQueuePosition()));

        SongInQueue songInQueue = new SongInQueue();

        songInQueue.setSong(song);
        songInQueue.setLobbyId(lobby.getId());
        songInQueue.setQueuePosition(getNextQueuePosition());

        songInQueueRepository.save(songInQueue);
    }

    public void deleteSongFromList(long songInQueueId) {
        int songInQueueToDeleteIndex = -1;

        for (int i = 0; i < lobby.getSongsList().size(); i++) {
            if (lobby.getSongsList().get(i).getId() == songInQueueId)
                songInQueueToDeleteIndex = i;
        }

        if (songInQueueToDeleteIndex == -1) {
            System.out.println("Такой песни в ротации нет.");
            return;
        }

        for (int i = songInQueueToDeleteIndex + 1; i < lobby.getSongsList().size(); i++) {
            SongInQueue songInQueueToMove = lobby.getSongsList().get(i);
            songInQueueToMove.setQueuePosition(songInQueueToMove.getQueuePosition() - 1);

            songInQueueRepository.save(songInQueueToMove);
        }

        songInQueueRepository.delete(songInQueueRepository.findById(songInQueueId).get());
    }

    private HashMap<Long, SongInQueue> getSongsListAsHashMap() {
        HashMap<Long, SongInQueue> songInQueueHashMap = new HashMap<>();

        for (SongInQueue songInQueue : lobby.getSongsList()) {
            songInQueueHashMap.put(songInQueue.getQueuePosition(), songInQueue);
        }

        return songInQueueHashMap;
    }

    public void moveTimeLine(Long time) {
        if (time > lobby.getMaxTime())
            return;

        if (time < 0)
            return;

        if (lobby.getCurrentSong() == null)
            return;

        lobby.setTime(time);
        lobbyRepository.save(lobby);
    }

    public boolean isSongEnded() {
        return isSongEnded;
    }

    public void setSongEnded(boolean songEnded) {
        isSongEnded = songEnded;
    }

    public boolean isQueueEmpty() {
        return isQueueEmpty;
    }

    public void setQueueEmpty(boolean queueEmpty) {
        isQueueEmpty = queueEmpty;
    }
}
