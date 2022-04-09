package com.tcz.listentogether.repo;

import com.tcz.listentogether.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByToken(String token);

    Optional<User> findByName(String name);

    Optional<User> findBySimpSessionId(String simpSessionId);

    Iterable<User> findAllByLobbyId(long lobbyId);
}
