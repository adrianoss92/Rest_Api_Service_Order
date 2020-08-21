package com.algaworks.algafood.domain.service;

import java.util.List;

import com.algaworks.algafood.domain.model.Restaurante;

public interface CadastroRestauranteService {
	
	public List<Restaurante> listar();
	public Restaurante buscar(Long id);
}
