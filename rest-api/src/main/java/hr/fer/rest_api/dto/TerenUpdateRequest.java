package hr.fer.rest_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TerenUpdateRequest {

    @NotNull(message = "Sport je obavezan")
    private Integer idSport;

    @NotNull(message = "Cijena je obavezna")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cijena mora biti veća od nule")
    private BigDecimal cijena;
}
