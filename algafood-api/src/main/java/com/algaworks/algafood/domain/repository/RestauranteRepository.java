package com.algaworks.algafood.domain.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.algaworks.algafood.domain.model.Restaurante;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

	List<Restaurante> queryByTaxaFreteBetween(BigDecimal taxaInicial, BigDecimal taxaFinal); // Retorna o restaurante que possui a taxa frete com o valor entre os valores iniciais e finais passados.
	
	List<Restaurante> findBynomeContainingAndCozinhaId(String nome, Long cozinha); // Retorna o restaurante que contem o nome e o Id da cozinha informado.
	
	Optional<Restaurante> findFirstRestauranteByNomeContaining(String nome); //Retorna apenas o primeiro da lista
	
	List<Restaurante> findTop2ByNomeContaining(String nome); //Retorna os primeiros 2 resultados 
	
	boolean existsByNome(String nome);  //Retorna um boolean informando se o Restaurante existe ou não.
	
	
	
//	List<Restaurante> listar();
//	Restaurante buscar(Long id);
//	Restaurante salvar(Restaurante restaurante);
//	void remover(Long id);
	
}
