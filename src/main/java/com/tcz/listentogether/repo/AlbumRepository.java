package com.tcz.listentogether.repo;

import com.tcz.listentogether.models.Album;
import com.tcz.listentogether.models.Author;
import com.tcz.listentogether.models.Song;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface AlbumRepository extends CrudRepository<Album, Long> {

    Optional<Album> findByName(String name);
}
