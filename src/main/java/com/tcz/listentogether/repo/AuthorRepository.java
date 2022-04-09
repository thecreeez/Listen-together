package com.tcz.listentogether.repo;

import com.tcz.listentogether.models.Author;
import com.tcz.listentogether.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface AuthorRepository extends CrudRepository<Author, Long> {

    Optional<Author> findByName(String name);

}
