package com.algaworks.algafood.domain.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FotoProdutoModel {
	
	private String nomeArquivo;
	
	private String descricao;
	
	private String contentType;
	
	private Long tamanho;
	
}
