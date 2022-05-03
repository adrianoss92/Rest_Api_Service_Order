package com.algaworks.algafood.api.exceptionhandler;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.algaworks.algafood.domain.exeption.EntidadeEmUsoExeption;
import com.algaworks.algafood.domain.exeption.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.exeption.NegocioException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;

//A notação @ControllerAdvice é utilizado para monitorar todos as controllers e nos casos de disparo de exceções o tratamento dos responses
//serão feitos por aqui tendo um padrão para responder com os dados corretamente em casos de erro.
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
	// Como estamos instanciando a classe ResponseEntityExceptionHandler, nos podemos alterar qualquer um dos métodos de respostas
	// como no caso abaixo do handleHttpMessageNotReadable, ele é um retorno dado quando o tipo do arquivo seja json, xml ou qualquer outro formato
	// ao ser enviado esteja com erro na sua estrutura
	
	public static final String MSG_ERRO_GENERICA_USUARIO_FINAL
	= "Ocorreu um erro interno inesperado no sistema. Tente novamente e se "
			+ "o problema persistir, entre em contato com o administrador do sistema.";
	
	@Autowired
	private MessageSource messageSource;
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;		
		ProblemType problemType = ProblemType.ERRO_DE_SISTEMA;
		String detail = MSG_ERRO_GENERICA_USUARIO_FINAL;

		ex.printStackTrace();
		
		Problema problema = createProblemaBuilder(status, problemType, detail)
				.userMessage(detail)
				.build();

		return handleExceptionInternal(ex, problema, new HttpHeaders(), status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		Throwable causaRaiz = ExceptionUtils.getRootCause(ex);
		if(causaRaiz instanceof InvalidFormatException) {
			return handleInvalidFormat((InvalidFormatException)causaRaiz, headers, status, request);
		} else if (causaRaiz instanceof PropertyBindingException) {
	        return handlePropertyBinding((PropertyBindingException) causaRaiz, headers, status, request); 
	    }
		ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENSIVEL;
		String detail = "O corpo da requisição está inválido. Verifique erro de sintaxe.";
		
		Problema problema = createProblemaBuilder(status, problemType, detail).userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL).build();
		return handleExceptionInternal(ex, problema, headers, status, request);
	}
	
	// Este método pe utilizado para poder tratar as exeptions onde o json rece um valor invalido ex: o campo é long e possui uma string, desta formaé informando o campo
	// que esta com o tipo de dado errado o valor que esta sendo passando errado e o tipo de dado esperado para este campo.
	private ResponseEntity<Object> handleInvalidFormat(InvalidFormatException ex, HttpHeaders headers
			, HttpStatus status, WebRequest request) {
		
		String path = joinPath(ex.getPath());
		
		ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENSIVEL;
		String detail = String.format("A propriedade '%s' recebeu o valor '%s', que é de um tipo invalido."
				+ "Corrija e informe um valor compativel com o tipo %s.", path, ex.getValue(), ex.getTargetType().getSimpleName());
		
		Problema problema = createProblemaBuilder(status, problemType, detail).userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL).build();
		
		return handleExceptionInternal(ex, problema, headers, status, request);
	}

	// Os métodos @ExceptionHandler são utilizados para quando ocorrer algum erro do tipo monitorado
	// ser realizada uma tratativa no response, parametrizando o tipo de retorno desejado nestes casos de erros.
	// no exemplo abaixo é passado apenas a data e hora do erro, com uma mensagem padrão.
	
	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<?> handleEntidadeNaoEncontrado(EntidadeNaoEncontradaException ex, WebRequest request){
		
		HttpStatus status = HttpStatus.NOT_FOUND;
		String detail = ex.getMessage();
		ProblemType problemType = ProblemType.ENTIDADE_NAO_ENCONTRADA;
	
		
		Problema problema = createProblemaBuilder(status, problemType, detail).userMessage(detail).build();
		return handleExceptionInternal(ex, problema, new HttpHeaders(), status, request);
		
	//	Problema problem = new Problema(LocalDateTime.now(), e.getMessage());
		
	//	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
		
	}
	
	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<?> handleNegocio(NegocioException  ex, WebRequest request){
		
		HttpStatus status = HttpStatus.BAD_REQUEST;
		String detail = ex.getMessage();
		ProblemType problemType = ProblemType.ERRO_NEGOCIO;
	
		
		Problema problema = createProblemaBuilder(status, problemType, detail).build();
		
		return handleExceptionInternal(ex, problema, new HttpHeaders(), status, request);
		
	}

	
	@ExceptionHandler(EntidadeEmUsoExeption.class)
	public ResponseEntity<?> handleEntidadeEmUso(EntidadeEmUsoExeption  ex, WebRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		String detail = ex.getMessage();
		ProblemType problemType = ProblemType.ERRO_NEGOCIO;
	
		
		Problema problema = createProblemaBuilder(status, problemType, detail).userMessage(detail).build();
		
		return handleExceptionInternal(ex, problema, new HttpHeaders(), status, request);
	}
	
	@ExceptionHandler({ ValidacaoException.class })
	public ResponseEntity<Object> handleValidacaoException(ValidacaoException ex, WebRequest request) {
	    return handleValidationInternal(ex, ex.getBindingResult(), new HttpHeaders(), 
	            HttpStatus.BAD_REQUEST, request);
	}
	

	protected ResponseEntity<Object> handleException(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		if(body == null) {
			body = Problema.builder()
					.timestamp(OffsetDateTime.now())
					.title(status.getReasonPhrase())
					.status(status.value())
					.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
					.build();
		} else if(body instanceof String) {
			body = Problema.builder()
					.timestamp(OffsetDateTime.now())
					.title((String) body)
					.status(status.value())
					.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
					.build();
		}
		
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}
	
	// 1. MethodArgumentTypeMismatchException é um subtipo de TypeMismatchException

	// 2. ResponseEntityExceptionHandler já trata TypeMismatchException de forma mais abrangente

	// 3. Então, especializamos o método handleTypeMismatch e verificamos se a exception
	//	    é uma instância de MethodArgumentTypeMismatchException

	// 4. Se for, chamamos um método especialista em tratar esse tipo de exception

	// 5. Poderíamos fazer tudo dentro de handleTypeMismatch, mas preferi separar em outro método
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
	        HttpStatus status, WebRequest request) {
	    
	    if (ex instanceof MethodArgumentTypeMismatchException) {
	        return handleMethodArgumentTypeMismatch(
	                (MethodArgumentTypeMismatchException) ex, headers, status, request);
	    }

	    return super.handleTypeMismatch(ex, headers, status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
	        HttpHeaders headers, HttpStatus status, WebRequest request) {

		return handleValidationInternal(ex, ex.getBindingResult(), headers, status, request);
	} 
	
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, 
	        HttpHeaders headers, HttpStatus status, WebRequest request) {
	    
	    ProblemType problemType = ProblemType.RECURSO_NAO_ENCONTRADO;
	    String detail = String.format("O recurso %s, que você tentou acessar, é inexistente.", 
	            ex.getRequestURL());
	    
	    Problema problema = createProblemaBuilder(status, problemType, detail).build();
	    
	    return super.handleExceptionInternal(ex, problema, headers, status, request);
	}
	
	private ResponseEntity<Object> handleValidationInternal(Exception ex, BindingResult bindingResult, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		        
		    ProblemType problemType = ProblemType.DADOS_INVALIDOS;
		    String detail = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.";
		    
		    List<Problema.Field> problemObjects = bindingResult.getAllErrors().stream()
		            .map(objectError -> {
		                String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
		                
		                String name = objectError.getObjectName();
		                
		                if (objectError instanceof FieldError) {
		                    name = ((FieldError) objectError).getField();
		                }
		                
		                return Problema.Field.builder()
		                    .name(name)
		                    .userMessage(message)
		                    .build();
		            })
		            .collect(Collectors.toList());
		    
		    Problema problem = createProblemaBuilder(status, problemType, detail)
		        .userMessage(detail)
		        .fields(problemObjects)
		        .build();
		    
		    return handleExceptionInternal(ex, problem, headers, status, request);
		}

	private ResponseEntity<Object> handleMethodArgumentTypeMismatch(
	        MethodArgumentTypeMismatchException ex, HttpHeaders headers,
	        HttpStatus status, WebRequest request) {

	    ProblemType problemType = ProblemType.PARAMETRO_INVALIDO;

	    String detail = String.format("O parâmetro de URL '%s' recebeu o valor '%s', "
	            + "que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %s.",
	            ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

	    Problema problema = createProblemaBuilder(status, problemType, detail).build();

	    return handleExceptionInternal(ex, problema, headers, status, request);
	}
	
	private Problema.ProblemaBuilder createProblemaBuilder(HttpStatus status, ProblemType problemType, String detail) {
		return Problema.builder()
				.timestamp(OffsetDateTime.now())
				.status(status.value())
				.type(problemType.getUri())
				.title(problemType.getTitle())
				.detail(detail);
	}
	
	private ResponseEntity<Object> handlePropertyBinding(PropertyBindingException ex,
	        HttpHeaders headers, HttpStatus status, WebRequest request) {

	    // Criei o método joinPath para reaproveitar em todos os métodos que precisam
	    // concatenar os nomes das propriedades (separando por ".")
	    String path = joinPath(ex.getPath());
	    
	    ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENSIVEL;
	    String detail = String.format("A propriedade '%s' não existe. "
	            + "Corrija ou remova essa propriedade e tente novamente.", path);

	    Problema problema = createProblemaBuilder(status, problemType, detail).build();
	    
	    return handleExceptionInternal(ex, problema, headers, status, request);
	}
	
	private String joinPath(List<Reference> references) {
	    return references.stream()
	        .map(ref -> ref.getFieldName())
	        .collect(Collectors.joining("."));
	}
	
	
}
