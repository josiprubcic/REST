package hr.fer.rest_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

interface Sport extends JpaRepository<hr.fer.rest_api.model.Sport, Integer> {
}
