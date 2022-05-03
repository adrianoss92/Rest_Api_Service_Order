package com.algaworks.algafood.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.algaworks.algafood.core.validation.Groups;
import com.algaworks.algafood.core.validation.TaxaFrete;
import com.algaworks.algafood.core.validation.ValorZeroIncluiDescricao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

@ValorZeroIncluiDescricao(valorField = "taxaFrete", descricaoField = "nome", descricaoObrigatoria = "Frete Grátis")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Restaurante {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// @NotNull
	// @NotEmpty // serve para validar se o campo foi enviado vazio, porem se for enviado apenas com espaços a validação não consegue enchergar
	//	@NotBlank  já esta anotação, valida se o campo esta vazio e se possui apenas espaços, é melhor usar esta do que as outras duas anotações e é posivel também inserir uma mensagem em caso do campo vir vazio ou com espaços apenas no arquivo messages.properties em src/main/resources
	@Column(nullable = false)
	private String nome;

	// @DecimalMin("0") // Anotaçõa utilizada para poder validar se o valor minimo esperado esta sendo enviado
	//	@PositiveOrZero // esta anotação apenas valida se o valor informado é positivo ou zero, caso seja um valor negativo sera gerada uma exception
	//@TaxaFrete // anotação utilizada para demostrar a utilização de uma anotação criada dentro do proprio projeto
	@NotNull
	@Column(name = "taxa_frete", nullable = false)
	private BigDecimal taxaFrete;
	
	@JsonIgnoreProperties("nome") // Esta anotação serve para quando for exibir as informações o objeto cozinha ele ignorar a propriedade nome na exibição, caso possuisse mais algum campo que não deva ser exibido, nas apenas inserir todos os campo dentro de uma chave desta forma: {"nome", "id"}
	//	@JsonIgnoreProperties(value = "nome", allowGetters = true) //Caso seja necesário ignorar a propriedade nome, apenas nos input de dados ex: requisições PUT e POST, mas nas requisições de GET, seja exibido o campo nome normalmente, basta utilizar a propriedade  allowGetters = true como demonstrado
	//	@Valid // Esta anotação serve para poder validar se os campos destes objetos estão validos, e caso algum campo não tenha sido enviado, será gerada uma exception
	//	@ConvertGroup(from = Default.class, to = Groups.CozinhaId.class)
	//	@NotNull
	@ManyToOne // relacionamento muitos para um
	@JoinColumn(name = "cozinha_id", nullable = false)
	private Cozinha cozinha;

	//	@JsonIgnore // Quando precisamos que uma determinada informação não seja enviada com todo o model, basta inserirmos esta anotação
	@Embedded // Esta anotação é utilizada para informar que este cara é incorporado ao modelo de restaurante, pois ele possui suas prorias colunas no banco e dentro dela possuindo outros relacionamentos
	private Endereco endereco;
	
	private Boolean ativo = Boolean.TRUE;

	//	@JsonIgnore
	@CreationTimestamp // Esta anotação serve para especificar que no seu primeiro salvamento é preciso informar a data e hora do seu salvamento
	@Column(nullable = false, columnDefinition = "datetime")
	private OffsetDateTime dataCadastro;

	//@JsonIgnore
	@UpdateTimestamp // Esta anotação serve para informar que toda vez que a entidade for atualizada é preciso informar a data e hora da atualização
	@Column(nullable = false, columnDefinition = "datetime")
	private OffsetDateTime dataAtualizacao;

	@JsonIgnore
	@OneToMany(mappedBy = "restaurante")
	private List<Produto> produtos = new ArrayList<>();

	@ManyToMany // relacionamento muito para muitos em uma tabela que cuidado relacionamento entre as tabelas restaurante e forma de pagamento com isso sendo necessário informar o id de cada uma das colunas
	@JoinTable(name = "restaurante_forma_pagamento", joinColumns = @JoinColumn(name = "restaurante_id"), inverseJoinColumns = @JoinColumn(name = "forma_pagamento_id"))
	private Set<FormaPagamento> formasPagamento = new HashSet<>();
	
	@ManyToMany
	@JoinTable(name = "restaurante_usuario_responsavel",
	        joinColumns = @JoinColumn(name = "restaurante_id"),
	        inverseJoinColumns = @JoinColumn(name = "usuario_id"))
	private Set<Usuario> responsaveis = new HashSet<>();  

	public void ativar() {
		setAtivo(true);
	}
	
	public void inativar() {
		setAtivo(false);
	}

	public boolean removerFormaPagamento(FormaPagamento formaPagamento) {
		return getFormasPagamento().remove(formaPagamento);
	}
	
	public boolean adcionarFormaPagamento(FormaPagamento formaPagamento) {
		return getFormasPagamento().add(formaPagamento);
	}
	
	public boolean removerResponsavel(Usuario usuario) {
	    return getResponsaveis().remove(usuario);
	}
	
	public boolean adicionarResponsavel(Usuario usuario) {
	    return getResponsaveis().add(usuario);
	}
	
	public boolean aceitaFormaPagamento(FormaPagamento formaPagamento) {
	    return getFormasPagamento().contains(formaPagamento);
	}

	public boolean naoAceitaFormaPagamento(FormaPagamento formaPagamento) {
	    return !aceitaFormaPagamento(formaPagamento);
	}
}
