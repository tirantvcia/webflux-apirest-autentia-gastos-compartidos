package com.autentia.prueba.webflux.app.controllers;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import com.autentia.prueba.webflux.app.models.document.Gasto;
import com.autentia.prueba.webflux.app.models.document.Persona;
import com.autentia.prueba.webflux.app.models.services.GastoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class GastoController {
	
	private static Logger log = LoggerFactory.getLogger(GastoController.class);
	
	private static final int NUMBER_OF_REPEATS = 5000;
	@Autowired
	private GastoService service;
	
	@GetMapping({"/listar", "/"})
	public String listar(Model model) {
		Flux<Gasto> gastos = service.findAll();
		model.addAttribute("gastos", gastos);
		model.addAttribute("titol", "Total gastos compartits");
		return "listar";
	}
	
	@GetMapping("/listar-dataDriver")
	public String listarDataDriver(Model model) {
		Flux<Gasto> gastos = service.findAllWithNameUpperCase()
				.delayElements(Duration.ofSeconds(2));
		model.addAttribute("gastos", new ReactiveDataDriverContextVariable(gastos, 2) );
		model.addAttribute("titol", "Total gastos compartits");
		return "listar";
	}
	
	@GetMapping({"/listar-full"})
	public String listarFull (Model model) {
		Flux<Gasto> gastos = service.findAllWithRepeats(NUMBER_OF_REPEATS);
		model.addAttribute("gastos", gastos);
		model.addAttribute("titol", "Total gastos compartits");
		return "listar";
	}
	@GetMapping({"/listar-chunked"})
	public String listarChunked (Model model) {
		Flux<Gasto> gastos = service.findAllWithRepeats(NUMBER_OF_REPEATS);
		model.addAttribute("gastos", gastos);
		model.addAttribute("titol", "Total gastos compartits");
		return "listar-chuncked";
	}	
	
	@PostMapping
	public Mono<String> guardar(Gasto gasto, Model model, SessionStatus status) {
		status.setComplete();
		Mono<Persona> persona = service.findPersonaById(gasto.getPersona().getId());
		
		return persona.flatMap(p -> {
			gasto.setPersona(p);
			return service.save(gasto);
		}).doOnNext(g -> {
			log.info("Gasto guardat " + g.getDescripcion() + " per " + g.getPersona().getNombre() + " de " + g.getImporte());
		}).thenReturn("redirect:/listar?success=producte+guardat+amb+exit");
	}
	
}
