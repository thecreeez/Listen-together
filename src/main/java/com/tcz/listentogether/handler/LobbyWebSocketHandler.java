package com.tcz.listentogether.handler;

import com.tcz.listentogether.CodeRandomizer;
import com.tcz.listentogether.LobbyAudioController;
import com.tcz.listentogether.enums.UserState;
import com.tcz.listentogether.models.Lobby;
import com.tcz.listentogether.models.Song;
import com.tcz.listentogether.models.User;
import com.tcz.listentogether.repo.*;
import com.tcz.listentogether.websockets.UserConnection;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.persistence.Lob;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

/**
 * DONE:
 *      Подключение к лобби ({conn/disc}//user.name//user.state//user.id)
 *
 *      Example:
 *          conn//Billy//null//0 - Билли подключился к вашему лобби и пока ниче не делает
 *
 * TODO:
 *      New:
 *          connection//{connect/disconnect}//user.id//user.name//user.state
 *
 *
 * TODO:
 *      Управление лобби (control//{play/stop/next/prev/set/movetime/repeat/unrepeat}//{params if need})
 *
 *      Example:
 *          control//play - Продолжить проигрывание
 *          control//stop - Остановить проигрывание
 *          control//move_time//long time - Переместить песню на выбранное время
 *
 *          control//drop_song//id SongInQueue - Убрать песню из очереди
 *          control//add_song//id Song - Добавить песню в очередь
 *          control//move_song//id SongInQueue//position - Переместить песню в очереди
 *
 *          control//next_song - Пропустить текущую песню
 *          control//prev_song - Перейти на песню назад (Если timing > 3000мс то переместить на начало песни)
 *
 *          control//set_current_song//type = {in_queue/not_in_queue}//
 *              * Если in_queue то очередь песен останется.
 *              * Если not_in_queue то очередь песен превратится в то где запускали (При запуске альбома запустится альбом, при трека - трек)
 *          Если not_in_queue то
 *              //typeSet = {song / album}//id Song
 *          Если in_queue
 *              //id SongInQueue
 *
 *
 *          control//repeat - Включить режим повтора
 *          control//repeat_one - Включить режим повтора одной песни
 *          control//unrepeat - Выключить режим повтора
 *
 *          control//close - (додумать как выдавать админки) Закрывает лобби
 *          control//open - (додумать как выдавать админки) Открывает лобби
 *
 * TODO:
 *      Уведомление сервера о состоянии пользователя (notify//{mute_song/unmute_song})
 *
 *      Example:
 *          notify//mute_song - Полностью замутить песню
 *          notify//unmute_song - Полностью размутить песню
 */

