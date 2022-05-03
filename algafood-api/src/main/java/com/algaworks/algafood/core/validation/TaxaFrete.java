package com.algaworks.algafood.core.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.constraints.PositiveOrZero;

//Esta interface tem como o intuito demonstrara criação de uma anotação onde nesta mesma interface fazemos a utilização de uma outra @anotação 
//que é responsavel pela validação propriamente dita que é o PositiveOrZero, mas estas são as informações necessárias para criação de uma anotação

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = { })
@PositiveOrZero
public @interface TaxaFrete {
	
	@OverridesAttribute(constraint = PositiveOrZero.class, name="message") //esta anotação é utilizada para poder subistituir o valor de uma propriedade que neste caso é o message da class PositiveOrZero inserindo o valor abaixo
	String message() default "{TaxaFrete.invalida}";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}
