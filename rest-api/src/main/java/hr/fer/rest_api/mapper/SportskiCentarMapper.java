package hr.fer.rest_api.mapper;

import hr.fer.rest_api.dto.SportskiCentarDTO;
import hr.fer.rest_api.model.SportskiCentar;

public class SportskiCentarMapper {

    public static SportskiCentarDTO toDto(SportskiCentar centar) {
        if (centar == null) return null;
        SportskiCentarDTO dto = new SportskiCentarDTO();
        dto.setIdCentar(centar.getIdCentar());
        dto.setNazivCentra(centar.getNazivCentra());
        dto.setAdresa(centar.getAdresa());
        dto.setRadnoVrijemeTjedanOd(centar.getRadnoVrijemeTjedanOd());
        dto.setRadnoVrijemeTjedanDo(centar.getRadnoVrijemeTjedanDo());
        dto.setRadnoVrijemeVikendOd(centar.getRadnoVrijemeVikendOd());
        dto.setRadnoVrijemeVikendDo(centar.getRadnoVrijemeVikendDo());
        if (centar.getMjesto() != null) {
            dto.setIdMjesto(centar.getMjesto().getIdMjesto());
            dto.setNazivMjesta(centar.getMjesto().getNazivMjesta());
        }
        if (centar.getVlasnikCentra() != null) {
            dto.setIdVlasnik(centar.getVlasnikCentra().getIdKorisnik());
        }
        return dto;
    }
}
