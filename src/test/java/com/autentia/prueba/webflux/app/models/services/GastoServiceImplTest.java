package com.autentia.prueba.webflux.app.models.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.autentia.prueba.webflux.app.models.dao.GastoDao;
import com.autentia.prueba.webflux.app.models.document.Gasto;
import com.autentia.prueba.webflux.app.models.document.Persona;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

//@SpringBootTest
class GastoServiceImplTest {
	
	@MockBean
	GastoDao dao;

	@Autowired
	GastoService service;
	
//	@Test
	void visualizarInfoDeUnCadaGastoRealizado() {
		Date dataCompra = new Date();
		Persona josePascual = new Persona("José Pascual", "Gimeno Marí", "cumpleLorena");
		Gasto gasto = new Gasto(josePascual, 65.50, "Camiseta del València CF");
		gasto.setCreateAt(dataCompra);
		
		when(dao.save(gasto)).thenReturn(Mono.just(gasto));
//		when(dao.save(any(Gasto.class))).thenReturn(Mono.just(gasto));	
		Mono<Gasto> monoGastoDB = service.save(gasto);
	

        StepVerifier.create(monoGastoDB)
        .consumeNextWith(gastoDB -> {
        	assertEquals("José Pascual", gastoDB.getPersona().getNombre());
        	assertEquals(65.50, gastoDB.getImporte());
        	assertEquals("Camiseta del València CF", gastoDB.getDescripcion());
        	assertEquals(dataCompra, gastoDB.getCreateAt());
        })
        .verifyComplete();

	}

//	@Test
	void visualizarTotalGastoRealizado() {
		
		Persona josePascual = new Persona("José Pascual", "Gimeno Marí", "cumpleLorena");
		Persona fabiola = new Persona("Fabiola", "Franco Salazar", "cumpleLorena");
		Persona sandra = new Persona("Sandra", "Gimeno Franco", "cumpleLorena");
		Persona lorena = new Persona("Lorena", "Franco Franco", "cumpleLorena");
		
		List<Gasto> gastos = Arrays.asList(
				new Gasto(josePascual, 9.00, "Camiseta del Numancia CF"),
				new Gasto(josePascual, 50.15, "Camiseta del València CF"),
				new Gasto(fabiola, 11.55, "Camiseta del Barça"),
				new Gasto(fabiola, 11.00, "Camiseta del Mallorca CF"),
				new Gasto(sandra, 40.85, "Camiseta del Atlco Madrid CF"),
				new Gasto(lorena, 40.85, "Camiseta del Elche CF"));
		
		
		

		when(dao.findAll()).thenReturn(Flux.fromIterable(gastos));
	    Flux<Gasto> resultFindAll = service.findAll();
		
		StepVerifier.create(resultFindAll)
		.expectNext(new Gasto(josePascual, 9.00, "Camiseta del Numancia CF"))
		.expectNext(new Gasto(josePascual, 50.15, "Camiseta del València CF"))
		.expectNext(new Gasto(fabiola, 11.55, "Camiseta del Barça"))
		.expectNext(new Gasto(fabiola, 11.00, "Camiseta del Mallorca CF"))
		.expectNext(new Gasto(sandra, 40.85, "Camiseta del Atlco Madrid CF"))
		.expectNext(new Gasto(lorena, 40.85, "Camiseta del Elche CF"))
		.expectComplete()
		.verify();
		
		// Verify that the findAll method was called
		verify(dao).findAll();
		
		assertEquals(163.4, service.calcularGastoTotal());
		
		assertEquals(40.85, service.calcularGastoPromedio());
		
 


	}
}
