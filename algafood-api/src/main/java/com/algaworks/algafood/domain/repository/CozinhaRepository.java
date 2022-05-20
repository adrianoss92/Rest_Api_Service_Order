package com.algaworks.algafood.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.algaworks.algafood.domain.model.Cozinha;

@Repository
public interface CozinhaRepository extends JpaRepository<Cozinha, Long>{

	Optional<Cozinha> findByNome(String nome);
	
	List<Cozinha> findTodasByNomeContaining(String nome); //Apenas lista todas as cozinhas
	
	Page<Cozinha> findTodasByNomeContaining(String nome, Pageable pageable); // Busca todas as cozinha pelo nome mas com paginação
	
	boolean existsByNome(String nome);

	
}
