package com.example.bookmyshow.Repository;

import com.example.bookmyshow.Entity.Screen;
import com.example.bookmyshow.Entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    List<Show> findByMovieId(Long  movieId);
    List<Show> findByScreenScreenId(Long  screenId);

    List<Show> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Show> findByMovie_IdAndScreen_Theatre_City(Long movieId, String city);
}
