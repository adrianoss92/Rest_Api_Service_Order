package com.algaworks.algafood.api.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.algafood.api.assembler.FotoProdutoModelAssembler;
import com.algaworks.algafood.api.model.input.FotoProdutoInput;
import com.algaworks.algafood.domain.exeption.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.FotoProduto;
import com.algaworks.algafood.domain.model.FotoProdutoModel;
import com.algaworks.algafood.domain.model.Produto;
import com.algaworks.algafood.domain.service.impl.CadastroProdutoServiceImpl;
import com.algaworks.algafood.domain.service.impl.CatalogoFotoProdutoService;
import com.algaworks.algafood.domain.service.impl.FotoStorageService;
import com.algaworks.algafood.domain.service.impl.FotoStorageService.FotoRecuperada;

@RestController
@RequestMapping("/restaurantes/{restauranteId}/produtos/{produtoId}/foto")
public class RestauranteProdutoFotoController {
	
	@Autowired
	private CatalogoFotoProdutoService catalogoFotoproduto;
	
	@Autowired
	private CadastroProdutoServiceImpl cadastroProdutoservice;
	
	@Autowired
	private FotoStorageService fotoStorageService;
	
	@Autowired
	private FotoProdutoModelAssembler fotoprodutoModelAssembler;
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public FotoProdutoModel buscar(@PathVariable Long restauranteId, 
	        @PathVariable Long produtoId) {
	    FotoProduto fotoProduto = catalogoFotoproduto.buscarOuFalhar(restauranteId, produtoId);
	    
	    return fotoprodutoModelAssembler.toModel(fotoProduto);
	}
	
	@GetMapping
	public ResponseEntity<InputStreamResource> servirFoto(@PathVariable Long restauranteId, 
	        @PathVariable Long produtoId, @RequestHeader(name = "accept") String acceptHeader) throws HttpMediaTypeNotAcceptableException {
	   try {
		   FotoProduto fotoProduto = catalogoFotoproduto.buscarOuFalhar(restauranteId, produtoId);
		   
		   MediaType mediaTypeFoto = MediaType.parseMediaType(fotoProduto.getContentType());
		   
		   List<MediaType> mediaTypesAceitas = MediaType.parseMediaTypes(acceptHeader);
		   
		   verificarCompatibilidadeMediaType(mediaTypeFoto, mediaTypesAceitas);
		   
		   FotoRecuperada fotoRecuperada = fotoStorageService.recuperar(fotoProduto.getNomeArquivo());
		    

			if (fotoRecuperada.temUrl()) {
				return ResponseEntity
						.status(HttpStatus.FOUND)
						.header(HttpHeaders.LOCATION, fotoRecuperada.getUrl())
						.build();
			} else {
				return ResponseEntity.ok()
						.contentType(mediaTypeFoto)
						.body(new InputStreamResource(fotoRecuperada.getInputStream()));
			}
//		   return ResponseEntity.ok()
//		    		.contentType(mediaTypeFoto)
//		    		.body(new InputStreamResource(fotoRecuperada.getInputStream()));
	} catch (EntidadeNaoEncontradaException e) {
		 return ResponseEntity.notFound().build();
	}
	}
	
	private void verificarCompatibilidadeMediaType(MediaType mediaTypeFoto, List<MediaType> mediaTypesAceitas) throws HttpMediaTypeNotAcceptableException {
		boolean compativel = mediaTypesAceitas.stream()
				.anyMatch(mediaTypeAceita -> mediaTypeAceita.isCompatibleWith(mediaTypeFoto));
		
		if(!compativel) {
			throw new HttpMediaTypeNotAcceptableException(mediaTypesAceitas);
		}
				
	}

	/*
	 * Endpois utilizado para realizar o upload de arquivos, utilizando o tipo form-data
	 * */
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public FotoProdutoModel atualizarFotro(@PathVariable Long restauranteId, @PathVariable Long produtoId, @Valid FotoProdutoInput fotoProdutoInput) throws IOException {
		
		Produto produto = this.cadastroProdutoservice.buscarOuFalhar(restauranteId, produtoId);
		
		MultipartFile arquivo = fotoProdutoInput.getArquivo();
		
		FotoProduto foto = new FotoProduto();
		foto.setProduto(produto);
		foto.setDescricao(fotoProdutoInput.getDescricao());
		foto.setContentType(arquivo.getContentType());
		foto.setTamanho(arquivo.getSize());
		foto.setNomeArquivo(arquivo.getOriginalFilename());
		
		FotoProduto fotoProdutoSalva = catalogoFotoproduto.salvar(foto, arquivo.getInputStream());
		
		return fotoprodutoModelAssembler.toModel(fotoProdutoSalva);
		
//		var nomeArquivo = UUID.randomUUID().toString() + "_" + fotoProdutoInput.getArquivo().getOriginalFilename();
//		
//		var arquivoFoto = Path.of("C:/Users/ADRIANO/Desktop/catalogo", nomeArquivo);
//		
//		System.out.println(fotoProdutoInput.getDescricao());
//		System.out.println(arquivoFoto);
//		System.out.println(fotoProdutoInput.getArquivo().getContentType());
//		
//		try {
//			fotoProdutoInput.getArquivo().transferTo(arquivoFoto);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
	}
	
	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Long restauranteId, 
	        @PathVariable Long produtoId) {
	    catalogoFotoproduto.excluir(restauranteId, produtoId);
	}  
	
}
