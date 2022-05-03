package com.algaworks.algafood.api.model;

import java.math.BigDecimal;

import com.algaworks.algafood.api.model.view.RestauranteView;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RestauranteModel {
	

	@JsonView({RestauranteView.Resumo.class, RestauranteView.ApenasNome.class}) //Ao inserir a anotação @JsonView a mesma aceita que seja informada uma unica interface ou uma lista de interface, para identificar
	private Long id;    // um atributo do objeto.
	
	@JsonView({RestauranteView.Resumo.class, RestauranteView.ApenasNome.class})
	private String nome;

	@JsonView(RestauranteView.Resumo.class)
	private BigDecimal taxaFrete;
	
	@JsonView(RestauranteView.Resumo.class)
	private CozinhaModel cozinha;
	
	private EnderecoModel endereco;
	
	private Boolean aberto;
	
	private Boolean ativo;

}
