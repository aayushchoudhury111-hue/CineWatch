package com.example.bookmyshow.Repository;

import com.example.bookmyshow.Entity.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ShowSeat s where s.Id in :ids")
    List<ShowSeat> findAllForUpdateByIdIn(@Param("ids") List<Long> ids);

    List<ShowSeat> findByShow_Id(Long movieId);

    List<ShowSeat> findByShowIdAndStatus(Long showId, String status);
}
