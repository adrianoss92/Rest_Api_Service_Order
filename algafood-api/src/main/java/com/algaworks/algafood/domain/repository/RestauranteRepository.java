package com.algaworks.algafood.domain.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.algaworks.algafood.domain.model.Restaurante;



@Repository
public interface RestauranteRepository 
	extends CustomJpaRepositoy<Restaurante, Long>, RestauranteRepositoryQuery,
	JpaSpecificationExecutor<Restaurante>{

	List<Restaurante> queryByTaxaFreteBetween(BigDecimal taxaInicial, BigDecimal taxaFinal); // Retorna o restaurante que possui a taxa frete com o valor entre os valores iniciais e finais passados.
	
	@Query("from Restaurante where nome like %:nome% and cozinha.id = :id" )					// Quando quisermos fazer uma consulta que não exista no JPARepository podemos utilizar a notação @Query e inserir como valor a query SQL ou NoSQL correspondente a consulta 
	List<Restaurante> consultarPorNome(String nome,@Param("id") Long cozinha);  // ou alteração que queremos fazer no banco de dados.
	
	List<Restaurante> findBynomeContainingAndCozinhaId(String nome, Long cozinha); // Retorna o restaurante que contem o nome e o Id da cozinha informado.
	
	Optional<Restaurante> findFirstRestauranteByNomeContaining(String nome); //Retorna apenas o primeiro da lista
	
	List<Restaurante> findTop2ByNomeContaining(String nome); //Retorna os primeiros 2 resultados 
	
	boolean existsByNome(String nome);  //Retorna um boolean informando se o Restaurante existe ou não.
	
}
