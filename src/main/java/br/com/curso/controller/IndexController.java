package br.com.curso.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController /*ARQUITETURA REST*/
@RequestMapping(value = "/usuario")
public class IndexController {
	
	/*SERVIÇO RESTFULL*/
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity init(@RequestParam (value = "nome", required = false, defaultValue = "Nome não informado") String nome, @RequestParam(value="salario", required = false) Long salario  )
	{
		
		return new ResponseEntity<>("Usuario: " + nome + ", Salario: "+ salario, HttpStatus.OK);
	}

}