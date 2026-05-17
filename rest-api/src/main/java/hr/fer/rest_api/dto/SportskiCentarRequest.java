package hr.fer.rest_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalTime;

@Data
public class SportskiCentarRequest {

    @Schema(example = "Sportski centar Maksimir")
    @NotBlank(message = "Naziv centra je obavezan")
    @Size(max = 100, message = "Naziv centra može imati najviše 100 znakova")
    @Pattern(
            regexp = "^[\\p{L}\\p{M}0-9 .'-]+$",
            message = "Naziv centra smije sadržavati samo slova, brojeve, razmake i osnovne znakove"
    )
    private String nazivCentra;

    @Schema(example = "Maksimirska 128")
    @NotBlank(message = "Adresa je obavezna")
    @Size(max = 120, message = "Adresa može imati najviše 120 znakova")
    @Pattern(
            regexp = "^[\\p{L}\\p{M}0-9 .,'/-]+$",
            message = "Adresa sadrži nedopuštene znakove"
    )
    private String adresa;

    @Schema(example = "1")
    @NotNull(message = "Mjesto je obavezno")
    private Integer idMjesto;

    @Schema(example = "08:00")
    @NotNull(message = "Početak radnog vremena kroz tjedan je obavezan")
    private LocalTime radnoVrijemeTjedanOd;

    @Schema(example = "22:00")
    @NotNull(message = "Kraj radnog vremena kroz tjedan je obavezan")
    private LocalTime radnoVrijemeTjedanDo;

    @Schema(example = "09:00")
    @NotNull(message = "Početak radnog vremena vikendom je obavezan")
    private LocalTime radnoVrijemeVikendOd;

    @Schema(example = "20:00")
    @NotNull(message = "Kraj radnog vremena vikendom je obavezan")
    private LocalTime radnoVrijemeVikendDo;
}
