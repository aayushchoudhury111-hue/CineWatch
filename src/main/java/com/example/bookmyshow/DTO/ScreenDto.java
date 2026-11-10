package com.example.bookmyshow.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenDto {
    private Long id;
    private String name;
    private Integer totalSeats;
    private TheatreDto theatre;
}

