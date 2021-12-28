package com.algaworks.algafood.domain.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.algaworks.algafood.domain.model.Restaurante;

@Repository
public interface RestauranteRepositoryQuery {
	
	List<Restaurante> find(String nome, BigDecimal taxaFreteInicial, BigDecimal taxaFreteFinal);  // Assinatura do repository criado, onde foi implementada uma consulta em JPQL
	// mesmo que a classe onde o metodo se encontra não implemente esta interface o Spring consegue identificar a classe que possui esta assinatura e acionar a mesma quando o metodo é inicializado na controller.

	List<Restaurante> findComFreteGratis(String nome);
}
