package com.example.bookmyshow.Service;

import com.example.bookmyshow.DTO.*;
import com.example.bookmyshow.Entity.Movie;
import com.example.bookmyshow.Entity.Screen;
import com.example.bookmyshow.Entity.Show;
import com.example.bookmyshow.Entity.ShowSeat;
import com.example.bookmyshow.Exception.ResourceNotFoundException;
import com.example.bookmyshow.Repository.MovieRepository;
import com.example.bookmyshow.Repository.ScreenRepository;
import com.example.bookmyshow.Repository.ShowRepository;
import com.example.bookmyshow.Repository.ShowSeatRepository;
import com.example.bookmyshow.Repository.SeatRepository;
import com.example.bookmyshow.Entity.Seat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShowService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;
    @Autowired
    private SeatRepository seatRepository;

    @Transactional
    public ShowDto createShow(ShowDto showDto) {
        Show show = new Show();
        Movie movie = movieRepository.findById(showDto.getMovie().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + showDto.getMovie().getId()));

        Screen screen = screenRepository.findById(showDto.getScreen().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + showDto.getScreen().getId()));

        show.setMovie(movie);
        show.setScreen(screen);
        show.setStartTime(showDto.getStartTime());
        show.setEndTime(showDto.getEndTime());

        if (showDto.getStartTime() == null || showDto.getEndTime() == null || !showDto.getEndTime().isAfter(showDto.getStartTime())) {
            throw new IllegalArgumentException("Show end time must be after its start time");
        }
        Show savedShow = showRepository.save(show);
        List<ShowSeat> showSeats = ensureShowSeats(savedShow);
        return mapToDto(savedShow, showSeats);
    }

    public ShowDto getShowById(Long id){
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));
        return mapToDto(show, ensureShowSeats(show));
    }

    public List<ShowDto> getAllShows(){
        List<Show> shows = showRepository.findAll();
        return shows.stream()
                .map(show -> {
                    return mapToDto(show, ensureShowSeats(show));
                }).collect(Collectors.toList());
    }

    public List<ShowDto> getShowsByMovie(Long movieId){
        List<Show> shows = showRepository.findByMovieId(movieId);
        return shows.stream()
                .map(show -> {
                    return mapToDto(show, ensureShowSeats(show));
                })
                .collect(Collectors.toList());
    }

    public List<ShowDto> getShowsByMovieAndCity(Long movieId, String city){
        List<Show> shows = showRepository.findByMovie_IdAndScreen_Theatre_City(movieId, city);
        return shows.stream()
                .map(show -> {
                    return mapToDto(show, ensureShowSeats(show));
                })
                .collect(Collectors.toList());
    }

    public List<ShowDto> getShowsByDateRange(LocalDateTime startDate, LocalDateTime endDate){
        List<Show> shows = showRepository.findByStartTimeBetween(startDate, endDate);
        return shows.stream()
                .map(show -> {
                    return mapToDto(show, ensureShowSeats(show));
                })
                .collect(Collectors.toList());
    }

    private ShowDto mapToDto(Show show, List<ShowSeat> availableSeats) {
        ShowDto showDto = new ShowDto();
        showDto.setId(show.getId());
        showDto.setEndTime(show.getEndTime());
        showDto.setStartTime(show.getStartTime());
        showDto.setMovie(new MovieDto(
                show.getMovie().getId(),
                show.getMovie().getTitle(),
                show.getMovie().getDescription(),
                show.getMovie().getLanguage(),
                show.getMovie().getGenre(),
                show.getMovie().getDuration(),
                show.getMovie().getReleaseDate(),
                show.getMovie().getPosterUrl()
        ));

        TheatreDto theatreDto = new TheatreDto(
                show.getScreen().getTheatre().getId(),
                show.getScreen().getTheatre().getName(),
                show.getScreen().getTheatre().getAddress(),
                show.getScreen().getTheatre().getCity(),
                show.getScreen().getTheatre().getTotalScreen()
        );

        showDto.setScreen(new ScreenDto(
                show.getScreen().getScreenId(),
                show.getScreen().getName(),
                show.getScreen().getTotalSeats(),
                theatreDto

        ));
        List<ShowSeatDto> seatDtos = availableSeats.stream()
                .map(seat -> {
                    ShowSeatDto seatDto = new ShowSeatDto();
                    seatDto.setId(seat.getId());
                    seatDto.setStatus(seat.getStatus());
                    seatDto.setPrice(seat.getPrice());

                    SeatDto baseSeatDto = new SeatDto();
                    baseSeatDto.setId(seat.getSeat().getId());
                    baseSeatDto.setSeatNumber(seat.getSeat().getSeatNumber());
                    baseSeatDto.setSeatType(seat.getSeat().getSeatType());
                    baseSeatDto.setBasePrice(seat.getSeat().getBasePrice());
                    seatDto.setSeat(baseSeatDto);
                    return seatDto;
                }).collect(Collectors.toUnmodifiableList());
        showDto.setSeats(seatDtos);
        showDto.setAvailableSeat(seatDtos.stream().filter(seat -> "AVAILABLE".equals(seat.getStatus())).toList());
        return showDto;
    }

    private List<ShowSeat> ensureShowSeats(Show show) {
        List<ShowSeat> current = showSeatRepository.findByShow_Id(show.getId());
        List<Seat> screenSeats = seatRepository.findByScreenScreenIdOrderBySeatNumber(show.getScreen().getScreenId());
        if (screenSeats.isEmpty()) return current;

        java.util.Set<Long> existingSeatIds = current.stream().map(item -> item.getSeat().getId()).collect(Collectors.toSet());
        List<ShowSeat> missing = screenSeats.stream().filter(seat -> !existingSeatIds.contains(seat.getId()))
                .map(seat -> {
                    ShowSeat showSeat = new ShowSeat();
                    showSeat.setShow(show);
                    showSeat.setSeat(seat);
                    showSeat.setStatus("AVAILABLE");
                    showSeat.setPrice(seat.getBasePrice());
                    return showSeat;
                }).toList();
        if (!missing.isEmpty()) current.addAll(showSeatRepository.saveAll(missing));
        return current;
    }
}
