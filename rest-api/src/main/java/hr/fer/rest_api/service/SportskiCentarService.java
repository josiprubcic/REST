package hr.fer.rest_api.service;

import hr.fer.rest_api.dto.SportskiCentarRequest;
import hr.fer.rest_api.dto.SportskiCentarDTO;
import hr.fer.rest_api.exception.BusinessRuleException;
import hr.fer.rest_api.exception.ConflictException;
import hr.fer.rest_api.exception.ResourceNotFoundException;
import hr.fer.rest_api.mapper.SportskiCentarMapper;
import hr.fer.rest_api.model.Mjesto;
import hr.fer.rest_api.model.SportskiCentar;
import hr.fer.rest_api.repository.MjestoRepository;
import hr.fer.rest_api.repository.SportskiCentarRepository;
import hr.fer.rest_api.repository.TerenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SportskiCentarService {

    private final SportskiCentarRepository sportskiCentarRepository;
    private final MjestoRepository mjestoRepository;
    private final TerenRepository terenRepository;

    public List<SportskiCentarDTO> getAll(String search, Integer mjestoId) {
        String normalizedSearch = search == null ? null : search.trim();
        List<SportskiCentar> sportskiCentri;

        if (normalizedSearch != null && !normalizedSearch.isBlank() && mjestoId != null) {
            sportskiCentri = sportskiCentarRepository
                    .findByNazivCentraContainingIgnoreCaseAndMjesto_IdMjesto(normalizedSearch, mjestoId);
        } else if (normalizedSearch != null && !normalizedSearch.isBlank()) {
            sportskiCentri = sportskiCentarRepository.findByNazivCentraContainingIgnoreCase(normalizedSearch);
        } else if (mjestoId != null) {
            sportskiCentri = sportskiCentarRepository.findByMjesto_IdMjesto(mjestoId);
        } else {
            sportskiCentri = sportskiCentarRepository.findAll();
        }

        return sportskiCentri.stream()
                .map(SportskiCentarMapper::toDto)
                .toList();
    }

    public SportskiCentarDTO getById(Integer id) {
        return SportskiCentarMapper.toDto(findCentar(id));
    }

    public SportskiCentarDTO create(SportskiCentarRequest request) {
        validateWorkingHours(request);

        Mjesto mjesto = findMjesto(request.getIdMjesto());
        String nazivCentra = request.getNazivCentra().trim();
        String adresa = request.getAdresa().trim();
        validateDuplicateCenter(adresa, mjesto.getIdMjesto());

        SportskiCentar centar = new SportskiCentar();
        applyRequest(centar, request, mjesto, nazivCentra, adresa);

        return SportskiCentarMapper.toDto(sportskiCentarRepository.save(centar));
    }

    public SportskiCentarDTO update(Integer id, SportskiCentarRequest request) {
        validateWorkingHours(request);

        SportskiCentar centar = findCentar(id);
        Mjesto mjesto = findMjesto(request.getIdMjesto());
        String nazivCentra = request.getNazivCentra().trim();
        String adresa = request.getAdresa().trim();
        validateDuplicateCenter(adresa, mjesto.getIdMjesto(), id);

        applyRequest(centar, request, mjesto, nazivCentra, adresa);

        return SportskiCentarMapper.toDto(sportskiCentarRepository.save(centar));
    }

    public void delete(Integer id) {
        SportskiCentar centar = findCentar(id);

        if (terenRepository.existsBySportskiCentar_IdCentar(id)) {
            throw new ConflictException("Sportski centar se ne može obrisati jer ima terene");
        }

        sportskiCentarRepository.delete(centar);
    }

    private void applyRequest(
            SportskiCentar centar,
            SportskiCentarRequest request,
            Mjesto mjesto,
            String nazivCentra,
            String adresa
    ) {
        centar.setNazivCentra(nazivCentra);
        centar.setAdresa(adresa);
        centar.setMjesto(mjesto);
        centar.setRadnoVrijemeTjedanOd(request.getRadnoVrijemeTjedanOd());
        centar.setRadnoVrijemeTjedanDo(request.getRadnoVrijemeTjedanDo());
        centar.setRadnoVrijemeVikendOd(request.getRadnoVrijemeVikendOd());
        centar.setRadnoVrijemeVikendDo(request.getRadnoVrijemeVikendDo());
    }

    private void validateDuplicateCenter(String adresa, Integer idMjesto) {
        if (sportskiCentarRepository.existsByAdresaIgnoreCaseAndMjesto_IdMjesto(
                adresa,
                idMjesto
        )) {
            throw new ConflictException("Sportski centar s tom adresom u tom mjestu već postoji");
        }
    }

    private void validateDuplicateCenter(String adresa, Integer idMjesto, Integer idCentar) {
        if (sportskiCentarRepository.existsByAdresaIgnoreCaseAndMjesto_IdMjestoAndIdCentarNot(
                adresa,
                idMjesto,
                idCentar
        )) {
            throw new ConflictException("Sportski centar s tom adresom u tom mjestu već postoji");
        }
    }

    private SportskiCentar findCentar(Integer id) {
        return sportskiCentarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sportski centar nije pronađen"));
    }

    private Mjesto findMjesto(Integer idMjesto) {
        return mjestoRepository.findById(idMjesto)
                .orElseThrow(() -> new ResourceNotFoundException("Mjesto nije pronađeno"));
    }

    private void validateWorkingHours(SportskiCentarRequest request) {
        validateTimeRange(
                request.getRadnoVrijemeTjedanOd(),
                request.getRadnoVrijemeTjedanDo(),
                "Radno vrijeme kroz tjedan nije ispravno"
        );
        validateTimeRange(
                request.getRadnoVrijemeVikendOd(),
                request.getRadnoVrijemeVikendDo(),
                "Radno vrijeme vikendom nije ispravno"
        );
    }

    private void validateTimeRange(LocalTime od, LocalTime doVrijeme, String message) {
        if ((od == null && doVrijeme != null) || (od != null && doVrijeme == null)) {
            throw new BusinessRuleException(message);
        }

        if (od != null && !od.isBefore(doVrijeme)) {
            throw new BusinessRuleException(message);
        }
    }
}
