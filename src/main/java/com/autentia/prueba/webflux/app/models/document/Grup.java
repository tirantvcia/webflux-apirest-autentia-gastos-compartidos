package com.autentia.prueba.webflux.app.models.document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "grups")
public class Grup {
	@Id
	private String id;
	@NotEmpty
	private String nom;
	@NotNull
	private Persona administrador;
	
	
	
	public Grup() {
	}
	
	
	public Grup(@NotEmpty String nom, @NotNull Persona administrador) {
		super();
		this.nom = nom;
		this.administrador = administrador;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public Persona getAdministrador() {
		return administrador;
	}
	public void setAdministrador(Persona administrador) {
		this.administrador = administrador;
	}
	
	
	
}
