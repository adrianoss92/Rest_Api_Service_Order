package com.algaworks.algafood.domain.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algaworks.algafood.domain.exeption.EntidadeEmUsoExeption;
import com.algaworks.algafood.domain.exeption.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
//import com.algaworks.algafood.domain.service.CadastroCozinhaService;
//import com.algaworks.algafood.domain.service.CadastroRestauranteService;

@Service
public class CadastroRestauranteServiceImpl {

	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CadastroCozinhaServiceImpl cadastroCozinhaService; 
	
	public List<Restaurante> listar(){
		return restauranteRepository.findAll();
	}
	
	public Optional<Restaurante> buscar(Long id) {
		return restauranteRepository.findById(id);
	}
	
	public Restaurante salvar(Restaurante restaurante) {
		Long cozinhaId = restaurante.getCozinha().getId();
		Cozinha cozinha = cadastroCozinhaService.buscar(cozinhaId)
				.orElseThrow(() ->   
				new EntidadeNaoEncontradaException(String.format("Não existe cadastro de cozinha com código: %d", cozinhaId)));
		// No código acima, caso não retornar nenhuma cozinha ele ira retornar o erro de Entidade não encontrada.
		restaurante.setCozinha(cozinha);
		return restauranteRepository.save(restaurante);
	}
	public void remover(Long id) {
		try {
			restauranteRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não localizado o cadastro do restaurante de código: %d ", id));
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoExeption(
					String.format("Restaurante de código %d não pode ser removida, pois está em uso", id));
		}
	}
}
