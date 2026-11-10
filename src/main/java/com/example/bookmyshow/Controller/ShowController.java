package com.example.bookmyshow.Controller;

import com.example.bookmyshow.DTO.ShowDto;
import com.example.bookmyshow.Service.MovieService;
import com.example.bookmyshow.Service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
    @RequestMapping("/api/shows")
public class ShowController {

    @Autowired
    private ShowService showService;
    @Autowired
    private MovieService movieService;

    @PostMapping
    public ResponseEntity<ShowDto> createShow(@RequestBody ShowDto showDto) {
        return new ResponseEntity<>(showService.createShow(showDto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowDto> getShowById(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShowById(id));
    }

    @GetMapping
    public ResponseEntity<List<ShowDto>> getAllShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }

    @GetMapping("/movie/{id}")
    public ResponseEntity<List<ShowDto>> getShowByMovies(@PathVariable Long id) {
        return ResponseEntity.ok(showService.getShowsByMovie(id));
    }

    @GetMapping("/movie/{id}/city")
    public ResponseEntity<List<ShowDto>> getShowByMoviesCity(@PathVariable Long id, @RequestParam String city) {
        return ResponseEntity.ok(showService.getShowsByMovieAndCity(id, city));
    }

    @GetMapping("/date")
    public ResponseEntity<List<ShowDto>> getShowByDate(@RequestParam LocalDateTime startDate , @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(showService.getShowsByDateRange(startDate, endDate));
    }
}