@Controller
public class LobbyWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public LobbyRepository lobbyRepository;

    @Autowired
    public SongInQueueRepository songInQueueRepository;

    @Autowired
    public SongRepository songRepository;

    @Autowired
    public AlbumRepository albumRepository;

    @Autowired
    public AuthorRepository authorRepository;

    private static HashMap<String, WebSocketSession> webSocketSessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        String token = getToken(session);

        Optional<User> user = userRepository.findByToken(token);

        if (user.isEmpty()) {
            //session.close();
            return;
        }

        user.get().setState(UserState.LISTENING);
        user.get().setSimpSessionId(session.getId());
        userRepository.save(user.get());
        webSocketSessions.put(session.getId(), session);

        if (user.get().getLobby() != null) {
            Lobby lobby = user.get().getLobby();

            UserConnection userConnection = new UserConnection(user.get().getName(), user.get().getState(), user.get().getId());
            sendToLobby(user.get().getLobby().getId(), "userState//"+userConnection.toString());
        }

        checkSocketConnections();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception, IOException {
        super.handleTextMessage(session, message);

        Optional<User> user = userRepository.findByToken(getToken(session));

        if (user.isEmpty()) {
            System.out.println("Пользователь не найден... Отключение от сервера!");
            session.close();
            return;
        }

        String[] args = message.getPayload().split("//");


        handleCommand(args, session, user.get());

        return;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        Optional<User> user =  userRepository.findByToken(getToken(session));

        if (!user.isEmpty()) {
            webSocketSessions.remove(session.getId());

            user.get().setSimpSessionId(null);
            user.get().setState(UserState.DISCONNECTED);

            userRepository.save(user.get());

            if (user.get().getLobby() != null) {
                UserConnection userConnection = new UserConnection(user.get().getName(), user.get().getState(), user.get().getId());
                sendToLobby(user.get().getLobby().getId(), "userState//"+userConnection.toString());
            }
        }
    }

    private void handleCommand(String[] args, WebSocketSession session, User user) throws IOException {
        String command = args[0];

        switch (command) {
            case "conn": {
                if (args.length == 1) {
                    Lobby newLobby = new Lobby();

                    String code = CodeRandomizer.random();

                    while (!lobbyRepository.findByCode(code).isEmpty()) {
                        code = CodeRandomizer.random();
                    }

                    System.out.println("Создание нового лобби для "+user.getName()+". Код лобби: "+code);

                    newLobby.setCode(code);
                    lobbyRepository.save(newLobby);

                    if (user.getLobby() != null) {
                        Lobby lobby = user.getLobby();

                        Iterable<User> usersIterator = userRepository.findAllByLobbyId(lobby.getId());
                        List<User> users = new ArrayList<>();
                        usersIterator.forEach(users::add);

                        System.out.println(users.size());

                        if (users.size() <= 1) {
                            System.out.println("Удаление лобби "+lobby.getCode()+". Причина: Последний пользователь вышел из лобби.");
                            lobbyRepository.delete(lobby);
                        }
                    }

                    user.setLobby(lobbyRepository.findById(newLobby.getId()).get());
                    userRepository.save(user);

                    session.sendMessage(new TextMessage("redir//to:/lobby/"+code));
                    return;
                }

                String code = args[1];

                Optional<Lobby> lobby = lobbyRepository.findByCode(code);

                if (lobby.isEmpty()) {
                    System.out.println("Лобби не найдено!");

                    //Прислать сообщение о том что лобби не найдено
                    return;
                }

                System.out.println("Пользователь "+user.getName()+" подключается к лобби с кодом "+code+"...");

                user.setLobby(lobby.get());
                userRepository.save(user);

                UserConnection userConnection = new UserConnection(user.getName(), user.getState(), user.getId());
                sendToLobby(lobby.get().getId(), "conn//"+userConnection.toString());
                break;
            }
            case "disc": {
                System.out.println("Пользователь "+user.getName()+" выходит из лобби...");

                if (user.getLobby() == null) {
                    System.out.println("Нельзя выйти из лобби, пользователь и так в нем не находится");
                    return;
                }

                Lobby lobby = user.getLobby();

                user.setLobby(null);
                userRepository.save(user);

                UserConnection userConnection = new UserConnection(user.getName(), user.getState(), user.getId());
                sendToLobby(lobby.getId(), "disc//"+userConnection.toString());
                break;
            }
            case "pcontrol": {
                Lobby lobby = user.getLobby();

                if (lobby == null)
                    return;

                handlePlayerCommand(args, session, user, lobby);
                break;
            }
            default: {
                System.out.println("Ошибка парсинга команды: Получено:" + command);
                return;
            }
        }
    }

    private void handlePlayerCommand(String[] args, WebSocketSession session, User user, Lobby lobby) throws IOException {
        String command = args[1];

        LobbyAudioController lobbyAudioController = new LobbyAudioController(lobby, lobbyRepository, songInQueueRepository);

        switch (command) {
            case "playstop": {
                lobbyAudioController.playstop();
                sendToLobby(lobby.getId(), "player//isPlaying//"+lobby.isPlaying());
                break;
            }
            case "skipsong": {
                lobbyAudioController.skipSong();
                sendToLobby(lobby.getId(), "player//skipSong");
                break;
            }
            case "addSongToList": {
                try {
                    long songId = Long.valueOf(args[2]);

                    Optional<Song> song = songRepository.findById(songId);

                    if (!song.isEmpty())
                        lobbyAudioController.addSongToList(song.get());
                    else
                        System.out.println("Попытка добавить несуществующую песню...");
                } catch(NumberFormatException e) {
                    System.out.println("Неверно введена команда добавления песни. id не указан в формате числа.");
                }
            }
        }
    }

    private void sendToLobby(long lobbyId, String message) throws IOException {
        Iterable<User> usersInLobby = userRepository.findAllByLobbyId(lobbyId);

        for (User user : usersInLobby) {
            if (webSocketSessions.containsKey(user.getSimpSessionId())) {
                webSocketSessions.get(user.getSimpSessionId()).sendMessage(new TextMessage(message));
            }
        }
    }

    private String getToken(WebSocketSession session) {
        if (session.getHandshakeHeaders().get("cookie") == null)
            return "null";

        String[] cookies = session.getHandshakeHeaders().get("cookie").get(0).split("; ");

        String token = "";

        for (String cookie : cookies) {
            String[] args = cookie.split("=");

            if (args.length == 1)
                return "null";

            switch (args[0]) {
                case "token": token = args[1]; break;
                default: {
                    //System.out.println("useless cookie: "+args[0]);
                }
            }
        }

        return token;
    }

    @Scheduled(fixedRate = 500L)
    void updateLobbyTime() {
        Iterable<Lobby> lobbies = lobbyRepository.findAll();

        lobbies.forEach(lobby -> {
            if (lobby.isPlaying() && lobby.getCurrentSong() != null) {
                LobbyAudioController lobbyAudioController = new LobbyAudioController(lobby, lobbyRepository, songInQueueRepository);

                boolean isBeforePlaying = lobby.isPlaying();
                boolean isBeforeSongQueueEnded = lobbyAudioController.isSongEnded();

                lobbyAudioController.update();

                try {
                    if (isBeforePlaying != lobby.isPlaying())
                        sendToLobby(lobby.getId(), "player//isPlaying//"+lobby.isPlaying());

                    if (lobbyAudioController.isSongEnded()) {
                        String arg = lobbyAudioController.isQueueEmpty() ? "nextSongEmpty" : "nextSongReady";

                        sendToLobby(lobby.getId(), "player//songEnded//"+arg);
                    }

                    sendToLobby(lobby.getId(), "player//sync//"+lobby.getTime());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkSocketConnections() throws IOException {
        /*Iterable<User> usersInLobby = userRepository.findAll();

        for (User user : usersInLobby) {
            if (user.getLobbyId() != null && !webSocketSessions.containsKey(user.getSimpSessionId())) {
                UserConnection userConnection = new UserConnection(user.getName(), user.getState(), user.getId());
                sendToLobby(user.getLobbyId(), "disc//"+userConnection.toString());

                user.setLobbyId(null);
                user.setSimpSessionId(null);

                userRepository.save(user);
            }
        }*/
    }
}
