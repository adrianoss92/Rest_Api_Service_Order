package com.algaworks.algafood.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;

@RestController
// é possivel informar o tipo de media que todas as chamadas poderão receber inserindo o MediaType já na RequestMapping

@RequestMapping(value = "/cozinhas", produces = MediaType.APPLICATION_JSON_VALUE) 
public class CozinhaController {

	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	// @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE) // esta variação do GetMapping serve para 
	// Informar que o este metodo ira produzir uma media do tipo Json, porem é possivel inserir outras medias para o qual ->
	// o dado poderar ser transformado como xml e por ai vai...
	@GetMapping
	public List<Cozinha> listar() {
		return cozinhaRepository.listar();
	}
	
	@GetMapping(value = "/{cozinhaId}")
	public Cozinha buscar(@PathVariable Long cozinhaId) {
		// Para pegar uma informação do path que é variavel é possivel fazela utilizando o parametro @PathVariable onde ele sabe que o valor inserido
		// após o cozinhas/ ira mudar, logo ele pega o valor passado no path e atribui a variavel que possui o mesmo nome
		return cozinhaRepository.buscar(cozinhaId);
	}
	
}
