package com.autentia.prueba.webflux.app.models.dao;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.autentia.prueba.webflux.app.models.document.Persona;

import reactor.core.publisher.Mono;

public interface PersonaDao extends ReactiveMongoRepository<Persona, String> {

	@Query("{'nombre':?0, 'apellidos':?1}")
	Mono<Persona> findPersonaByNombreAndApellidos(String nombre, String cogNoms);

}
