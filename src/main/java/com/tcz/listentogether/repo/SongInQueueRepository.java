package com.tcz.listentogether.repo;

import com.tcz.listentogether.models.Song;
import com.tcz.listentogether.models.SongInQueue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongInQueueRepository extends CrudRepository<SongInQueue, Long> {
}