package com.algaworks.algafood.api.controller;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.api.assembler.RestauranteInputDisassembler;
import com.algaworks.algafood.api.assembler.RestauranteModelAssembler;
import com.algaworks.algafood.api.exceptionhandler.ValidacaoException;
import com.algaworks.algafood.api.model.RestauranteModel;
import com.algaworks.algafood.api.model.input.CozinhaIdInput;
import com.algaworks.algafood.api.model.input.CozinhaInput;
import com.algaworks.algafood.api.model.input.RestauranteInput;
import com.algaworks.algafood.api.model.view.RestauranteView;
import com.algaworks.algafood.domain.exeption.CidadeNaoEncontradaException;
import com.algaworks.algafood.domain.exeption.CozinhaNaoEncontradaException;
import com.algaworks.algafood.domain.exeption.EntidadeEmUsoExeption;
import com.algaworks.algafood.domain.exeption.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.exeption.NegocioException;
import com.algaworks.algafood.domain.exeption.RestauranteNaoEncontradoException;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.service.impl.CadastroRestauranteServiceImpl;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

	@Autowired
	private CadastroRestauranteServiceImpl cadastroRestauranteServiceImpl;
	
	@Autowired
	private SmartValidator smartValidator;
	
	@Autowired
	private RestauranteModelAssembler restauranteModelAssembler;
	
	@Autowired
	private RestauranteInputDisassembler restauranteInputDisassembler;
	
//	@GetMapping
//	public List<RestauranteModel> listar() {
//		return restauranteModelAssembler.toCollectionModel(cadastroRestauranteServiceImpl.listar());
//	}
	
	@GetMapping  // O tipo de retorno MappingJacksonValue é utilizado para filtrar os dados com base nas anotaçãos @JsonView desta forma pode
	public MappingJacksonValue listar(@RequestParam(required = false) String projecao) { // O parametro require = false serve para informar que o requestParam que pode ser passado na request não é obrigatorio
		List<Restaurante> restaurantes = cadastroRestauranteServiceImpl.listar();
		List<RestauranteModel> restaurantesModel = restauranteModelAssembler.toCollectionModel(restaurantes);
		
		MappingJacksonValue restaurantesWrapper = new MappingJacksonValue(restaurantesModel);
		
		restaurantesWrapper.setSerializationView(RestauranteView.Resumo.class);
		
		if ("apenas-nome".equals(projecao)) {
			restaurantesWrapper.setSerializationView(RestauranteView.ApenasNome.class); // neste caso será filtrado um RestauranteModel apenas com os campos anotado com RestauranteView.ApenasNome.class
		} else if ("completo".equals(projecao)) {
			restaurantesWrapper.setSerializationView(null);
		}
		
		return restaurantesWrapper;
	}
	
//	@JsonView(RestauranteView.Resumo.class)
//	@GetMapping(params = "projecao=resumo")
//	public List<RestauranteModel> listarResumido() {
//		return listar();
//	}
	
