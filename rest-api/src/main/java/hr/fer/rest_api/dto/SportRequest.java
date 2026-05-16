package hr.fer.rest_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SportRequest {

    @NotBlank(message = "Naziv sporta je obavezan")
    @Size(max = 50, message = "Naziv sporta može imati najviše 50 znakova")
    @Pattern(
            regexp = "^[\\p{L}\\p{M}0-9 .'-]+$",
            message = "Naziv sporta smije sadržavati samo slova, brojeve, razmake i osnovne znakove"
    )
    private String nazivSporta;
}
