package com.example.bookmyshow.Service;

import com.example.bookmyshow.DTO.TheatreDto;
import com.example.bookmyshow.Entity.Theatre;
import com.example.bookmyshow.Exception.ResourceNotFoundException;
import com.example.bookmyshow.Repository.TheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TheatreService {

    @Autowired
    private TheatreRepository theatreRepository;

    public TheatreDto createTheatreDto(TheatreDto theatreDto) {
        Theatre theatre = mapToEntity(theatreDto);
        Theatre savedTheatre = theatreRepository.save(theatre);
        return mapToDto(savedTheatre);
    }

    public TheatreDto getTheatreById(Long id) {
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre Not Found with id : " + id));
        return mapToDto(theatre);
    }

    public List<TheatreDto> getAllTheatreDto() {
        List<Theatre> theatres= theatreRepository.findAll();
        return theatres.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<TheatreDto> getAllTheatreByCity(String city) {
        List<Theatre> theatres= theatreRepository.findByCity(city);
        return theatres.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // update Theatre

//    public TheatreDto updateTheatre(Long id, TheatreDto theatreDto){
//        Theatre theatre = theatreRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Theatre Not Found with id : " + id));
//        theatre.setId(theatreDto.getId());
//        theatre.setName(theatreDto.getName());
//        theatre.setCity(theatreDto.getCity());
//        theatre.setAddress(theatreDto.getAddress());
//        theatre.setTotalScreen(theatreDto.getTotalScreens());
//
//
//    }
//    // delete Theatre


    private Theatre mapToEntity(TheatreDto theatreDto) {
        Theatre theatre = new Theatre();
//        theatre.setId(theatreDto.getId());
        theatre.setName(theatreDto.getName());
        theatre.setAddress(theatreDto.getAddress());
        theatre.setCity(theatreDto.getCity());
        theatre.setTotalScreen(theatreDto.getTotalScreens());
        return theatre;
    }

    private TheatreDto mapToDto(Theatre theatre) {
        TheatreDto theatreDto = new TheatreDto();
        theatreDto.setName(theatre.getName());
        theatreDto.setAddress(theatre.getAddress());
        theatreDto.setCity(theatre.getCity());
        theatreDto.setTotalScreens(theatre.getTotalScreen());
        theatreDto.setId(theatre.getId());
        return theatreDto;
    }
}
