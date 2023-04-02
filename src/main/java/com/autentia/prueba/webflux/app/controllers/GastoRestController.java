package com.autentia.prueba.webflux.app.controllers;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.autentia.prueba.webflux.app.models.document.Gasto;
import com.autentia.prueba.webflux.app.models.services.GastoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/gastos-compartits")
public class GastoRestController {
	private static final Logger log = LoggerFactory.getLogger(GastoRestController.class);
	
	@Autowired
	private GastoService service;
	
	@GetMapping
	public Mono<ResponseEntity<Flux<Gasto>>>  listar() {
		Flux<Gasto> gastos = service.findAll()
				.doOnNext(gasto -> log.info(String.format("Gasto de %s per a %s per comprar %s", gasto.getImporte(), gasto.getPersona().getNombre(), gasto.getDescripcion())));
		return Mono.just(ResponseEntity.ok(gastos)) ;
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Gasto>> mostrarById(@PathVariable String id) {
		return service.findById(id)
				.map(p -> 
					ResponseEntity.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(p))
					.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	
	
	@PostMapping
	public Mono<ResponseEntity<Map<String, Object>>> guardar(@Valid @RequestBody Mono<Gasto> monoGasto) {
		Map<String, Object> resposta = new HashMap<String, Object>();
		
		return monoGasto.flatMap(gasto -> {
			return service.save(gasto)
					.map(g -> {
						resposta.put("gasto", g);
						resposta.put("missatge", "Guardat amb exit");
						return ResponseEntity.created(URI.create("/api/gastos-compartits/".concat(g.getId())))
								.contentType(MediaType.APPLICATION_JSON)
								.body(resposta);
							});
			
		}).onErrorResume(t -> {
			return Mono.just(t).cast(WebExchangeBindException.class)
					.flatMap(e -> Mono.just(e.getFieldErrors()))
					.flatMapMany(Flux::fromIterable)
					.map(fieldError -> "El camp " + fieldError.getField() + " " + fieldError.getDefaultMessage())
					.collectList()
					.flatMap(list -> {
						resposta.put("errors", list);
						resposta.put("missatge", "No s'ha pogut guardar");
						return Mono.just(ResponseEntity.badRequest().body(resposta));
					});
		});
	}
	
	
	
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> eliminar(@PathVariable String id) {
		return service.findById(id)
				.flatMap(g -> {
					return service.delete(g).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
				})
				.defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}
}
