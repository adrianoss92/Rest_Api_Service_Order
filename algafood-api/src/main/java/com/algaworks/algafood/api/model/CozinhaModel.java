package com.algaworks.algafood.api.model;

import com.algaworks.algafood.api.model.view.RestauranteView;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CozinhaModel {
	
	@JsonView(RestauranteView.Resumo.class)
	private long id;
	
	@JsonView(RestauranteView.Resumo.class)
	private String nome;
}
