package com.tcz.listentogether.repo;

import com.tcz.listentogether.models.Lobby;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface LobbyRepository extends CrudRepository<Lobby, Long> {

    Optional<Lobby> findByCode(String code);
}
