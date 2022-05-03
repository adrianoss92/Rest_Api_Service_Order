package com.algaworks.algafood.domain.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.exeption.CozinhaNaoEncontradaException;
import com.algaworks.algafood.domain.exeption.EntidadeEmUsoExeption;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;

@Service
public class CadastroCozinhaServiceImpl {

	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	public List<Cozinha> listar(){
		return cozinhaRepository.findAll();
	}
	
	public Optional<Cozinha> buscar(Long id) {
		return cozinhaRepository.findById(id);
	}
	
	public Cozinha salvar(Cozinha cozinha) {
		return cozinhaRepository.save(cozinha);
	}
	
	@Transactional
	public void remover(Long id) {
		try {
			cozinhaRepository.deleteById(id); // quando executamos a função do delete do jpa, ele enfilera os commits e em algum momento ele descarrega estas alterações no banco de dados
			// geralmente este commit pode demorar caso existam varias outras commits concorrendo ente si para ser executado no banco e para forçar a descarga de forma prioritaria do commit
			//é possivel utilizar o comando "flush" como demonstrado abaixo, que desta forma o JPA executara este comando com prioridade maxima.
			cozinhaRepository.flush();
		} catch (EmptyResultDataAccessException e) {
			throw new CozinhaNaoEncontradaException(id);
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoExeption(
					String.format("Cozinha de código %d não pode ser removida, pois está em uso", id));
		}
	}
	
	public Cozinha buscarOuFalhar(Long cozinhaId) {
		return cozinhaRepository.findById(cozinhaId)
				.orElseThrow(() -> new CozinhaNaoEncontradaException(cozinhaId));
	}

}
