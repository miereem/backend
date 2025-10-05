package org.example.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Coordinates {
    @NotNull(message = "X cannot be null")
    @Max(value = 15, message = "X cannot exceed 15")
    private Integer x;

    @NotNull(message = "Y cannot be null")
    @DecimalMax(value = "277.0", message = "Y cannot exceed 277")
    private Double y;
}