package hr.fer.rest_api.service;

import hr.fer.rest_api.dto.SportskiCentarDTO;
import hr.fer.rest_api.mapper.SportskiCentarMapper;
import hr.fer.rest_api.model.SportskiCentar;
import hr.fer.rest_api.repository.SportskiCentarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SportskiCentarService {

    private final SportskiCentarRepository sportskiCentarRepository;

    public List<SportskiCentarDTO> getAll() {
        List<SportskiCentar> sportskiCentri = sportskiCentarRepository.findAll();
        return sportskiCentri.stream()
                .map(SportskiCentarMapper::toDto)
                .toList();
    }

}
