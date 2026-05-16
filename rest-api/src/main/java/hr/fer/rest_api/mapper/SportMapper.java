package hr.fer.rest_api.mapper;

import hr.fer.rest_api.dto.response.SportDTO;
import hr.fer.rest_api.model.Sport;

public class SportMapper {

    private SportMapper(){}
    public static SportDTO toDto(Sport sport){
        return new SportDTO(
                sport.getIdSport(),
                sport.getNazivSporta()
        );
    }
}
