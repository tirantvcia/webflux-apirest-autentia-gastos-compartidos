package com.autentia.prueba.webflux.app.models.services;

import com.autentia.prueba.webflux.app.models.document.Gasto;
import com.autentia.prueba.webflux.app.models.document.Persona;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GastoService {
	public Flux<Gasto> findAll();
	public Flux<Gasto> findAllWithNameUpperCase();
	public Flux<Gasto> findAllWithRepeats(long numRepeat);	
	public Mono<Gasto> save(Gasto gasto);
	public Mono<Void> delete(Gasto gasto);
	public Mono<Gasto> findById(String id);
	public Mono<Gasto> findByDescripcion(String descripcion);
	public Flux<Persona> findAllPersona();
	public Mono<Persona> findPersonaById(String id);
	public Mono<Persona> savePersona(Persona persona);
	public Mono<Persona> findPersonaByNombreAndApellidos(String nom, String cogNoms);
	public Mono<Double> calcularGastoTotal();
	public Mono<Double> calcularGastoPromedio();
	public Flux<Persona> obtenerBalancePorPersona();
	public Flux<Gasto> findGastosByPersona(Persona persona);
}
