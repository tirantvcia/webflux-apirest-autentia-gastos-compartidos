package com.autentia.prueba.webflux.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.autentia.prueba.webflux.app.models.document.Grup;

public interface GrupDao extends ReactiveMongoRepository<Grup, String>{

}
