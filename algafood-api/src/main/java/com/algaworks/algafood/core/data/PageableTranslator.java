package com.algaworks.algafood.core.data;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/*
 * Método utilizado para receber uma entidade Pageable e FieldMapping e converter os fieldsMapping em um Pageable
 * 
 * Obs: Os fieldsMapping são os DE/PARA que é passado pelo método traduzirPageable que fica na controller de pedidos
 * */
public class PageableTranslator {

	public static Pageable Translate(Pageable pageable, Map<String, String> fieldsMapping) {
		var orders = pageable.getSort().stream()
				.filter(order -> fieldsMapping.containsKey(order.getProperty()))
				.map(order -> new Sort.Order(order.getDirection(),
						fieldsMapping.get(order.getProperty())))
				.collect(Collectors.toList());
		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
	}
	
}
