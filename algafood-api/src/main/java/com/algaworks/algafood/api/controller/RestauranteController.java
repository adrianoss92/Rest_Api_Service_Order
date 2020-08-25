package com.algaworks.algafood.api.controller;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.domain.exeption.EntidadeEmUsoExeption;
import com.algaworks.algafood.domain.exeption.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.service.CadastroRestauranteService;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

	@Autowired
	private CadastroRestauranteService cadastroRestauranteService;

	@GetMapping
	public List<Restaurante> listar() {
		return cadastroRestauranteService.listar();
	}

	@GetMapping(value = "/{restauranteId}")
	public ResponseEntity<Restaurante> buscar(@PathVariable Long restauranteId) {
		Restaurante restaurante = cadastroRestauranteService.buscar(restauranteId);

		if (restaurante != null) {
			return ResponseEntity.ok(restaurante);
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping
	public ResponseEntity<?> salvar(@RequestBody Restaurante restaurante) {
		try {
			restaurante = cadastroRestauranteService.salvar(restaurante);
			return ResponseEntity.status(HttpStatus.CREATED).body(restaurante);
		} catch (EntidadeNaoEncontradaException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/{restauranteId}")
	public ResponseEntity<?> atualizar(@PathVariable Long restauranteId,
			@RequestBody Restaurante restaurante) {
		try {
			Restaurante restauranteAtual = cadastroRestauranteService.buscar(restauranteId);
			if (restauranteAtual != null) {
//				restauranteAtual.setNome(restaurante.getNome());
				BeanUtils.copyProperties(restaurante, restauranteAtual, "id");
				cadastroRestauranteService.salvar(restauranteAtual);
				return ResponseEntity.ok(restauranteAtual);
			}
			return ResponseEntity.notFound().build();
		} catch (EntidadeNaoEncontradaException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@DeleteMapping("/{restauranteId}")
	public ResponseEntity<Restaurante> remover(@PathVariable Long restauranteId){
		try {
			cadastroRestauranteService.remover(restauranteId);
			return ResponseEntity.noContent().build();
		}catch (EntidadeNaoEncontradaException e){
			return ResponseEntity.notFound().build();
		}catch (EntidadeEmUsoExeption e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}
	
	@PatchMapping("/{restauranteId}")
	public ResponseEntity<?> atualizarParcial(@PathVariable Long restauranteId, 
			@RequestBody Map<String, Object> campos){
		Restaurante restauranteAtual = cadastroRestauranteService.buscar(restauranteId);
		if(restauranteAtual == null) {
			return ResponseEntity.notFound().build();
		}
		merge(campos, restauranteAtual);
		
		return atualizar(restauranteId, restauranteAtual);
	}

	private void merge(Map<String, Object> campos, Restaurante restaurateDestino) { 
		campos.forEach((nomePropriedade, valorPropriedade) -> {
			// Metodo utilizado para varrer cada atributo do objeto e atribuindo o valor que foi passado apra aquele campo
			// assim atualizando apenas os campos que foram enviados na chamada 
			Field field = ReflectionUtils.findField(Restaurante.class, nomePropriedade);
			
			System.out.println(nomePropriedade + " = " + valorPropriedade);
			
			ReflectionUtils.setField(field, restaurateDestino, valorPropriedade);
			
		});
	}
}
