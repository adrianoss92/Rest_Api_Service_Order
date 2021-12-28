package com.algaworks.algafood.api.controller;

import java.util.List;

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
	public Estado buscar(@PathVariable Long estadoId){
		return cadastroEstadoService.buscarOuFalhar(estadoId);
	}
	
	@PostMapping()
	@ResponseStatus(HttpStatus.CREATED)
	public Estado salvar(@RequestBody Estado estado) {
		return cadastroEstadoService.salvar(estado);
	}
	
	@PutMapping("/{estadoId}")
	public Estado atualizar(@PathVariable Long estadoId, @RequestBody Estado estado){
		Estado estadoAtualizado = cadastroEstadoService.buscarOuFalhar(estadoId);

		BeanUtils.copyProperties(estado, estadoAtualizado, "id");

		return cadastroEstadoService.salvar(estadoAtualizado);
	}
	
	@DeleteMapping("/{estadoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long estadoId){

		cadastroEstadoService.remover(estadoId);

	}
}
