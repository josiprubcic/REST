package hr.fer.rest_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MjestoRequest {

    @NotBlank(message = "Naziv mjesta je obavezan")
    @Size(max = 80, message = "Naziv mjesta može imati najviše 80 znakova")
    @Pattern(
            regexp = "^[\\p{L}\\p{M}0-9 .'-]+$",
            message = "Naziv mjesta smije sadržavati samo slova, brojeve, razmake i osnovne znakove"
    )
    private String nazivMjesta;

    @NotBlank(message = "Poštanski broj je obavezan")
    @Pattern(
            regexp = "^\\d{5}$",
            message = "Poštanski broj mora imati točno 5 znamenki"
    )
    private String postanskiBroj;
}
