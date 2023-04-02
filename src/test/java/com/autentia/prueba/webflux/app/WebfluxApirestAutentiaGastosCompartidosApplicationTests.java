package com.autentia.prueba.webflux.app;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.autentia.prueba.webflux.app.models.document.Gasto;
import com.autentia.prueba.webflux.app.models.document.Persona;
import com.autentia.prueba.webflux.app.models.services.GastoService;

import reactor.core.publisher.Mono;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebfluxApirestAutentiaGastosCompartidosApplicationTests {

	private static final String CAMISETA_DEL_VALENCIA_CF = "Camiseta del València CF";

	private static final String CAMISETA_DEL_OSASUNA = "Camiseta del Osasuna CF";

	@Autowired
	private WebTestClient client;
	
	@Autowired
	GastoService service;
	
	Gasto gastoAnt = null;
	
	@BeforeEach
	public void init() {
		gastoAnt = null;
	}
	
	
	
	@Test
	public void listarTest() {
		
		
		
		client.get().uri("/api/rf/gastos-compartits")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBodyList(Gasto.class)
		//.hasSize(5)
		.consumeWith(response -> {
			List<Gasto> gastosCompartits = response.getResponseBody();
			
			Assertions.assertThat(gastosCompartits.size() > 0).isTrue();
			
			gastosCompartits.forEach(gasto -> {
				if(gastoAnt == null) {
					gastoAnt = gasto;
				} else {
					Assertions.assertThat(gastoAnt.getCreateAt().compareTo(gasto.getCreateAt()) >= 0).isTrue();
					gastoAnt = gasto;
				}
				
			});
			
		});
	}
	@Test
	public void verTestJsoPath() {
		Gasto gasto = service.findByDescripcion(CAMISETA_DEL_VALENCIA_CF).block();
		
		client.get().uri("/api/rf/gastos-compartits/{id}", Collections.singletonMap("id", gasto.getId()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.descripcion").isEqualTo(CAMISETA_DEL_VALENCIA_CF);
	}
	
	@Test
	public void verTestConsumeWith() {
		Gasto gasto = service.findByDescripcion(CAMISETA_DEL_VALENCIA_CF).block();
		
		client.get().uri("/api/rf/gastos-compartits/{id}", Collections.singletonMap("id", gasto.getId()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Gasto.class)
		.consumeWith(response -> {
			Gasto g = response.getResponseBody();
			Assertions.assertThat(g.getDescripcion()).isEqualTo(CAMISETA_DEL_VALENCIA_CF);
			
		});
	}	
	@Test
	public void afegirGastoTest() {
		
		Persona persona = service.findPersonaByNombreAndApellidos("Fabiola", "Franco Salazar").block();
		
		Gasto gasto = new Gasto(persona, 0.0, CAMISETA_DEL_OSASUNA);
		
		client.post().uri("/api/rf/gastos-compartits")
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(gasto), Gasto.class)
		.exchange().expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Gasto.class)
		.consumeWith(response -> {
			Gasto g = response.getResponseBody();
			Assertions.assertThat(g.getDescripcion()).isEqualTo(CAMISETA_DEL_OSASUNA);
			
		});
	}
	@Test
	public void afegirAmicTest() {
		
		Persona persona = new Persona("Conchin", "Marí Ricart", "cumpleLorena");
		
		
		client.post().uri("/api/rf/gastos-compartits/afegir-amic")
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(persona), Persona.class)
		.exchange().expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Persona.class)
		.consumeWith(response -> {
			Persona g = response.getResponseBody();
			Assertions.assertThat(g.getNombre()).isEqualTo("Conchin");
			Assertions.assertThat(g.getGrupGastos()).isEqualTo("cumpleLorena");
			
		});
	}
	
	@Test
	public void listarBalances() {
		client.get().uri("/api/rf/gastos-compartits/balances")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBodyList(Persona.class)
		//.hasSize(5)
		.consumeWith(response -> {
			List<Persona> personas = response.getResponseBody();
			
			Assertions.assertThat(personas.size() > 0).isTrue();
			
			Optional<Persona> findJosePascual = personas.stream().filter(p -> p.getNombre().equals("José Pascual")).findAny();
			Assertions.assertThat(findJosePascual.isPresent()).isEqualTo(true);
			Assertions.assertThat(findJosePascual.get().getBalance()).isEqualTo(18.3);
		});
	}	
	
}
