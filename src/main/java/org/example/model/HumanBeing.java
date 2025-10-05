package org.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "human_beings")
@NoArgsConstructor
@AllArgsConstructor
public class HumanBeing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be null or empty")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Coordinates cannot be null")
    @Embedded
    private Coordinates coordinates;

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "real_hero")
    private boolean realHero;

    @Column(name = "has_toothpick")
    private Boolean hasToothpick;

    @Embedded
    private Car car;

    @Enumerated(EnumType.STRING)
    private Mood mood;

    @DecimalMax(value = "664.0", message = "Impact speed cannot exceed 664")
    @Column(name = "impact_speed")
    private double impactSpeed;

    @NotBlank(message = "Soundtrack name cannot be null or empty")
    @Column(name = "soundtrack_name", nullable = false)
    private String soundtrackName;

    @NotNull(message = "Weapon type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "weapon_type", nullable = false)
    private WeaponType weaponType;
}