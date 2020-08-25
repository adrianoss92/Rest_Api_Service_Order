package com.algaworks.algafood.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algaworks.algafood.domain.exeption.EntidadeEmUsoExeption;
import com.algaworks.algafood.domain.exeption.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.model.Estado;
import com.algaworks.algafood.domain.repository.CidadeRepository;
import com.algaworks.algafood.domain.service.CadastroCidadeService;
import com.algaworks.algafood.domain.service.CadastroEstadoService;

@Service
public class CadastroCidadeServiceImpl implements CadastroCidadeService {

	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private CadastroEstadoService cadastroEstadoService;
	
	public List<Cidade> listar(){
		return cidadeRepository.listar();
	}
	
	public Cidade buscar(Long id) {
		return cidadeRepository.buscar(id);
	}
	
	public Cidade salvar(Cidade cidade) {
		Long cidadeId = cidade.getEstado().getId();
		Estado estado = cadastroEstadoService.buscar(cidadeId);
		if(estado == null) {
			throw new EntidadeNaoEncontradaException(String.format("Não existe cadastro de Estado com código: %d", cidadeId));
		}
		cidade.setEstado(estado);;
		return cidadeRepository.salvar(cidade);
	}
	public void remover(Long id) {
		try {
			cidadeRepository.remover(id);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não localizado o cadastro do Estado de código: %d ", id));
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoExeption(
					String.format("Cidade de código %d não pode ser removida, pois está em uso", id));
		}
	}
}
