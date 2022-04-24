package com.tcz.listentogether.repo;

import com.tcz.listentogether.models.Album;
import com.tcz.listentogether.models.Author;
import com.tcz.listentogether.models.Song;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRepository extends CrudRepository<Song, Long> {

    Optional<Song> findByName(String name);
}
