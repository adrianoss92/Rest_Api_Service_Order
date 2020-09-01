package com.algaworks.algafood.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.algaworks.algafood.domain.model.Estado;
//import com.algaworks.algafood.domain.service.CadastroEstadoService;
import com.algaworks.algafood.domain.service.impl.CadastroEstadoServiceImpl;

@RestController
@RequestMapping(value = "/estados")
public class EstadoController {
	
	@Autowired
	private CadastroEstadoServiceImpl cadastroEstadoService;
	
	@GetMapping
	public List<Estado> listar(){
		return cadastroEstadoService.listar();
	}
	
	@GetMapping("/{estadoId}")
	public ResponseEntity<Object> buscar(@PathVariable Long estadoId){
		Optional<Estado> estado = cadastroEstadoService.buscar(estadoId);
		if (estado.isPresent()) {
			return ResponseEntity.ok(estado.get()); // Forma Resumida
		}
		return ResponseEntity.notFound().build();

	}
	
	@PostMapping()
	@ResponseStatus(HttpStatus.CREATED)
	public Estado salvar(@RequestBody Estado estado) {
		return cadastroEstadoService.salvar(estado);
	}
	
	@PutMapping("/{estadoId}")
	public ResponseEntity<Estado> atualizar(@PathVariable Long estadoId, @RequestBody Estado estado){
		Optional<Estado> estadoAtualizado = cadastroEstadoService.buscar(estadoId);
		if(estadoAtualizado.isPresent()) {
			BeanUtils.copyProperties(estado, estadoAtualizado, "id");

			cadastroEstadoService.salvar(estadoAtualizado.get());
			return ResponseEntity.ok(estadoAtualizado.get());
		}
		return ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{estadoId}")
	public ResponseEntity<Estado> remover(@PathVariable Long estadoId){
		try {
			cadastroEstadoService.remover(estadoId);
			return ResponseEntity.noContent().build();
		}catch (EntidadeNaoEncontradaException e){
			return ResponseEntity.notFound().build();
		}catch (EntidadeEmUsoExeption e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}
}
