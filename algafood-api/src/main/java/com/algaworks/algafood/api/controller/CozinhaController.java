package com.algaworks.algafood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.domain.model.Cozinha;
//import com.algaworks.algafood.domain.service.CadastroCozinhaService;
import com.algaworks.algafood.domain.service.impl.CadastroCozinhaServiceImpl;

@RestController // é possivel informar o tipo de media que todas as chamadas poderão receber
				// inserindo o MediaType já na RequestMapping
@RequestMapping(value = "/cozinhas")
public class CozinhaController {

	@Autowired
	private CadastroCozinhaServiceImpl cadastroCozinhaService;

	// @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE) // esta variação do
	// GetMapping serve para
	// Informar que o este metodo ira produzir uma media do tipo Json, porem é
	// possivel inserir outras medias para o qual ->
	// o dado poderar ser transformado como xml e por ai vai...
	@GetMapping
	public List<Cozinha> listar() {
		return cadastroCozinhaService.listar();
	}

//	@ResponseStatus(HttpStatus.BAD_GATEWAY)
	@GetMapping(value = "/{cozinhaId}")
	public Cozinha buscar(@PathVariable Long cozinhaId) {
		return cadastroCozinhaService.buscarOuFalhar(cozinhaId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Cozinha adcionar(@RequestBody @Valid Cozinha cozinha) {
		return cadastroCozinhaService.salvar(cozinha);
	}

	@PutMapping("/{cozinhaId}")
	public Cozinha atualizar(@PathVariable Long cozinhaId, @RequestBody @Valid Cozinha cozinha) {
		Cozinha cozinhaAtual = cadastroCozinhaService.buscarOuFalhar(cozinhaId);
		
	
			BeanUtils.copyProperties(cozinha, cozinhaAtual, "id");
			// Este Bean do Spring faz o seguinte, atualiza as
			// informações de um objeto com base em um objeto atual
			// onde o primeiro objeto sera o objeto que esta com as informações que serão
			// atualizadas no outro objeto.
			// no terceiro parametro é passada informações que deverão ser ignoradas para
			// não sofrerem alteração.
			
			//cozinhaAtual.setNome(cozinha.getNome()); 
			// Forma normal de atualizar os campos, porem se for um objeto
			// muito grande isso acabara ficando dificil
			// pois vamos ter que fazer este processo para cada atributo do projeto.

			// Existe uma forma mais facil de fazer esta atualização que é utilizando um
			// cara do Spring como mostra abaixo:


			return cadastroCozinhaService.salvar(cozinhaAtual);
	}
	
	@DeleteMapping("/{cozinhaId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removerTeste(@PathVariable Long cozinhaId) {
			cadastroCozinhaService.remover(cozinhaId);
	}
}
