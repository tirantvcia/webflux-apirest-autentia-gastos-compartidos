package com.autentia.prueba.webflux.app;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.autentia.prueba.webflux.app.handler.GastoHandler;

@Configuration
public class RouterFunctionConfig {

	@Autowired
	private GastoHandler handler;
	
	@Bean
	public RouterFunction<ServerResponse> routes () {
		return route(GET("/api/rf/gastos-compartits"), handler::listar)
				.andRoute(GET("/api/rf/gastos-compartits/balances"), handler::obtenerBalances)
				.andRoute(GET("/api/rf/gastos-compartits/{id}"), handler::mostrar)
				.andRoute(POST("/api/rf/gastos-compartits"), handler::guardar)
				.andRoute(POST("/api/rf/gastos-compartits/afegir-amic"), handler::afegirAmic)
				.andRoute(PUT("/api/rf/gastos-compartits/{id}"), handler::editar)
				.andRoute(DELETE("/api/rf/gastos-compartits/{id}"), handler::eliminar);
	}
}
