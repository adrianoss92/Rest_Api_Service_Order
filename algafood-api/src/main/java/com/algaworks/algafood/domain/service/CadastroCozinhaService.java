package com.algaworks.algafood.domain.service;


import java.util.List;

import com.algaworks.algafood.domain.model.Cozinha;

public interface CadastroCozinhaService {
	
	public List<Cozinha> listar();
	public Cozinha buscar(Long id);
	public Cozinha salvar(Cozinha cozinha);
	public void remover(Long id);
	

}
