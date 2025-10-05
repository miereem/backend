package org.example.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.model.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class HumanBeingDto {
    private Long id;

    @NotBlank(message = "Name cannot be null or empty")
    @Size(min = 1, message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Coordinates cannot be null")
    @Valid
    private Coordinates coordinates;

    private LocalDateTime creationDate;

    private boolean realHero;

    private Boolean hasToothpick;

    @Valid
    private Car car;

    private Mood mood;

    @DecimalMax(value = "664.0", message = "Impact speed cannot exceed 664")
    private double impactSpeed;

    @NotBlank(message = "Soundtrack name cannot be null or empty")
    @Size(min = 1, message = "Soundtrack name cannot be empty")
    private String soundtrackName;

    @NotNull(message = "Weapon type cannot be null")
    private WeaponType weaponType;

    public static HumanBeing convertFromDto(HumanBeingDto humanBeingDto) {
        return HumanBeing.builder()
                .name(humanBeingDto.getName())
                .coordinates(humanBeingDto.getCoordinates())
                .creationDate(humanBeingDto.getCreationDate())
                .realHero(humanBeingDto.isRealHero())
                .hasToothpick(humanBeingDto.getHasToothpick())
                .car(humanBeingDto.getCar())
                .mood(humanBeingDto.getMood())
                .impactSpeed(humanBeingDto.getImpactSpeed())
                .soundtrackName(humanBeingDto.getSoundtrackName())
                .weaponType(humanBeingDto.getWeaponType())
                .build();
    }
}