package com.tcz.listentogether.repo;

import com.tcz.listentogether.models.Album;
import com.tcz.listentogether.models.Author;
import com.tcz.listentogether.models.Song;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface SongRepository extends CrudRepository<Song, Long> {

    Optional<Song> findByName(String name);
}
