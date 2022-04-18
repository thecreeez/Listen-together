package com.tcz.listentogether.handler;

import com.tcz.listentogether.enums.UserState;
import com.tcz.listentogether.models.Lobby;
import com.tcz.listentogether.models.User;
import com.tcz.listentogether.repo.LobbyRepository;
import com.tcz.listentogether.repo.UserRepository;
import com.tcz.listentogether.websockets.UserConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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

    private HashMap<String, WebSocketSession> webSocketSessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        String token = getToken(session);

        Optional<User> user = userRepository.findByToken(token);

        if (user.isEmpty()) {
            session.close();
            return;
        }

        user.get().setState(UserState.LISTENING);
        user.get().setSimpSessionId(session.getId());
        userRepository.save(user.get());
        webSocketSessions.put(session.getId(), session);

        if (user.get().getLobbyId() != null) {
            Optional<Lobby> optionalLobby = lobbyRepository.findById(user.get().getLobbyId());

            if (!optionalLobby.isEmpty()) {
                UserConnection userConnection = new UserConnection(user.get().getName(), user.get().getState(), user.get().getId());
                sendToLobby(user.get().getLobbyId(), "userState//"+userConnection.toString());
            }
        }

        checkSocketConnections();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception, IOException {
        super.handleTextMessage(session, message);
        String code = message.getPayload();

        System.out.println("tm is getted!!!!");

        Optional<User> user = userRepository.findByToken(getToken(session));

        if (user.isEmpty()) {
            session.close();
            return;
        }

        Optional<Lobby> lobby = lobbyRepository.findByCode(code);

        if (lobby.isEmpty()) {
            System.out.println("Лобби пустое!");
            session.close();
            return;
        }

        user.get().setLobbyId(lobby.get().getId());
        userRepository.save(user.get());

        UserConnection userConnection = new UserConnection(user.get().getName(), user.get().getState(), user.get().getId());
        sendToLobby(lobby.get().getId(), "conn//"+userConnection.toString());

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

            UserConnection userConnection = new UserConnection(user.get().getName(), user.get().getState(), user.get().getId());
            sendToLobby(user.get().getLobbyId(), "userState//"+userConnection.toString());
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

            switch (args[0]) {
                case "token": token = args[1]; break;
                default: {
                    //System.out.println("useless cookie: "+args[0]);
                }
            }
        }

        return token;
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
