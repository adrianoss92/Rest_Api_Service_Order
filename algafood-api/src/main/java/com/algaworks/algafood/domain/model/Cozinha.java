package com.algaworks.algafood.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Cozinha {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonProperty("titulo") // A notação @JsonProperty é utilizada quando se que mudar o nome da representação do atributo
	// que seria neste caso na hora de exibir a informação inves de ir nome:exemplo ira titulo:exemplo isso é possivel por causa da notação @JsonProperty
	// Existe uma forma também para quando o atributor for ser apresentado ele nãoi ser exibido e para isso é necessário apenas utilizar a notação @JsonIgnore, 
	// assim o atributo que possuir esta notação não sera exibido, porem o mesmo não deixa de existir
	@Column(nullable = false)
	private String nome;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
}
