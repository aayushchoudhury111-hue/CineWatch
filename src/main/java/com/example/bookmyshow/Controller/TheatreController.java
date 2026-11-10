package com.example.bookmyshow.Controller;

import com.example.bookmyshow.DTO.TheatreDto;
import com.example.bookmyshow.Service.TheatreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/theatres", "/theatre"})
public class TheatreController {

    @Autowired
    private TheatreService theatreService;

    @PostMapping("/create")
    public ResponseEntity<TheatreDto> createTheatre(@RequestBody TheatreDto theatreDto) {
        return ResponseEntity.ok(theatreService.createTheatreDto(theatreDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheatreDto> getTheatreById(@PathVariable Long id) {
        return ResponseEntity.ok(theatreService.getTheatreById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TheatreDto>> getAllTheatre() {
        return ResponseEntity.ok(theatreService.getAllTheatreDto());
    }

    @GetMapping("/city")
    public ResponseEntity<List<TheatreDto>> getAllTheatreByCity(@RequestParam String city) {
        return ResponseEntity.ok(theatreService.getAllTheatreByCity(city));
    }

//    @GetMapping("/")
}
