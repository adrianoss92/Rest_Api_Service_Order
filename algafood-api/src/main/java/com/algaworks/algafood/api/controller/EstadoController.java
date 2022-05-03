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

import com.algaworks.algafood.api.assembler.EstadoInputDisassembler;
import com.algaworks.algafood.api.assembler.EstadoModelAssembler;
import com.algaworks.algafood.api.model.EstadoModel;
import com.algaworks.algafood.api.model.input.EstadoInput;
import com.algaworks.algafood.domain.model.Estado;
//import com.algaworks.algafood.domain.service.CadastroEstadoService;
import com.algaworks.algafood.domain.service.impl.CadastroEstadoServiceImpl;

@RestController
@RequestMapping(value = "/estados")
public class EstadoController {
	
	@Autowired
	private CadastroEstadoServiceImpl cadastroEstadoService;
	
	@Autowired
	private EstadoModelAssembler estadoModelAssembler;

	@Autowired
	private EstadoInputDisassembler estadoInputDisassembler;
	
	@GetMapping
	public List<EstadoModel> listar(){
		List<Estado> todosEstados = cadastroEstadoService.listar();
		return estadoModelAssembler.toCollectionModel(todosEstados);
	}
	
	@GetMapping("/{estadoId}")
	public EstadoModel buscar(@PathVariable Long estadoId){
		Estado estado =  cadastroEstadoService.buscarOuFalhar(estadoId);
		return estadoModelAssembler.toModel(estado);
	}
	
	@PostMapping()
	@ResponseStatus(HttpStatus.CREATED)
	public EstadoModel salvar(@RequestBody @Valid EstadoInput estadoInput) {
		Estado estado = estadoInputDisassembler.toDomainObject(estadoInput);
		
		estado = cadastroEstadoService.salvar(estado);
		
		return estadoModelAssembler.toModel(estado);
	}
	
	@PutMapping("/{estadoId}")
	public EstadoModel atualizar(@PathVariable Long estadoId, @RequestBody @Valid EstadoInput estadoInput){
		Estado estadoAtual = cadastroEstadoService.buscarOuFalhar(estadoId);

		estadoInputDisassembler.copyToDomainObject(estadoInput, estadoAtual);
		// BeanUtils.copyProperties(estado, estadoAtualizado, "id");

		estadoAtual = cadastroEstadoService.salvar(estadoAtual);
		return estadoModelAssembler.toModel(estadoAtual);
	}
	
	@DeleteMapping("/{estadoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long estadoId){

		cadastroEstadoService.remover(estadoId);

	}
}
