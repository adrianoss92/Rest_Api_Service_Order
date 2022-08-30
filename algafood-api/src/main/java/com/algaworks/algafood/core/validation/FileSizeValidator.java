package com.algaworks.algafood.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

public class FileSizeValidator implements ConstraintValidator<FileSize, MultipartFile> {
	
	/*
	 * O DataSize é um método utilizado para realizar a conversão de bytes ex: 500KB em 0,5MB
	 * */
	private DataSize maxSize;
	
	@Override
	public void initialize(FileSize constraintAnnotation) {
		/*
		 * Nesta momento está sendo realizada a leitura do tamanho maximo suportado e inserindo o mesmo na variavel maxSize
		 * */
		this.maxSize = DataSize.parse(constraintAnnotation.max());
	}
	
	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
		
		/*
		 * Validando se o tamanho do arquivo multipart é menor ou igual ao tamanho maximo suportado
		 * */
		return value == null || value.getSize() <= this.maxSize.toBytes();
	}
	
}
