package hr.fer.rest_api.service;

import hr.fer.rest_api.dto.MjestoRequest;
import hr.fer.rest_api.dto.response.MjestoDTO;
import hr.fer.rest_api.exception.ConflictException;
import hr.fer.rest_api.exception.ResourceNotFoundException;
import hr.fer.rest_api.mapper.MjestoMapper;
import hr.fer.rest_api.model.Mjesto;
import hr.fer.rest_api.repository.MjestoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MjestoService {

    private final MjestoRepository mjestoRepository;

    public MjestoService(MjestoRepository mjestoRepository) {
        this.mjestoRepository = mjestoRepository;
    }

    public List<MjestoDTO> getAll(String search) {
        String normalizedSearch = search == null ? null : search.trim();
        List<Mjesto> mjesta = normalizedSearch == null || normalizedSearch.isBlank()
                ? mjestoRepository.findAll()
                : mjestoRepository.findByNazivMjestaContainingIgnoreCaseOrPostanskiBrojContainingIgnoreCase(
                        normalizedSearch,
                        normalizedSearch
                );

        return mjesta.stream()
                .map(MjestoMapper::toDto)
                .toList();
    }

    public MjestoDTO getById(Integer id) {
        return MjestoMapper.toDto(findMjesto(id));
    }

    public MjestoDTO create(MjestoRequest request) {
        String nazivMjesta = request.getNazivMjesta().trim();
        String postanskiBroj = request.getPostanskiBroj().trim();

        if (mjestoRepository.existsByNazivMjestaIgnoreCaseAndPostanskiBroj(nazivMjesta, postanskiBroj)) {
            throw new ConflictException("Mjesto s tim nazivom i poštanskim brojem već postoji");
        }

        Mjesto mjesto = new Mjesto();
        mjesto.setNazivMjesta(nazivMjesta);
        mjesto.setPostanskiBroj(postanskiBroj);

        return MjestoMapper.toDto(mjestoRepository.save(mjesto));
    }

    public MjestoDTO update(Integer id, MjestoRequest request) {
        Mjesto mjesto = findMjesto(id);
        String nazivMjesta = request.getNazivMjesta().trim();
        String postanskiBroj = request.getPostanskiBroj().trim();

        if (mjestoRepository.existsByNazivMjestaIgnoreCaseAndPostanskiBrojAndIdMjestoNot(
                nazivMjesta,
                postanskiBroj,
                id
        )) {
            throw new ConflictException("Mjesto s tim nazivom i poštanskim brojem već postoji");
        }

        mjesto.setNazivMjesta(nazivMjesta);
        mjesto.setPostanskiBroj(postanskiBroj);

        return MjestoMapper.toDto(mjestoRepository.save(mjesto));
    }

    public void delete(Integer id) {
        Mjesto mjesto = findMjesto(id);
        mjestoRepository.delete(mjesto);
    }

    private Mjesto findMjesto(Integer id) {
        return mjestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mjesto nije pronađeno"));
    }
}
