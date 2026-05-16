package hr.fer.rest_api.service;

import hr.fer.rest_api.dto.SportRequest;
import hr.fer.rest_api.dto.response.SportDTO;
import hr.fer.rest_api.exception.ConflictException;
import hr.fer.rest_api.exception.ResourceNotFoundException;
import hr.fer.rest_api.mapper.SportMapper;
import hr.fer.rest_api.model.Sport;
import hr.fer.rest_api.repository.SportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SportService {

    private final SportRepository sportRepository;

    public SportService(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }

    public List<SportDTO> getAll(String search){
        String normalizedSearch = search == null ? null : search.trim();
        List<Sport> sports = normalizedSearch == null || normalizedSearch.isBlank()
                ? sportRepository.findAll()
                : sportRepository.findByNazivSportaContainingIgnoreCase(normalizedSearch);

        return sports.stream()
                .map(SportMapper::toDto)
                .toList();
    }

    public SportDTO getById(Integer id){
        return SportMapper.toDto(findSport(id));
    }

    public SportDTO create(SportRequest sportRequest){
        String nazivSporta = sportRequest.getNazivSporta().trim();

        if(sportRepository.existsByNazivSportaIgnoreCase(nazivSporta))
        {
            throw new ConflictException("Sport s tim nazivom već postoji");
        }

        Sport sport = new Sport();
        sport.setNazivSporta(nazivSporta);

        return SportMapper.toDto(sportRepository.save(sport));
    }

    public SportDTO update(Integer id, SportRequest sportRequest){
        Sport sport = findSport(id);
        String nazivSporta = sportRequest.getNazivSporta().trim();

        if (sportRepository.existsByNazivSportaIgnoreCaseAndIdSportNot(
                nazivSporta, id)
        ) {

            throw new ConflictException("Sport s tim nazivom već postoji");
        }

        sport.setNazivSporta(nazivSporta);

        return SportMapper.toDto(sportRepository.save(sport));
    }

    private Sport findSport(Integer id){
        return sportRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Sport nije pronadjen"));
    }

    public void delete(Integer id) {
        Sport sport = findSport(id);
        sportRepository.delete(sport);
    }



}
