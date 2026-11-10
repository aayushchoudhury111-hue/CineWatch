package com.example.bookmyshow.Repository;

import com.example.bookmyshow.Entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByGenre(String genre);

    List<Movie> findByLanguage(String language);

    List<Movie> findByTitle(String title);
    List<Movie> findByTitleContainingIgnoreCase(String title);
}
