package com.autentia.prueba.webflux.app.models.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.autentia.prueba.webflux.app.models.document.Gasto;
import com.autentia.prueba.webflux.app.models.document.Persona;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GastoDao extends ReactiveMongoRepository<Gasto, String>{
	
	
	public Flux<Gasto> findAll(Sort sort);
	
	public Mono<Gasto> findByDescripcion(String descripcion);
	
	@Query("{'descripcion': ?0}")
	public Mono<Gasto> buscarPorDescripcion(String descripcion);
	
	@Query("{'persona': ?0}")
	public Flux<Gasto> findGastosByPersona(Persona persona);
}
