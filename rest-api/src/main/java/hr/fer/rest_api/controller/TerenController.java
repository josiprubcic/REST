package hr.fer.rest_api.controller;

import hr.fer.rest_api.dto.TerenCreateRequest;
import hr.fer.rest_api.dto.TerenUpdateRequest;
import hr.fer.rest_api.dto.response.TerenDTO;
import hr.fer.rest_api.service.TerenService;
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
@RequestMapping("/api/sportski-centri/{centarId}/tereni")
@RequiredArgsConstructor
public class TerenController {

    private final TerenService terenService;

    @GetMapping
    public List<TerenDTO> getAllForCentar(
            @PathVariable Integer centarId,
            @RequestParam(required = false) Integer sportId
    ) {
        return terenService.getAllForCentar(centarId, sportId);
    }

    @GetMapping("/{terenId}")
    public TerenDTO getById(
            @PathVariable Integer centarId,
            @PathVariable String terenId
    ) {
        return terenService.getById(centarId, terenId);
    }

    @PostMapping
    public ResponseEntity<TerenDTO> create(
            @PathVariable Integer centarId,
            @Valid @RequestBody TerenCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(terenService.create(centarId, request));
    }

    @PutMapping("/{terenId}")
    public TerenDTO update(
            @PathVariable Integer centarId,
            @PathVariable String terenId,
            @Valid @RequestBody TerenUpdateRequest request
    ) {
        return terenService.update(centarId, terenId, request);
    }

    @DeleteMapping("/{terenId}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer centarId,
            @PathVariable String terenId
    ) {
        terenService.delete(centarId, terenId);
        return ResponseEntity.noContent().build();
    }
}
