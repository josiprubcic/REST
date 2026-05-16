package hr.fer.rest_api.controller;

import hr.fer.rest_api.dto.MjestoRequest;
import hr.fer.rest_api.dto.response.MjestoDTO;
import hr.fer.rest_api.service.MjestoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/mjesta")
@RequiredArgsConstructor
public class MjestoController {

    private final MjestoService mjestoService;

    @GetMapping
    public List<MjestoDTO> getAll(@RequestParam(required = false) String search) {
        return mjestoService.getAll(search);
    }

    @GetMapping("/{id}")
    public MjestoDTO getById(@PathVariable Integer id) {
        return mjestoService.getById(id);
    }

    @Operation(summary = "Kreira novo mjesto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mjesto je uspješno kreirano"),
            @ApiResponse(responseCode = "400", description = "Neispravan zahtjev"),
            @ApiResponse(responseCode = "409", description = "Mjesto s tim nazivom i poštanskim brojem već postoji")
    })
    @PostMapping
    public ResponseEntity<MjestoDTO> create(@Valid @RequestBody MjestoRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mjestoService.create(request));
    }

    @PutMapping("/{id}")
    public MjestoDTO update(
            @PathVariable Integer id,
            @Valid @RequestBody MjestoRequest request
    ) {
        return mjestoService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        mjestoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
