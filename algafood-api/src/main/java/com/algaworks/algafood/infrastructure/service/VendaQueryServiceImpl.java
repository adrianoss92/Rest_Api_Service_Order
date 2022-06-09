package com.algaworks.algafood.infrastructure.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;

import org.springframework.stereotype.Repository;

import com.algaworks.algafood.domain.enums.StatusPedido;
import com.algaworks.algafood.domain.filter.VendaDiariaFilter;
import com.algaworks.algafood.domain.model.Pedido;
import com.algaworks.algafood.domain.model.dto.VendaDiaria;
import com.algaworks.algafood.domain.service.impl.VendaQueryService;

@Repository
public class VendaQueryServiceImpl implements VendaQueryService {
	
	@PersistenceContext
	private EntityManager manager;

	/*
	 * Método utilizado para realizar fazer as consultas personalizadas utilizando o EntityManager onde fica uma forma mais organizada de realizar este select
	 * select date(p.data_criacao) as data_criacao,
	 *	count(p.id) as total_vendas,
	 *	sum(p.valor_total) as total_faturado
	 *	from pedido p group by date(p.data_criacao)
	 * 
	 * */
	@Override
	public List<VendaDiaria> consultarVendasDiarias(VendaDiariaFilter filtro, String timeOffset) {
		
		var builder = manager.getCriteriaBuilder();
		var query = builder.createQuery(VendaDiaria.class);
		var root = query.from(Pedido.class);
		var predicates = new ArrayList<Predicate>();
		
		var functionConvertTzDataCriacao = builder.function(
				"convert_tz", Date.class, root.get("dataCriacao"),builder.literal("+00:00"),
				builder.literal(timeOffset)); //Função para realizar o tratamento das datas, convertendo a data para o UTC informado pelo usuário
		
		var functionDateDataCriacao = builder.function("date", Date.class, functionConvertTzDataCriacao);
		
		var selection = builder.construct(VendaDiaria.class, 
				functionDateDataCriacao, // Função para converter a data que esta neste formato: "2022-06-03 10:06:10" para este formato "2022-06-03"
				builder.count(root.get("id")), //Conta os IDs de todas as vendas
				builder.sum(root.get("valorTotal")));  //Soma o valor total de todas as vendas
		
		if (filtro.getRestauranteId() != null) {
		    predicates.add(builder.equal(root.get("restaurante"), filtro.getRestauranteId()));
		}
		    
		if (filtro.getDataCriacaoInicio() != null) {
		    predicates.add(builder.greaterThanOrEqualTo(root.get("dataCriacao"), 
		            filtro.getDataCriacaoInicio()));
		}

		if (filtro.getDataCriacaoFim() != null) {
		    predicates.add(builder.lessThanOrEqualTo(root.get("dataCriacao"), 
		            filtro.getDataCriacaoFim()));
		}
		    
		predicates.add(root.get("status").in(
		        StatusPedido.CONFIRMADO, StatusPedido.ENTREGUE));
		
		query.select(selection);
		query.where(predicates.toArray(new Predicate[0]));
		query.groupBy(functionDateDataCriacao);
		
		
		return manager.createQuery(query).getResultList();
	}

}
