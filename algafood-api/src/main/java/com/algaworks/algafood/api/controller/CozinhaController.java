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

import com.algaworks.algafood.api.assembler.CozinhaInputDisassembler;
import com.algaworks.algafood.api.assembler.CozinhaModelAssembler;
import com.algaworks.algafood.api.model.CozinhaModel;
import com.algaworks.algafood.api.model.input.CozinhaInput;
import com.algaworks.algafood.domain.model.Cozinha;
//import com.algaworks.algafood.domain.service.CadastroCozinhaService;
import com.algaworks.algafood.domain.service.impl.CadastroCozinhaServiceImpl;

@RestController // é possivel informar o tipo de media que todas as chamadas poderão receber
				// inserindo o MediaType já na RequestMapping
@RequestMapping(value = "/cozinhas")
public class CozinhaController {

	@Autowired
	private CadastroCozinhaServiceImpl cadastroCozinhaService;
	
	@Autowired
	private CozinhaModelAssembler cozinhaModelAssembler;

	@Autowired
	private CozinhaInputDisassembler cozinhaInputDisassembler;

	// @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE) // esta variação do GetMapping serve para informar que o este metodo ira produzir uma media do tipo Json, porem é 
	// possivel inserir outras medias para o qual o dado poderar ser transformado como xml e por ai vai...
	@GetMapping
	public List<CozinhaModel> listar() {
		List<Cozinha> todasCozinhas =  cadastroCozinhaService.listar();
		
		return cozinhaModelAssembler.toCollectionModel(todasCozinhas);
	}

//	@ResponseStatus(HttpStatus.BAD_GATEWAY)
	@GetMapping(value = "/{cozinhaId}")
	public CozinhaModel buscar(@PathVariable Long cozinhaId) {
		 Cozinha cozinha =  cadastroCozinhaService.buscarOuFalhar(cozinhaId);
		 
		 return cozinhaModelAssembler.toModel(cozinha);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CozinhaModel adcionar(@RequestBody @Valid CozinhaInput cozinhaInput) {
		Cozinha cozinha = cozinhaInputDisassembler.toDomainObject(cozinhaInput);
		cozinha = cadastroCozinhaService.salvar(cozinha);
		
		return cozinhaModelAssembler.toModel(cozinha);
	}

	@PutMapping("/{cozinhaId}")
	public CozinhaModel atualizar(@PathVariable Long cozinhaId, @RequestBody @Valid CozinhaInput cozinhaInput) {
		Cozinha cozinhaAtual = cadastroCozinhaService.buscarOuFalhar(cozinhaId);
		
		cozinhaInputDisassembler.copyToDomainObject(cozinhaInput, cozinhaAtual);
		
		
//		BeanUtils.copyProperties(cozinha, cozinhaAtual, "id");
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


		cozinhaAtual = cadastroCozinhaService.salvar(cozinhaAtual);
		return cozinhaModelAssembler.toModel(cozinhaAtual);
	}
	
	@DeleteMapping("/{cozinhaId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removerTeste(@PathVariable Long cozinhaId) {
			cadastroCozinhaService.remover(cozinhaId);
	}
}
