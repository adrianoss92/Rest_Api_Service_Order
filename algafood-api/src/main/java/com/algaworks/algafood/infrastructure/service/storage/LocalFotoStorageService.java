package com.algaworks.algafood.infrastructure.service.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.algaworks.algafood.core.storage.StorageProperties;
import com.algaworks.algafood.domain.service.impl.FotoStorageService;

public class LocalFotoStorageService implements FotoStorageService {
	
	@Value("${algafood.storage.local.diretorio-fotos}")
	private Path diretorioFotos;
	

	@Autowired
	private StorageProperties storageProperties;
	
	@Override
	public void armazenar(NovaFoto novaFoto) {
		
		try {
			Path arquivoPath = getArquivoPath(novaFoto.getNomeAquivo());
			
			FileCopyUtils.copy(novaFoto.getInputStream(), Files.newOutputStream(arquivoPath));
			
		} catch (Exception e) {
			throw new StorageException("Não foi possivel armazenar o arquivo.", e);
		}
		
	}

	/*
	 * Método utilizado para pegar o Path completo do arquivo ex: D:/Algafood/nomeDoArquivo.jpg
	 * */
	private Path getArquivoPath(String nomeAquivo) {
		return diretorioFotos.resolve(Path.of(nomeAquivo));
	}

	@Override
	public void remover(String nomeArqivo) {
		Path arquivoPath = getArquivoPath(nomeArqivo);
		
		try {
			Files.deleteIfExists(arquivoPath);
		} catch (IOException e) {
			throw new StorageException("Não foi possivel excluir o arquivo.", e);
		}
		
	}
	
	@Override
	public FotoRecuperada recuperar(String nomeArquivo) {
	    try {
	        Path arquivoPath = getArquivoPath(nomeArquivo);
	        
	        FotoRecuperada fotoRecuperada = FotoRecuperada.builder().inputStream(Files.newInputStream(arquivoPath)).build();
	        
	        return fotoRecuperada;
	    } catch (Exception e) {
	        throw new StorageException("Não foi possível recuperar arquivo.", e);
	    }
	}  
	
}
