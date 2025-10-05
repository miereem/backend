package org.example.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Car {
    @Column(name = "car_name")
    private String name; //Поле не может быть null

    private Boolean cool; //Поле не может быть null
}
