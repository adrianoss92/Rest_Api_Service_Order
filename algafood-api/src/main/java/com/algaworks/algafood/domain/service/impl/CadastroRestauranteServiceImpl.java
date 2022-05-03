package com.algaworks.algafood.domain.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.exeption.EntidadeEmUsoExeption;
import com.algaworks.algafood.domain.exeption.RestauranteNaoEncontradoException;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.FormaPagamento;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.model.Usuario;
import com.algaworks.algafood.domain.repository.RestauranteRepository;

@Service
public class CadastroRestauranteServiceImpl {

	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CadastroCozinhaServiceImpl cadastroCozinhaService; 
	
	@Autowired
	private CadastroCidadeServiceImpl cadastroCidade;
	
	@Autowired
	private CadastroFormaPagamentoServiceImpl cadastroFormaPagamento;
	
	@Autowired
	private CadastroUsuarioServiceImpl cadastroUsuario;
	
	public List<Restaurante> listar(){
		return restauranteRepository.findAll();
	}
	
	public Optional<Restaurante> buscar(Long id) {
		return restauranteRepository.findById(id);
	}
	
	@Transactional // esta anotação é utilizada para iniciar uma transação no banco, quando é realizada altum tipo de inclusão ou alteração de dados e para ter uma maior garantia, onde em casos que existam
	//uma cadeia de chamadas para o banco de dados e uma delas apresentar erro, isso garante que, quando o jpa estiver fazendo o rollback da chamada realizada as demais transações não sejam executadas para ter
	// uma maior garantia de que o dado esta da forma esperada.
	public Restaurante salvar(Restaurante restaurante) {
		Long cozinhaId = restaurante.getCozinha().getId();
		Long cidadeId = restaurante.getEndereco().getCidade().getId();

		Cozinha cozinha = cadastroCozinhaService.buscarOuFalhar(cozinhaId);
		Cidade cidade = cadastroCidade.buscarOuFalhar(cidadeId);
		// No código acima, caso não retornar nenhuma cozinha ele ira retornar o erro de Entidade não encontrada.
		restaurante.setCozinha(cozinha);
		restaurante.getEndereco().setCidade(cidade);
		return restauranteRepository.save(restaurante);
	}
	
	@Transactional // esta anotação é utilizada para iniciar uma transação no banco, quando é realizada altum tipo de inclusão ou alteração de dados e para ter uma maior garantia, onde em casos que existam
	//uma cadeia de chamadas para o banco de dados e uma delas apresentar erro, isso garante que, quando o jpa estiver fazendo o rollback da chamada realizada as demais transações não sejam executadas para ter
	// uma maior garantia de que o dado esta da forma esperada.
	public void remover(Long id) {
		try {
			restauranteRepository.deleteById(id); // quando executamos a função do delete do jpa, ele enfilera os commits e em algum momento ele descarrega estas alterações no banco de dados
			// geralmente este commit pode demorar caso existam varias outras commits concorrendo ente si para ser executado no banco e para forçar a descarga de forma prioritaria do commit
			//é possivel utilizar o comando "flush" como demonstrado abaixo, que desta forma o JPA executara este comando com prioridade maxima.
			restauranteRepository.flush();
		} catch (EmptyResultDataAccessException e) {
			throw new RestauranteNaoEncontradoException(id);
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoExeption(
					String.format("Restaurante de código %d não pode ser removida, pois está em uso", id));
		}
	}
	
	@Transactional
	public void desassociarFormaPagamento(Long restauranteId, Long formaPgamentoId) {
		Restaurante restaurante = buscarOuFalhar(restauranteId);
		
		FormaPagamento formaPagamento = cadastroFormaPagamento.buscarOuFalhar(formaPgamentoId); // Esta etapa é utilizada para validar se a forma de pagamento informada para remoção existe, e
		 // caso não exista será retornado um erro 404 para a chamada do método
		
		restaurante.removerFormaPagamento(formaPagamento);
		// a função utilizada para salvar a alteração realizada na entidade buscada no banco não é necessária, pois o JPA  ao identificar qualquer alteração no objeto já realizada a alteração 
		// do mesmo na base, fazendo o update necessário, desta forma não havendo a necessidade de realizar um update no objeto.
	}
	
	@Transactional
	public void associarFormaPagamento(Long restauranteId, Long formaPgamentoId) {
		Restaurante restaurante = buscarOuFalhar(restauranteId);
		
		FormaPagamento formaPagamento = cadastroFormaPagamento.buscarOuFalhar(formaPgamentoId); // Esta etapa é utilizada para validar se a forma de pagamento informada para remoção existe, e
		 // caso não exista será retornado um erro 404 para a chamada do método
		
		restaurante.adcionarFormaPagamento(formaPagamento);
		// a função utilizada para salvar a alteração realizada na entidade buscada no banco não é necessária, pois o JPA  ao identificar qualquer alteração no objeto já realizada a alteração 
		// do mesmo na base, fazendo o update necessário, desta forma não havendo a necessidade de realizar um update no objeto.
	}
	
	@Transactional
	public void ativar(Long restauranteId) {
		Restaurante restauranteAtual = buscarOuFalhar(restauranteId);
		
		restauranteAtual.setAtivo(true);
		
	}
	
	@Transactional
	public void inativar(Long restauranteId) {
		Restaurante restauranteAtual = buscarOuFalhar(restauranteId);
		
		restauranteAtual.setAtivo(false);
		
	}
	
	@Transactional
	public void ativar(List<Long> restaurantesIds) {
		restaurantesIds.forEach(this::ativar);
	}
	
	@Transactional
	public void inativar(List<Long> restaurantesIds) {
		restaurantesIds.forEach(this::inativar);
	}
	
	public Restaurante buscarOuFalhar(Long restauranteId) {
	    return restauranteRepository.findById(restauranteId)
	        .orElseThrow(() -> new RestauranteNaoEncontradoException(restauranteId));
	}
	
	@Transactional
	public void desassociarResponsavel(Long restauranteId, Long usuarioId) {
	    Restaurante restaurante = buscarOuFalhar(restauranteId);
	    Usuario usuario = cadastroUsuario.buscarOuFalhar(usuarioId);
	    
	    restaurante.removerResponsavel(usuario);
	}

	@Transactional
	public void associarResponsavel(Long restauranteId, Long usuarioId) {
	    Restaurante restaurante = buscarOuFalhar(restauranteId);
	    Usuario usuario = cadastroUsuario.buscarOuFalhar(usuarioId);
	    
	    restaurante.adicionarResponsavel(usuario);
	}
}
