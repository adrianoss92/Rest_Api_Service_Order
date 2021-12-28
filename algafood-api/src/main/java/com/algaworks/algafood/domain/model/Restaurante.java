package com.algaworks.algafood.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.groups.ConvertGroup;
import javax.validation.groups.Default;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.algaworks.algafood.Groups;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Restaurante {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// @NotNull
	// @NotEmpty // serve para validar se o campo foi enviado vazio, porem se for
	// enviado apenas com espaços a validação não consegue enchergar
	@NotBlank(groups = Groups.CadastroRestaurante.class) // já esta anotação, valida se o campo esta vazio e se possui
																												// apenas espaços, é melhor usar esta do que as outras duas
																												// anotações
	@Column(nullable = false)
	private String nome;

	// @DecimalMin("0") // Anotaçõa utilizada para poder validar se o valor minimo
	// esperado esta sendo enviado
	@PositiveOrZero // esta anotação apenas valida se o falor informado é positivo ou zero, caso
									// seja um valor negativo sera gerada uma exception
	@Column(name = "taxa_frete", nullable = false)
	private BigDecimal taxaFrete;

	@Valid // Esta anotação serve para poder validar se os campos destes objetos estão
					// validos, e caso algum campo não tenha sido enviado, será gerada uma exception
	@ConvertGroup(from = Default.class, to = Groups.CadastroRestaurante.class)
	@NotNull
	@ManyToOne // relacionamento muitos para um
	@JoinColumn(name = "cozinha_id", nullable = false)
	private Cozinha cozinha;

	@JsonIgnore // Quando precisamos que uma determinada informação não seja enviada com todo o
							// model, basta inserirmos esta anotação
	@Embedded // Esta anotação é utilizada para informar que este cara é incorporado ao modelo
						// de restaurante, pois ele possui suas prorias colunas no banco e dentro dela
						// possuindo outros relacionamentos
	private Endereco endereco;

	@JsonIgnore
	@CreationTimestamp // Esta anotação serve para especificar que no seu primeiro salvamento é preciso
											// informar a data e hora do seu salvamento
	@Column(nullable = false, columnDefinition = "datetime")
	private LocalDateTime dataCadastro;

	@JsonIgnore
	@UpdateTimestamp // Esta anotação serve para informar que toda vez que a entidade for atualizada
										// é preciso informar a data e hora da atualização
	@Column(nullable = false, columnDefinition = "datetime")
	private LocalDateTime dataAtualizacao;

	@JsonIgnore
	@OneToMany(mappedBy = "restaurante")
	private List<Produto> produtos = new ArrayList<>();

	@ManyToMany // relacionamento muito para muitos em uma tabela que cuidado relacionamento
							// entre as tabelas restaurante e forma de pagamento com isso sendo necessário
							// informar o id de cada uma das colunas
	@JoinTable(name = "restaurante_forma_pagamento", joinColumns = @JoinColumn(name = "restaurante_id"), inverseJoinColumns = @JoinColumn(name = "forma_pagamento_id"))
	private List<FormaPagamento> formasPagamento = new ArrayList<>();

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

	public BigDecimal getTaxaFrete() {
		return taxaFrete;
	}

	public void setTaxaFrete(BigDecimal taxaFrete) {
		this.taxaFrete = taxaFrete;
	}

	public Cozinha getCozinha() {
		return cozinha;
	}

	public void setCozinha(Cozinha cozinha) {
		this.cozinha = cozinha;
	}

}
