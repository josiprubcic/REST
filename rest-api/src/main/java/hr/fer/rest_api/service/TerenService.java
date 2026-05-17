package hr.fer.rest_api.service;

import hr.fer.rest_api.dto.TerenCreateRequest;
import hr.fer.rest_api.dto.TerenUpdateRequest;
import hr.fer.rest_api.dto.response.TerenDTO;
import hr.fer.rest_api.exception.ConflictException;
import hr.fer.rest_api.exception.ResourceNotFoundException;
import hr.fer.rest_api.mapper.TerenMapper;
import hr.fer.rest_api.model.Sport;
import hr.fer.rest_api.model.SportskiCentar;
import hr.fer.rest_api.model.Teren;
import hr.fer.rest_api.model.TerenId;
import hr.fer.rest_api.repository.RezervacijaRepository;
import hr.fer.rest_api.repository.SportRepository;
import hr.fer.rest_api.repository.SportskiCentarRepository;
import hr.fer.rest_api.repository.TerenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TerenService {

    private final TerenRepository terenRepository;
    private final SportskiCentarRepository sportskiCentarRepository;
    private final SportRepository sportRepository;
    private final RezervacijaRepository rezervacijaRepository;

    public List<TerenDTO> getAllForCentar(Integer centarId, Integer sportId) {
        ensureCentarExists(centarId);

        List<Teren> tereni = sportId == null
                ? terenRepository.findBySportskiCentar_IdCentar(centarId)
                : terenRepository.findBySportskiCentar_IdCentarAndSport_IdSport(centarId, findSport(sportId).getIdSport());

        return tereni.stream()
                .map(TerenMapper::toDto)
                .toList();
    }

    public TerenDTO getById(Integer centarId, String terenId) {
        return TerenMapper.toDto(findTeren(centarId, terenId));
    }

    public TerenDTO create(Integer centarId, TerenCreateRequest request) {
        SportskiCentar centar = findCentar(centarId);
        Sport sport = findSport(request.getIdSport());
        String normalizedTerenId = request.getIdTeren().trim();
        TerenId id = new TerenId(centarId, normalizedTerenId);

        if (terenRepository.existsById(id)) {
            throw new ConflictException("Teren s tom oznakom već postoji u ovom sportskom centru");
        }

        Teren teren = new Teren();
        teren.setId(id);
        teren.setSportskiCentar(centar);
        teren.setSport(sport);
        teren.setCijena(request.getCijena());

        return TerenMapper.toDto(terenRepository.save(teren));
    }

    public TerenDTO update(Integer centarId, String terenId, TerenUpdateRequest request) {
        Teren teren = findTeren(centarId, terenId);
        Sport sport = findSport(request.getIdSport());

        boolean sportSeMijenja = !teren.getSport().getIdSport().equals(sport.getIdSport());
        if (sportSeMijenja && rezervacijaRepository.existsByTeren_Id(teren.getId())) {
            throw new ConflictException("Sport terena se ne može promijeniti jer teren ima rezervacije");
        }

        teren.setSport(sport);
        teren.setCijena(request.getCijena());

        return TerenMapper.toDto(terenRepository.save(teren));
    }

    public void delete(Integer centarId, String terenId) {
        Teren teren = findTeren(centarId, terenId);

        if (rezervacijaRepository.existsByTeren_Id(teren.getId())) {
            throw new ConflictException("Teren se ne može obrisati jer ima rezervacije");
        }

        terenRepository.delete(teren);
    }

    private void ensureCentarExists(Integer centarId) {
        if (!sportskiCentarRepository.existsById(centarId)) {
            throw new ResourceNotFoundException("Sportski centar nije pronađen");
        }
    }

    private SportskiCentar findCentar(Integer centarId) {
        return sportskiCentarRepository.findById(centarId)
                .orElseThrow(() -> new ResourceNotFoundException("Sportski centar nije pronađen"));
    }

    private Sport findSport(Integer idSport) {
        return sportRepository.findById(idSport)
                .orElseThrow(() -> new ResourceNotFoundException("Sport nije pronađen"));
    }

    private Teren findTeren(Integer centarId, String terenId) {
        TerenId id = new TerenId(centarId, terenId.trim());

        return terenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teren nije pronađen"));
    }
}
