package com.algaworks.algafood.domain.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.exeption.CidadeNaoEncontradaException;
import com.algaworks.algafood.domain.exeption.EntidadeEmUsoExeption;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.model.Estado;
import com.algaworks.algafood.domain.repository.CidadeRepository;


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
	
	@Transactional // esta anotação é utilizada para iniciar uma transação no banco, quando é realizada altum tipo de inclusão ou alteração de dados e para ter uma maior garantia, onde em casos que existam
	//uma cadeia de chamadas para o banco de dados e uma delas apresentar erro, isso garante que, quando o jpa estiver fazendo o rollback da chamada realizada as demais transações não sejam executadas para ter
	// uma maior garantia de que o dado esta da forma esperada.
	public Cidade salvar(Cidade cidade) {
		Long estadoId = cidade.getEstado().getId();
		Estado estado = cadastroEstadoService.buscarOuFalhar(estadoId);
		
		cidade.setEstado(estado);
		return cidadeRepository.save(cidade);
	}
	
	@Transactional // esta anotação é utilizada para iniciar uma transação no banco, quando é realizada altum tipo de inclusão ou alteração de dados e para ter uma maior garantia, onde em casos que existam
	//uma cadeia de chamadas para o banco de dados e uma delas apresentar erro, isso garante que, quando o jpa estiver fazendo o rollback da chamada realizada as demais transações não sejam executadas para ter
	// uma maior garantia de que o dado esta da forma esperada.
	public void remover(Long id) {
		try {
			cidadeRepository.deleteById(id); // quando executamos a função do delete do jpa, ele enfilera os commits e em algum momento ele descarrega estas alterações no banco de dados
			// geralmente este commit pode demorar caso existam varias outras commits concorrendo ente si para ser executado no banco e para forçar a descarga de forma prioritaria do commit
			//é possivel utilizar o comando "flush" como demonstrado abaixo, que desta forma o JPA executara este comando com prioridade maxima.
			cidadeRepository.flush();
		} catch (EmptyResultDataAccessException e) {
			throw new CidadeNaoEncontradaException(id);
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoExeption(
					String.format("Cidade de código %d não pode ser removida, pois está em uso", id));
		}
	}
	
	public Cidade buscarOuFalhar(Long id) {
		return cidadeRepository.findById(id)
				.orElseThrow(() ->new CidadeNaoEncontradaException(id));
	}
}
