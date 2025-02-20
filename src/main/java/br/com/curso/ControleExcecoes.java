package br.com.curso;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
@ControllerAdvice
public class ControleExcecoes  extends ResponseEntityExceptionHandler
{
	
	@ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatusCode statusCode, WebRequest request) {
	
		String msg = "";
		
		if(ex instanceof MethodArgumentNotValidException)
		{
			List<ObjectError> list = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();
			
			for(ObjectError objectError : list)
			{
				msg += objectError.getDefaultMessage();
			}
			
		}
		else
		{
			msg = ex.getMessage();
		}
		
		ObjetoErro objetoErro = new ObjetoErro();
		objetoErro.setError(msg);
		
		
		return new ResponseEntity<>(objetoErro, headers, statusCode);
	}
}