//	@JsonView(RestauranteView.ApenasNome.class)
//	@GetMapping(params = "projecao=apenas-nome")
//	public List<RestauranteModel> listarApenasNome() {
//		return listar();
//	}

	@GetMapping(value = "/{restauranteId}")
	public RestauranteModel  buscar(@PathVariable Long restauranteId) {
		 Restaurante restaurante = cadastroRestauranteServiceImpl.buscarOuFalhar(restauranteId);
		 
		 
		 return restauranteModelAssembler.toModel(restaurante);
	}

	@PostMapping
	public RestauranteModel salvar(@RequestBody @Valid RestauranteInput restaurante) {
		
		try {
			return restauranteModelAssembler.toModel(cadastroRestauranteServiceImpl.salvar(restauranteInputDisassembler.toDomainObject(restaurante)));
	    } catch (CozinhaNaoEncontradaException | CidadeNaoEncontradaException  e) {
	        throw new NegocioException(e.getMessage());
	    }
	}

	@PutMapping("/{restauranteId}")
	public RestauranteModel atualizar(@PathVariable Long restauranteId,
			@RequestBody @Valid RestauranteInput restauranteInput) {	    
		try {
			
			// Restaurante restaurante = restauranteInputDisassembler.toDomainObject(restauranteInput);
			Restaurante restauranteAtual = cadastroRestauranteServiceImpl.buscarOuFalhar(restauranteId);
		    
			restauranteInputDisassembler.copyToDomainObject(restauranteInput, restauranteAtual);
			
		    // BeanUtils.copyProperties(restaurante, restauranteAtual, 
		    //        "id", "formasPagamento", "endereco", "dataCadastro", "produtos");
			return restauranteModelAssembler.toModel(cadastroRestauranteServiceImpl.salvar(restauranteAtual));
	    } catch (CozinhaNaoEncontradaException | CidadeNaoEncontradaException e) {
	        throw new NegocioException(e.getMessage());
	    }
	}
	
	@PutMapping("/{restauranteId}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ativar(@PathVariable Long restauranteId) {
		cadastroRestauranteServiceImpl.ativar(restauranteId);
	}
	
	@PutMapping("/ativacoes")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ativarMultiplos(@RequestBody List<Long> restaurantesIds) {
		try {
			cadastroRestauranteServiceImpl.ativar(restaurantesIds);
		} catch (RestauranteNaoEncontradoException e) {
			throw new NegocioException(e.getMessage());
		}
		
	}
	
	@DeleteMapping("/ativacoes")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inativarMultiplos(@RequestBody List<Long> restaurantesIds) {
		try {
			cadastroRestauranteServiceImpl.inativar(restaurantesIds);
		} catch (RestauranteNaoEncontradoException e) {
			throw new NegocioException(e.getMessage());
		}
	}
	
	@DeleteMapping("/{restauranteId}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inativar(@PathVariable Long restauranteId) {
		cadastroRestauranteServiceImpl.inativar(restauranteId);
	}
	
	@DeleteMapping("/{restauranteId}")
	public ResponseEntity<Restaurante> remover(@PathVariable Long restauranteId){
		try {
			cadastroRestauranteServiceImpl.remover(restauranteId);
			return ResponseEntity.noContent().build();
		}catch (EntidadeNaoEncontradaException e){
			return ResponseEntity.notFound().build();
		}catch (EntidadeEmUsoExeption e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}
	
	@PatchMapping("/{restauranteId}") //Este endPoint é utilizado para atualizar apenas um item do restaurante, não sendo necessário passar todos os dados de restaurante
	public RestauranteModel atualizarParcial(@PathVariable Long restauranteId, 
			@RequestBody Map<String, Object> campos,  HttpServletRequest request){
		Restaurante restauranteAtual = cadastroRestauranteServiceImpl.buscarOuFalhar(restauranteId);
		merge(campos, restauranteAtual, request);
		validate(restauranteAtual, "restaurante");
		
		RestauranteInput restauranteInput = new RestauranteInput();
		restauranteInput.setNome(restauranteAtual.getNome());
		restauranteInput.setTaxaFrete(restauranteAtual.getTaxaFrete());
		CozinhaIdInput cozinhaIdInput = new CozinhaIdInput();
		cozinhaIdInput.setId(restauranteAtual.getCozinha().getId());
// 		restauranteInput.setCozinha(cozinhaIdInput);
		
		return atualizar(restauranteId, restauranteInput);
	}

	private void validate(Restaurante restaurante, String objectName) {
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(restaurante, objectName);
		smartValidator.validate(restaurante, bindingResult);
		if(bindingResult.hasErrors()) {
			throw new ValidacaoException(bindingResult);
		}
	}

	private void merge(Map<String, Object> dadosOrigem, Restaurante restauranteAtual, HttpServletRequest request) {
		ServletServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(request);
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
			
			Restaurante restauranteOrigem = objectMapper.convertValue(dadosOrigem, Restaurante.class);
			
			// Metodo utilizado para varrer cada atributo do objeto e atribuindo o valor que foi passado apra aquele campo
			// assim atualizando apenas os campos que foram enviados na chamada 
			
			dadosOrigem.forEach((nomePropriedade, valorPropriedade) -> {
				Field field = ReflectionUtils.findField(Restaurante.class, nomePropriedade);
				field.setAccessible(true);
				
				Object novoValor = ReflectionUtils.getField(field, restauranteOrigem);
				
				ReflectionUtils.setField(field, restauranteAtual, novoValor);
			});
		} catch (IllegalArgumentException e) {
			Throwable causaRaiz = ExceptionUtils.getRootCause(e);
			throw new HttpMessageNotReadableException(e.getMessage(), causaRaiz, serverHttpRequest);
		}
	}

}
