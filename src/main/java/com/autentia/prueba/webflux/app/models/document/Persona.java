package com.autentia.prueba.webflux.app.models.document;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "personas")
public class Persona {
	@Id
	private String id;
	@NotEmpty
	private String nombre;
	@NotEmpty
	private String apellidos;
	@NotEmpty
	private String grupGastos;
	private Double balance;
	
//	private Grup grupGastos;
	
	public Persona() {

	}

	public Persona(String nombre, String apellidos, String grup) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.grupGastos = grup;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getGrupGastos() {
		return grupGastos;
	}

	public void setGrupGastos(String grupGastos) {
		this.grupGastos = grupGastos;
	}

	
}
