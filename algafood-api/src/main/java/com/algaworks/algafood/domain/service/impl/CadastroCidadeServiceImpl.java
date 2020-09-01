package com.algaworks.algafood.domain.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algaworks.algafood.domain.exeption.EntidadeEmUsoExeption;
import com.algaworks.algafood.domain.exeption.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.model.Estado;
import com.algaworks.algafood.domain.repository.CidadeRepository;
//import com.algaworks.algafood.domain.service.CadastroCidadeService;
//import com.algaworks.algafood.domain.service.CadastroEstadoService;

@Service
public class CadastroCidadeServiceImpl {

	@Autowired
	private CidadeRepository cidadeRepository;

	@Autowired
	private CadastroEstadoServiceImpl cadastroEstadoService;

	public List<Cidade> listar() {
		return cidadeRepository.findAll();
	}

	public Optional<Cidade> buscar(Long id) {
		return cidadeRepository.findById(id);
	}

	public Cidade salvar(Cidade cidade) {
		Long estadoId = cidade.getEstado().getId();
		Estado estado = cadastroEstadoService.buscar(estadoId)
				.orElseThrow(() 
				->new EntidadeNaoEncontradaException(String.format("Não existe cadastro de cozinha com código: %d", estadoId)));

		cidade.setEstado(estado);
		return cidadeRepository.save(cidade);
	}

	public void remover(Long id) {
		try {
			cidadeRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não localizado o cadastro do Estado de código: %d ", id));
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoExeption(
					String.format("Cidade de código %d não pode ser removida, pois está em uso", id));
		}
	}
}
