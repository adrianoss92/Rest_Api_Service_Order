package com.algaworks.algafood.api.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.domain.exeption.EntidadeEmUsoExeption;
import com.algaworks.algafood.domain.exeption.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.service.CadastroCozinhaService;

@RestController // é possivel informar o tipo de media que todas as chamadas poderão receber
				// inserindo o MediaType já na RequestMapping
@RequestMapping(value = "/cozinhas")
public class CozinhaController {

	
	@Autowired
	private CadastroCozinhaService cadastroCozinhaService;

	// @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE) // esta variação do
	// GetMapping serve para
	// Informar que o este metodo ira produzir uma media do tipo Json, porem é
	// possivel inserir outras medias para o qual ->
	// o dado poderar ser transformado como xml e por ai vai...
	@GetMapping
	public List<Cozinha> listar() {
		return cadastroCozinhaService.listar();
	}

//	@GetMapping(produces = MediaType.APPLICATION_ATOM_XML_VALUE)
//	public CozinhasXmlWrapper listarCozinhas() {
//		return new CozinhasXmlWrapper(cozinhaRepository.listar());
//	}

//	@ResponseStatus(HttpStatus.BAD_GATEWAY)
	@GetMapping(value = "/{cozinhaId}")
	public ResponseEntity<Cozinha> buscar(@PathVariable Long cozinhaId) {
		// Para pegar uma informação do path que é variavel é possivel fazela utilizando
		// o parametro @PathVariable onde ele sabe que o valor inserido
		// após o cozinhas/ ira mudar, logo ele pega o valor passado no path e atribui a
		// variavel que possui o mesmo nome
		Cozinha cozinha = cadastroCozinhaService.buscar(cozinhaId);
//		return ResponseEntity.status(HttpStatus.OK).body(cozinha);  Forma completa da notação da resposta mudando também o
		// codigo do statusCode
		if (cozinha != null) {
			return ResponseEntity.ok(cozinha); // Forma Resumida
		}
		return ResponseEntity.notFound().build(); // Forma Resumida
//		HttpHeaders headers = new HttpHeaders(); Utilizando a classe HttpHeaders é possivel setar qualquer informação
//		// o Header da resposta
//		headers.add(HttpHeaders.LOCATION, "Novo link para acessar o a pagina"); // como neste exemplo
//		return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Cozinha adcionar(@RequestBody Cozinha cozinha) {
		return cadastroCozinhaService.salvar(cozinha);
	}

	@PutMapping("/{cozinhaId}")
	public ResponseEntity<Cozinha> atualizar(@PathVariable Long cozinhaId, @RequestBody Cozinha cozinha) {

		Cozinha cozinhaAtual = cadastroCozinhaService.buscar(cozinhaId);
		if (cozinhaAtual != null) {
			cozinhaAtual.setNome(cozinha.getNome()); // Forma normal de atualizar os campos, porem se for um objeto
														// muito grande isso acabara ficando dificil
			// pois vamos ter que fazer este processo para cada atributo do projeto.

			// Existe uma forma mais facil de fazer esta atualização que é utilizando um
			// cara do Spring como mostra abaixo:
			BeanUtils.copyProperties(cozinha, cozinhaAtual, "id"); // Este Bean do Spring faz o seguinte, atualiza as
																	// informações de um objeto com base em um objeto
																	// atual
			// onde o primeiro objeto sera o objeto que esta com as informações que serão
			// atualizadas no outro objeto.
			// no terceiro parametro é passada informações que deverão ser ignoradas para
			// não sofrerem alteração.

			cadastroCozinhaService.salvar(cozinhaAtual);

			return ResponseEntity.ok(cozinhaAtual);

		}

		return ResponseEntity.notFound().build();

	}

	@DeleteMapping("/{cozinhaId}")
	public ResponseEntity<Cozinha> remover(@PathVariable Long cozinhaId) {
		try {
			cadastroCozinhaService.remover(cozinhaId);
			return ResponseEntity.noContent().build();
		}catch (EntidadeNaoEncontradaException e){
			return ResponseEntity.notFound().build();
		}catch (EntidadeEmUsoExeption e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}

	}
}
