package com.algaworks.algafood.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;

import org.hibernate.annotations.CreationTimestamp;

import com.algaworks.algafood.domain.enums.StatusPedido;
import com.algaworks.algafood.domain.exeption.NegocioException;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Pedido {
	
	@EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String codigo;
    
    private BigDecimal subtotal;
    private BigDecimal taxaFrete;
    private BigDecimal valorTotal;

    @Embedded
    private Endereco enderecoEntrega;
    
    @Enumerated(EnumType.STRING)
    private StatusPedido status = StatusPedido.CRIADO;
    
    @CreationTimestamp
    private OffsetDateTime dataCriacao;

    private OffsetDateTime dataConfirmacao;
    private OffsetDateTime dataCancelamento;
    private OffsetDateTime dataEntrega;
    
    @ManyToOne(fetch = FetchType.LAZY) // esta anotação serve para indicar ao JPA que ao fazer uma consultar caso não seja explicita a necessidade de consultar este objeto a mesma não sera executad
    @JoinColumn(nullable = false)      // mas caso ao tentar fazer a leitura de um objeto e for realizada a leitura do campo ai a consulta será realizada
    private FormaPagamento formaPagamento;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private Restaurante restaurante;
    
    @ManyToOne
    @JoinColumn(name = "usuario_cliente_id", nullable = false)
    private Usuario cliente;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL) //Este parametro cacade = CascadeType.All informa que quando estiver salvando um pedido os itens do pedido também deverão ser salvos
    private List<ItemPedido> itens = new ArrayList<>();
    
    public void calcularValorTotal() {
        getItens().forEach(ItemPedido::calcularPrecoTotal);
        
        this.subtotal = getItens().stream()
            .map(item -> item.getPrecoTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.valorTotal = this.subtotal.add(this.taxaFrete);
    }

    public void definirFrete() {
        setTaxaFrete(getRestaurante().getTaxaFrete());
    }

    public void atribuirPedidoAosItens() {
        getItens().forEach(item -> item.setPedido(this));
    }
	
    public void confirmar() {
    	setStatus(StatusPedido.CONFIRMADO);
    	setDataConfirmacao(OffsetDateTime.now());
    }
    
    public void entregar() {
    	setStatus(StatusPedido.ENTREGUE);
    	setDataConfirmacao(OffsetDateTime.now());
    }
    
    public void cancelar() {
    	setStatus(StatusPedido.CANCELADO);
    	setDataConfirmacao(OffsetDateTime.now());
    }
    
    private void setStatus(StatusPedido novoStatus) {
    	if(getStatus().naoPodeAlterarPara(novoStatus)) {
    		throw new NegocioException(
	                String.format("Status do pedido %d não pode ser alterado de %s para %s",
	                        getCodigo(), getStatus().getDescricao(), 
	                        novoStatus.getDescricao()));
    	}
    	
    	this.status = novoStatus;
    }
    
    @PrePersist // esta anotação é utilizada para quando o jpa for realizar a persistencia dos dados no banco ele executa esta função, neste caso criando UUID do pedido
    private void gerarCodigo() {
    	setCodigo(UUID.randomUUID().toString());
    }
}
