package com.autentia.prueba.webflux.app.handler;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.autentia.prueba.webflux.app.models.document.Gasto;
import com.autentia.prueba.webflux.app.models.document.Persona;
import com.autentia.prueba.webflux.app.models.services.GastoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class GastoHandler {
	
	@Autowired
	private GastoService service;
	
	@Autowired
	private Validator validator;
	
	
	public Mono<ServerResponse> listar (ServerRequest request) {
		return 	 ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.findAll(), Gasto.class);
	}
	
	public Mono<ServerResponse> obtenerBalances(ServerRequest request) {
		return 	 ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.obtenerBalancePorPersona(), Persona.class);
		
	}

	
	public Mono<ServerResponse> mostrar (ServerRequest request) {
		String id = request.pathVariable("id");
		return 	service.findById(id).flatMap(p -> 
			ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(p))
				.switchIfEmpty(ServerResponse.notFound().build());

	}
	
	public Mono<ServerResponse> guardar(ServerRequest request) {
		Mono<Gasto> gasto = request.bodyToMono(Gasto.class);
		
		return gasto.flatMap(g -> {
			Errors errors = new BeanPropertyBindingResult(g, Gasto.class.getName());
			validator.validate(g, errors );
			if (errors.hasErrors()) {
				return Flux.fromIterable(errors.getFieldErrors())
						.map(fe -> "El camp " + fe.getField() + " " + fe.getDefaultMessage())
						.collectList()
						.flatMap(list -> ServerResponse.badRequest().bodyValue(list));
			} else {
				return service.save(g).flatMap(gDb ->  ServerResponse.created(URI.create("/api/rf/gastos-compartits/".concat(gDb.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(gDb));
			}
			
			
		});
				
	};
	
	public Mono<ServerResponse> afegirAmic(ServerRequest request) {
		Mono<Persona> monoPersona = request.bodyToMono(Persona.class);
		
		return monoPersona.flatMap(p -> {
			Errors errors = new BeanPropertyBindingResult(p, Persona.class.getName());
			validator.validate(p, errors );
			if (errors.hasErrors()) {
				return Flux.fromIterable(errors.getFieldErrors())
						.map(fe -> "El camp " + fe.getField() + " " + fe.getDefaultMessage())
						.collectList()
						.flatMap(list -> ServerResponse.badRequest().bodyValue(list));
			} else {
				return service.savePersona(p).flatMap(pDb ->  ServerResponse.created(URI.create("/api/rf/gastos-compartits/afegir-amic/".concat(pDb.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue(pDb));
			}
			
			
		});
		
	}	
	
	public Mono<ServerResponse> editar(ServerRequest request) {
		Mono<Gasto> gasto = request.bodyToMono(Gasto.class);
		String id = request.pathVariable("id");
		
		Mono<Gasto> gastoDb = service.findById(id);
		
		return gastoDb.zipWith(gasto, (db, req) -> {
			db.setDescripcion(req.getDescripcion());
			db.setImporte(req.getImporte());
			return db;
			}).flatMap(g ->  ServerResponse.created(URI.create("/api/rf/gastos-compartits/".concat(g.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(service.save(g), Gasto.class))
				.switchIfEmpty(ServerResponse.notFound().build());
	};
	public Mono<ServerResponse> eliminar(ServerRequest request) {
		String id = request.pathVariable("id");
		
		Mono<Gasto> gastoDb = service.findById(id);
		
		return gastoDb.flatMap(g -> 
			service.delete(g)
				.then(ServerResponse.noContent().build()))
				.switchIfEmpty(ServerResponse.notFound().build());
	};	
	
		
}
