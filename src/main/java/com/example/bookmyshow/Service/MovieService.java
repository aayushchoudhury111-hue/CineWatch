package com.example.bookmyshow.Service;

import com.example.bookmyshow.DTO.MovieDto;
import com.example.bookmyshow.Entity.Movie;
import com.example.bookmyshow.Exception.ResourceNotFoundException;
import com.example.bookmyshow.Repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public MovieDto createMovie(MovieDto movieDto) {
        Movie movie = mapToEntity(movieDto);
        Movie saveMovie = movieRepository.save(movie);
        return mapToDto(saveMovie);
    }

    public Movie mapToEntity(MovieDto movieDto) {
        Movie movie = new Movie();
        movie.setTitle(movieDto.getTitle());
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setDescription(movieDto.getDescription());
        movie.setLanguage(movieDto.getLanguage());
        movie.setGenre(movieDto.getGenre());
        movie.setDuration(movieDto.getDurationMins());
        movie.setPosterUrl(movieDto.getPosterUrl());
        return movie;
    }

    private MovieDto mapToDto(Movie movie) {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(movie.getId());
        movieDto.setTitle(movie.getTitle());
        movieDto.setReleaseDate(movie.getReleaseDate());
        movieDto.setDescription(movie.getDescription());
        movieDto.setLanguage(movie.getLanguage());
        movieDto.setGenre(movie.getGenre());
        movieDto.setDurationMins(movie.getDuration());
        movieDto.setPosterUrl(movie.getPosterUrl());
        return movieDto;
    }

    public MovieDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        return mapToDto(movie);
    }

    public List<MovieDto> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream()
                .map(this::mapToDto)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<MovieDto> getMoviesByLanguage(String Language){
        List<Movie> movies = movieRepository.findByLanguage(Language);
        return movies.stream().map(this::mapToDto).collect(Collectors.toUnmodifiableList());
    }

    public List<MovieDto> getMoviesByGenre(String Genre){
        List<Movie> movies = movieRepository.findByGenre(Genre);
        return movies.stream().map(this::mapToDto).collect(Collectors.toUnmodifiableList());
    }

    public List<MovieDto> searchMoviesByTitle(String Title){
        List<Movie> movies = movieRepository.findByTitleContainingIgnoreCase(Title);
        return movies.stream().map(this::mapToDto).collect(Collectors.toUnmodifiableList());
    }

    public MovieDto updateMovie(Long id, MovieDto movieDto) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        movie.setTitle(movieDto.getTitle());
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setDescription(movieDto.getDescription());
        movie.setLanguage(movieDto.getLanguage());
        movie.setGenre(movieDto.getGenre());
        movie.setDuration(movieDto.getDurationMins());
        movie.setPosterUrl(movieDto.getPosterUrl());

        Movie updateMovie = movieRepository.save(movie);
        return mapToDto(updateMovie);
    }

    public MovieDto deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        movieRepository.delete(movie);
        return mapToDto(movie);
    }
}
