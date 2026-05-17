package hr.fer.rest_api.mapper;

import hr.fer.rest_api.dto.response.TerenDTO;
import hr.fer.rest_api.model.Teren;

public class TerenMapper {

    private TerenMapper() {
    }

    public static TerenDTO toDto(Teren teren) {
        if (teren == null) {
            return null;
        }

        TerenDTO dto = new TerenDTO();
        dto.setIdCentar(teren.getId().getIdCentar());
        dto.setIdTeren(teren.getId().getIdTeren());
        dto.setCijena(teren.getCijena());

        if (teren.getSport() != null) {
            dto.setIdSport(teren.getSport().getIdSport());
            dto.setNazivSporta(teren.getSport().getNazivSporta());
        }

        return dto;
    }
}
