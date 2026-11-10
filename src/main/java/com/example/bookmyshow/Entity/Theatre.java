package com.example.bookmyshow.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "theatre")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Theatre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    private String address;

    private String city;

    private Integer totalScreen;


    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL)
    private List<Screen> screen;
}
