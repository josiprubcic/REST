package hr.fer.rest_api.repository;

import hr.fer.rest_api.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RezervacijaRepositoryTest {

    @Autowired
    private RezervacijaRepository rezervacijaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldCreateReservationAndSaveIt() {
        Korisnik korisnik = createDummyKorisnik("Ivan", "Ivić", "ivan@gmail.com", "SPORTAS_REKREATIVAC");
        entityManager.persist(korisnik);
        entityManager.flush();

        SportasRekreativac rekreativac = createDummySportasRekreativac(korisnik.getIdKorisnik(), korisnik);
        entityManager.persist(rekreativac);

        SportskiCentar centar = createDummySportskiCentar("TTS Varazdin");
        entityManager.persist(centar);
        entityManager.flush();

        Teren teren = createDummyTeren(new TerenId(centar.getIdCentar(), "Teren 1"), centar);
        entityManager.persist(teren);

        Rezervacija rezervacija = createDummyRezervacija(rekreativac, teren, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        entityManager.persist(rezervacija);

        entityManager.flush();

        List<Rezervacija> pronadeneRezervacije =
                rezervacijaRepository.findBySportasRekreativac_IdKorisnik(korisnik.getIdKorisnik());

        assertThat(pronadeneRezervacije).isNotEmpty();
        assertThat(pronadeneRezervacije.get(0).getSportasRekreativac().getIdKorisnik())
                .isEqualTo(korisnik.getIdKorisnik());
    }

    private Rezervacija createDummyRezervacija(SportasRekreativac rekreativac, Teren teren, LocalDateTime vrijemePocetka, LocalDateTime vrijemeZavrsetka) {
        Rezervacija rezervacija = new Rezervacija();
        rezervacija.setSportasRekreativac(rekreativac);
        rezervacija.setTeren(teren);
        rezervacija.setVrijemePocetka(vrijemePocetka);
        rezervacija.setVrijemeZavrsetka(vrijemeZavrsetka);
        return rezervacija;
    }

    private Teren createDummyTeren(TerenId terenId, SportskiCentar centar) {
        Teren teren = new Teren();
        teren.setId(terenId);
        teren.setSportskiCentar(centar);
        return teren;
    }

    private SportskiCentar createDummySportskiCentar(String nazivCentra) {
        SportskiCentar centar = new SportskiCentar();
        centar.setNazivCentra(nazivCentra);
        return centar;
    }

    private SportasRekreativac createDummySportasRekreativac(Integer idKorisnik, Korisnik korisnik) {
        SportasRekreativac rekreativac = new SportasRekreativac();
        rekreativac.setIdKorisnik(idKorisnik);
        rekreativac.setKorisnik(korisnik);
        return rekreativac;
    }

    private Korisnik createDummyKorisnik(String ime, String prezime, String email, String tipKorisnika) {
        Korisnik korisnik = new Korisnik();
        korisnik.setIme(ime);
        korisnik.setPrezime(prezime);
        korisnik.setEmail(email);
        korisnik.setTipKorisnika(tipKorisnika);
        return korisnik;
    }
}