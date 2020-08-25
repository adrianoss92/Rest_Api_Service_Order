package com.algaworks.algafood.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algaworks.algafood.domain.exeption.EntidadeEmUsoExeption;
import com.algaworks.algafood.domain.exeption.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Estado;
import com.algaworks.algafood.domain.repository.EstadoRepository;
import com.algaworks.algafood.domain.service.CadastroEstadoService;

@Service
public class CadastroEstadoServiceImpl implements CadastroEstadoService {
	
	@Autowired
	private EstadoRepository estadoRepository;
	
	public List<Estado> listar(){
		return estadoRepository.listar();
	}
	
	public Estado buscar(Long id) {
		return estadoRepository.buscar(id);
	}
	
	public Estado salvar(Estado estado) {
		return estadoRepository.salvar(estado);
	}
	public void remover(Long id) {
		try {
			estadoRepository.remover(id);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não localizado o cadastro do Estado de código: %d ", id));
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoExeption(
					String.format("Estado de código %d não pode ser removida, pois está em uso", id));
		}
	}
	
}
