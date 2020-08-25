package com.algaworks.algafood.domain.service;

import java.util.List;

import com.algaworks.algafood.domain.model.Cidade;


public interface CadastroCidadeService {
	
	public List<Cidade> listar();
	public Cidade buscar(Long id);
	public Cidade salvar(Cidade cidade);
	public void remover(Long id);
	
}
