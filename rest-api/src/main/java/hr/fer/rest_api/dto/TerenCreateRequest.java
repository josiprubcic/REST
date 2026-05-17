package hr.fer.rest_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TerenCreateRequest {

    @NotBlank(message = "Oznaka terena je obavezna")
    @Size(max = 20, message = "Oznaka terena može imati najviše 20 znakova")
    @Pattern(
            regexp = "^[A-Za-z0-9_-]+$",
            message = "Oznaka terena smije sadržavati samo slova, brojeve, _ i -"
    )
    private String idTeren;

    @NotNull(message = "Sport je obavezan")
    private Integer idSport;

    @NotNull(message = "Cijena je obavezna")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cijena mora biti veća od nule")
    private BigDecimal cijena;
}
