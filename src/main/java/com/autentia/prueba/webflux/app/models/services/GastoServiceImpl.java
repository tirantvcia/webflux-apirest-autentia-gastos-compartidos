package com.autentia.prueba.webflux.app.models.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.autentia.prueba.webflux.app.models.dao.GastoDao;
import com.autentia.prueba.webflux.app.models.dao.PersonaDao;
import com.autentia.prueba.webflux.app.models.document.Gasto;
import com.autentia.prueba.webflux.app.models.document.Persona;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GastoServiceImpl implements GastoService {
	
	private static Logger log = LoggerFactory.getLogger(GastoServiceImpl.class);

	@Autowired
	private GastoDao dao;
	
	@Autowired
	private PersonaDao personaDao;
	
	@Override
	public Flux<Gasto> findAll() {
		 Flux<Gasto> findAllGastos = dao.findAll(Sort.by(Sort.Direction.DESC, "createAt"));
		 return findAllGastos;
	}
	@Override
	public Flux<Gasto> findAllWithNameUpperCase() {
		return dao.findAll().map(pr -> {
			  pr.setDescripcion(pr.getDescripcion().toUpperCase());
			  return pr;
			});
	}
	@Override
	public Mono<Gasto> findById(String id) {
		return dao.findById(id);
	}

	@Override
	public Mono<Gasto> save(Gasto gasto) {
		if(gasto.getCreateAt() == null) {
			gasto.setCreateAt(new Date());
		}
		return dao.save(gasto);
	}

	@Override
	public Mono<Void> delete(Gasto gasto) {
		return dao.delete(gasto);
	}
	@Override
	public Flux<Gasto> findAllWithRepeats(long numRepeat) {
		return findAllWithNameUpperCase().repeat(numRepeat);
	}
	@Override
	public Flux<Persona> findAllPersona() {
		return personaDao.findAll();
	}
	@Override
	public Mono<Persona> findPersonaById(String id) {
		return personaDao.findById(id);
	}
	@Override
	public Mono<Persona> savePersona(Persona persona) {
		return personaDao.save(persona);
	}
	@Override
	public Mono<Gasto> findByDescripcion(String descripcion) {
		return dao.findByDescripcion(descripcion);
	}
	@Override
	public Mono<Persona> findPersonaByNombreAndApellidos(String nombre, String cogNoms) {
		return personaDao.findPersonaByNombreAndApellidos(nombre, cogNoms);
	}
	@Override
	public Mono<Double> calcularGastoTotal() {
		Flux<Gasto> gastos = findAll();
		Mono<Double> total =  gastos.
			map(gasto -> gasto.getImporte())
			.reduce(0.0, Double::sum);
			total
			.subscribe(s -> log.info("total " + s.toString()), e -> log.error(e.getMessage()));
			
		
		return total;
	}
	@Override
	public Mono<Double> calcularGastoPromedio() {
		
		Mono<Long> numPersonas = findAllPersona().count();
		Mono<Double> gastoTotal = calcularGastoTotal();
		
		Mono<Double> promedio = gastoTotal.zipWith(numPersonas)
				.map(tupla -> {
					Double total = tupla.getT1();
					Long numPer = tupla.getT2();
					
					return toTwoDecimals(total / numPer);
				});
		promedio
		.subscribe(s -> log.info("Promedio por persona" + s.toString()), e -> log.error(e.getMessage()));

		return promedio;
	}
	private Double toTwoDecimals(double input) {
		 BigDecimal bigInputTwoDecimals = new BigDecimal(input).setScale(2, RoundingMode.HALF_UP);
		return Double.valueOf(bigInputTwoDecimals.doubleValue());
	}
	@Override
	public Flux<Persona> obtenerBalancePorPersona() {
		Mono<Double> promedioGastosCompartidos = calcularGastoPromedio() ;
		 Flux<Persona> personas = findAllPersona().flatMap(
				p -> findGastosByPersona(p)
		        	.map(g -> g.getImporte())
		        	.reduce(0.0, Double::sum)
		        	.zipWith(promedioGastosCompartidos)
		        	.map(tupla -> {
		        		Double sumGastos = tupla.getT1();
		        		Double promedio = tupla.getT2();
		        		p.setBalance(toTwoDecimals(sumGastos - promedio));
		        		return p;
		        	}))
				;
		 personas.subscribe(s -> log.info(s.getNombre() + ": " + s.getBalance().toString()), e -> log.error(e.getMessage()));
		 return personas;
	}
	@Override
	public Flux<Gasto> findGastosByPersona(Persona persona) {
		return dao.findGastosByPersona(persona);
	}



}
