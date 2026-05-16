package hr.fer.rest_api.mapper;

import hr.fer.rest_api.dto.RezervacijaDTO;
import hr.fer.rest_api.model.Rezervacija;

public class RezervacijaMapper {

    public static RezervacijaDTO toDto(Rezervacija rezervacija) {
        if (rezervacija == null) return null;

        RezervacijaDTO dto = new RezervacijaDTO();
        dto.setIdRezervacija(rezervacija.getIdRezervacija());
        dto.setVrijemePocetka(rezervacija.getVrijemePocetka());
        dto.setVrijemeZavrsetka(rezervacija.getVrijemeZavrsetka());

        if (rezervacija.getTeren() != null) {
            dto.setIdCentar(rezervacija.getTeren().getSportskiCentar().getIdCentar());
            dto.setIdTeren(rezervacija.getTeren().getId().getIdTeren());
            if (rezervacija.getTeren().getSportskiCentar() != null) {
                dto.setNazivCentra(rezervacija.getTeren().getSportskiCentar().getNazivCentra());
            }
        }
        if (rezervacija.getSportasRekreativac() != null && rezervacija.getSportasRekreativac().getKorisnik() != null) {
            dto.setIme(rezervacija.getSportasRekreativac().getKorisnik().getIme());
            dto.setPrezime(rezervacija.getSportasRekreativac().getKorisnik().getPrezime());
        }
        return dto;
    }
}