package com.autentia.prueba.webflux.app.models.document;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;



@Document(collection = "gastos")
public class Gasto {
	@Id
	private String id;

	@Valid
	@NotNull
	private Persona persona;
	
	@NotNull
	private Double importe;
	
	@NotEmpty 
	private String descripcion;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createAt;
	
	
	public Gasto() {
	}


	public Gasto(Persona persona, Double importe, String descripcion) {
		this.persona = persona;
		this.importe = importe;
		this.descripcion = descripcion;
		this.createAt = new Date();
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Persona getPersona() {
		return persona;
	}


	public void setPersona(Persona persona) {
		this.persona = persona;
	}


	public Double getImporte() {
		return importe;
	}


	public void setImporte(Double importe) {
		this.importe = importe;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public Date getCreateAt() {
		return createAt;
	}


	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	
}
