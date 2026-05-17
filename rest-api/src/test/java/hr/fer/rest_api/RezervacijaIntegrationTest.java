package hr.fer.rest_api;

import hr.fer.rest_api.controller.RezervacijaController;
import hr.fer.rest_api.dto.RezervacijaRequest;
import hr.fer.rest_api.exception.ConflictException;
import hr.fer.rest_api.model.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RezervacijaIntegrationTest {

	@Autowired
	private RezervacijaController rezervacijaController;

	@Autowired
	private EntityManager entityManager;

	@Test
	void shouldCreteAndSaveReservation() {
		Korisnik korisnik = createDummyKorisnik("Ivan", "Ivić", "ivan@gmail.com", "SPORTAS_REKREATIVAC");
		entityManager.persist(korisnik);

		SportasRekreativac rekreativac = new SportasRekreativac();
		rekreativac.setIdKorisnik(korisnik.getIdKorisnik());
		rekreativac.setKorisnik(korisnik);
		entityManager.persist(rekreativac);

		SportskiCentar centar = createDummySportskiCentar("TTS Varazdin");
		entityManager.persist(centar);

		Teren teren = createDummyTeren(new TerenId(centar.getIdCentar(), "Teren 1"), centar);
		entityManager.persist(teren);
		entityManager.flush();

		RezervacijaRequest request = new RezervacijaRequest();
		request.setIdKorisnik(korisnik.getIdKorisnik());
		request.setIdCentar(centar.getIdCentar());
		request.setIdTeren(teren.getId().getIdTeren());
		request.setVrijemePocetka(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0));
		request.setVrijemeZavrsetka(LocalDateTime.now().plusDays(1).withHour(15).withMinute(0));

		var response = rezervacijaController.createReservation(request);

		assertThat(response.getStatusCode().value()).isEqualTo(201);

		assertThrows(ConflictException.class, () -> {
			rezervacijaController.createReservation(request);
		});
	}


	private SportskiCentar createDummySportskiCentar(String nazivCentra) {
		SportskiCentar centar = new SportskiCentar();
		centar.setNazivCentra(nazivCentra);
		return centar;
	}

	private Teren createDummyTeren(TerenId terenId, SportskiCentar centar) {
		Teren teren = new Teren();
		teren.setId(terenId);
		teren.setSportskiCentar(centar);
		return teren;
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