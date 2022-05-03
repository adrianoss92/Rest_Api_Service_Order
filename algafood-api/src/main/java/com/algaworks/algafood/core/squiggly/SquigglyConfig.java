package com.algaworks.algafood.core.squiggly;

import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.web.RequestSquigglyContextProvider;
import com.github.bohnman.squiggly.web.SquigglyRequestFilter;

@Configuration
public class SquigglyConfig {
 // O Squiggly é uma bliblioteca utilizada na serialização dos dados de retorno de qualquer request, onde com ela é possivel passarmos parametros, para filtrar os dados que desejamos visualizar
 // sem a necessidade de alteração ou inserir alguma anotação dentro da model ou dto, apenas inserindo um parametro na request chamado campos ou fildes, e passando os campos do objeto que desejamos visualizar	
	@Bean
	public FilterRegistrationBean<SquigglyRequestFilter> squigglyRequestFilter(ObjectMapper objectMapper) {
		Squiggly.init(objectMapper, new RequestSquigglyContextProvider("campos", null));
		
		var urlPatterns = Arrays.asList("/pedidos/*", "/restaurantes/*");
		
		var filterRegistration = new FilterRegistrationBean<SquigglyRequestFilter>();
		filterRegistration.setFilter(new SquigglyRequestFilter());
		filterRegistration.setOrder(1);
		filterRegistration.setUrlPatterns(urlPatterns);
		
		return filterRegistration;
	}
	
}