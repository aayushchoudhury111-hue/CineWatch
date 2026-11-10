package com.example.bookmyshow.Repository;

import com.example.bookmyshow.Entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByScreenScreenIdOrderBySeatNumber(Long screenId);
}
