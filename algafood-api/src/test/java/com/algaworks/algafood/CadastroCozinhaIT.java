package com.algaworks.algafood;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import javax.validation.ConstraintViolationException;

import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.algaworks.algafood.domain.exeption.CozinhaNaoEncontradaException;
import com.algaworks.algafood.domain.exeption.EntidadeEmUsoExeption;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.domain.service.impl.CadastroCozinhaServiceImpl;
import com.algaworks.algafood.util.DatabaseCleaner;
import com.algaworks.algafood.util.ResourceUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //Normalmente esta anoração não necessita que seja passado nenhuma valor
//... porem para testarmos o método"deveRetornarStatus200_QuandoConsultaCozinha" é necessário que durante a sua execução a API esteja de pé para ser executado o teste
//... por isso é necessário passar estes valores para poder o SpringBootTest subir um container com a API em uma porta randomica que vamos identificar ele em seguida
@TestPropertySource("/application-test.properties") //Anotação utilizada para poder utilizar outro arquivo de propriedades 
public class CadastroCozinhaIT {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private DatabaseCleaner databaseCleaner;
	
	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	private static final int COZINHA_ID_INEXISTENTE = 100;

	private Cozinha cozinhaAmericana;
	private int quantidadeCozinhasCadastradas;
	private String jsonCorretoCozinhaChinesa;
	
	@Before  // Anotação utilizada para executar algum código antes da execução dos testes
	public void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(); // É necessário habilitar esta função para quando uma chamada apresentar erro
		  //... ser exibido o log da chamado e do response do endpoint
		RestAssured.port = port;
		RestAssured.basePath = "/cozinhas";
		
		this.jsonCorretoCozinhaChinesa = ResourceUtils.getContentFromResource(
			"/json/correto/cozinha-chinesa.json");
		
		databaseCleaner.clearTables(); // metodo responsavel por fazer a limpeza dos dados das tabelas do banco de dados
		prepararDados(); // responsavel por inserir os dados no banco de dados
	}
	
	@Autowired
	private CadastroCozinhaServiceImpl cadastroCozinhaServiceImpl;
	
	@Test
	public void testarCadastroCozinhaComSucesso() {
		// cenario
		Cozinha novaCozinha = new Cozinha();
		novaCozinha.setNome("Chinesa");
		
		// ação
		novaCozinha = cadastroCozinhaServiceImpl.salvar(novaCozinha);
		
		// validação
		assertThat(novaCozinha).isNotNull();
		assertThat(novaCozinha.getId()).isNotNull();
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void testarCadastroCozinhaSemNomeDeveFalhar() {
		// cenario
		Cozinha novaCozinha = new Cozinha();
		novaCozinha.setNome(null);
		
		// ação
		novaCozinha = cadastroCozinhaServiceImpl.salvar(novaCozinha);
		

	}
	

	
//	@Test(expected = EntidadeEmUsoExeption.class)
//	public void deveFalharAoTentarExcluirCozinhaEmUso() {
//		cadastroCozinhaServiceImpl.remover(4L);
//	}
	
	@Test(expected =  CozinhaNaoEncontradaException.class)
	public void deveFalharAoTentarExcluirCozinhaInexistente(){	
		cadastroCozinhaServiceImpl.remover(100L);
	}
	
	@Test 
	public void deveRetornarStatus200_QuandoConsultaCozinha() {
		
		
		
		RestAssured.given() //Dado que seja realizada uma chamada com estes parametros
			.accept(ContentType.JSON)
		.when()  // quando for realizada uma chamada do tipo GET
			.get()
		.then() // então é esperado um status code
			.statusCode(HttpStatus.OK.value());
	}
	
	@Test
	public void deveRetornarRespostaEStatusCorretos_QuandoConsultarCozinhaExistente() {
		RestAssured.given()
			.pathParam("cozinhaId", 2)
			.accept(ContentType.JSON)
		.when()
			.get("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("nome", equalTo("Indiana"));
	}
	
	@Test
	public void deveRetornarRespostaEStatus404_QuandoConsultarCozinhaNaoExistente() {
		RestAssured.given()
			.pathParam("cozinhaId", 999)
			.accept(ContentType.JSON)
		.when()
			.get("/{cozinhaId}")
		.then()
			.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	public void deveConter4Cozinhas_quandoConsultarCozinhas() {
		
		RestAssured.given() //Dado que seja realizada uma chamada com estes parametros
			.accept(ContentType.JSON)
		.when()  // quando for realizada uma chamada do tipo GET
			.get()
		.then() // então é esperado 
			.body("", Matchers.hasSize(2)) //Esta biblioteca Matchers, faz a validação dentro do body da requisição, onde é possivel utilizar algumas 
			.body("nome", Matchers.hasItems("Indiana", "Tailandesa")); //... funções que existem na biblioteca, como contar a quantidade de itens de uma lista
												//...ou até mesmo validar se dentro de atributo de um objeto possui um valor especifico
	}
	
	@Test
	public void DeveRetornaStatus201_QuandoCadastrarCozinha() {
		RestAssured.given()
			.body("{ \"nome\": \"Chinesa\" }")
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.CREATED.value());
	}
	
	private void prepararDados() {
		Cozinha cozinha1 = new Cozinha();
		cozinha1.setNome("Tailandesa");
				
		cozinhaRepository.save(cozinha1);
		
		Cozinha cozinha2 = new Cozinha();
		cozinha2.setNome("Indiana");
		
		cozinhaRepository.save(cozinha2);
		
		this.quantidadeCozinhasCadastradas = (int) cozinhaRepository.count();
	}
}
