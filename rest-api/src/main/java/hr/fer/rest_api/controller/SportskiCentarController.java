package hr.fer.rest_api.controller;

import hr.fer.rest_api.dto.SportskiCentarDTO;
import hr.fer.rest_api.dto.SportskiCentarRequest;
import hr.fer.rest_api.service.SportskiCentarService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sportski-centri")
@RequiredArgsConstructor
public class SportskiCentarController {

    private final SportskiCentarService sportskiCentarService;

    @GetMapping
    public List<SportskiCentarDTO> getSportskiCentri(
            @Parameter(
                    description = "Pretraga po nazivu sportskog centra",
                    example = "Maksimir"
            )
            @RequestParam(required = false) String search,
            @Parameter(
                    description = "Filter po ID-u mjesta",
                    example = "1"
            )
            @RequestParam(required = false) Integer mjestoId
    ) {
        return sportskiCentarService.getAll(search, mjestoId);
    }

    @GetMapping("/{id}")
    public SportskiCentarDTO getById(@PathVariable Integer id) {
        return sportskiCentarService.getById(id);
    }

    @PostMapping
    public ResponseEntity<SportskiCentarDTO> create(
            @Valid @RequestBody SportskiCentarRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sportskiCentarService.create(request));
    }

    @PutMapping("/{id}")
    public SportskiCentarDTO update(
            @PathVariable Integer id,
            @Valid @RequestBody SportskiCentarRequest request
    ) {
        return sportskiCentarService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        sportskiCentarService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
