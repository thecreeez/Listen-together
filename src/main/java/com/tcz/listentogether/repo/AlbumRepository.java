package com.tcz.listentogether.repo;

import com.tcz.listentogether.models.Album;
import com.tcz.listentogether.models.Author;
import com.tcz.listentogether.models.Song;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumRepository extends CrudRepository<Album, Long> {

    Optional<Album> findByName(String name);
}
