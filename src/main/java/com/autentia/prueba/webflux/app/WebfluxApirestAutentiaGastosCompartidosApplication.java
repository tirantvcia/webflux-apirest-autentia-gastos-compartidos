package com.autentia.prueba.webflux.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.autentia.prueba.webflux.app.models.document.Gasto;
import com.autentia.prueba.webflux.app.models.document.Persona;
import com.autentia.prueba.webflux.app.models.services.GastoService;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class WebfluxApirestAutentiaGastosCompartidosApplication implements CommandLineRunner{

	private static final Logger log = LoggerFactory.getLogger(WebfluxApirestAutentiaGastosCompartidosApplication.class);
	
	@Autowired
	private GastoService service;
	
	@Autowired
	private ReactiveMongoTemplate mongoTemplate;
	
	public static void main(String[] args) {
		SpringApplication.run(WebfluxApirestAutentiaGastosCompartidosApplication.class, args);
	}

	public void run(String... args) throws Exception {
		
		mongoTemplate.dropCollection("personas").subscribe();
		mongoTemplate.dropCollection("gastos").subscribe();
		
	
		
		Persona josePascual = new Persona("José Pascual", "Gimeno Marí", "cumpleLorena");
		Persona fabiola = new Persona("Fabiola", "Franco Salazar", "cumpleLorena");
		Persona sandra = new Persona("Sandra", "Gimeno Franco", "cumpleLorena");
		Persona lorena = new Persona("Lorena", "Franco Franco", "cumpleLorena");
		
		Flux.just(josePascual, fabiola, sandra, lorena)
			.flatMap(service::savePersona)
			.doOnNext(p -> {
				log.info("Persona creada " + p.getNombre());
			})
			.thenMany(		
					Flux.just(
							new Gasto(josePascual, 9.00, "Camiseta del Numancia CF"),
							new Gasto(josePascual, 50.15, "Camiseta del València CF"),
							new Gasto(fabiola, 11.55, "Camiseta del Barça"),
							new Gasto(fabiola, 11.00, "Camiseta del Mallorca CF"),
							new Gasto(sandra, 20.85, "Camiseta del Atlco Madrid CF"),
							new Gasto(sandra, 20.00, "Camiseta del Atlco Mandril CF"),
							new Gasto(lorena, 20.85, "Camiseta del Elche CF"),
							new Gasto(lorena, 20.00, "Camiseta del Hercules CF")
					)
					.flatMap(service::save)
					)
		.subscribe(gasto -> log.info("gasto de "+ gasto.getImporte() +" per a " + gasto.getPersona().getNombre() + " del grup " + gasto.getPersona().getGrupGastos()));
	}

}
