package hr.fer.rest_api.controller;

import hr.fer.rest_api.dto.SportRequest;
import hr.fer.rest_api.dto.response.SportDTO;
import hr.fer.rest_api.service.SportService;
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
@RequestMapping("/api/sportovi")
@RequiredArgsConstructor
public class SportController {

    private final SportService sportService;

    @GetMapping
    public List<SportDTO> getAll(@RequestParam(required = false) String search) {
        return sportService.getAll(search);
    }

    @GetMapping("/{id}")
    public SportDTO getById(@PathVariable Integer id) {
        return sportService.getById(id);
    }

    @Operation(summary = "Kreira novi sport")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sport je uspješno kreiran"),
            @ApiResponse(responseCode = "400", description = "Neispravan zahtjev"),
            @ApiResponse(responseCode = "409", description = "Sport s tim nazivom već postoji")
    })
    @PostMapping
    public ResponseEntity<SportDTO> create(@Valid @RequestBody SportRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sportService.create(request));
    }

    @PutMapping("/{id}")
    public SportDTO update(
            @PathVariable Integer id,
            @Valid @RequestBody SportRequest request
    ) {
        return sportService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        sportService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
