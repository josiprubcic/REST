package hr.fer.rest_api.mapper;

import hr.fer.rest_api.dto.response.MjestoDTO;
import hr.fer.rest_api.model.Mjesto;

public class MjestoMapper {

    private MjestoMapper() {
    }

    public static MjestoDTO toDto(Mjesto mjesto) {
        return new MjestoDTO(
                mjesto.getIdMjesto(),
                mjesto.getNazivMjesta(),
                mjesto.getPostanskiBroj()
        );
    }
}
