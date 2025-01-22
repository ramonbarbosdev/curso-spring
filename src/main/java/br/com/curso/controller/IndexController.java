package br.com.curso.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.curso.model.Usuario;

@RestController /*ARQUITETURA REST*/
@RequestMapping(value = "/usuario")
public class IndexController {
	
	/*SERVIÇO RESTFULL*/
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> init( )
	{
		Usuario usuario = new Usuario();
		usuario.setId(50L);
		usuario.setLogin("ramonbarbosdev");
		usuario.setSenha("123456");
		
		Usuario usuario2 = new Usuario();
		usuario2.setId(51L);
		usuario2.setLogin("devlook");
		usuario2.setSenha("654321");
		
		List<Usuario> usuarios = new ArrayList<Usuario>();
		usuarios.add(usuario);
		usuarios.add(usuario2);
		
		return new ResponseEntity(usuarios, HttpStatus.OK);
	}
	
	/*
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity init(@RequestParam (value = "nome", required = false, defaultValue = "Nome não informado") String nome, @RequestParam(value="salario", required = false) Long salario  )
	{
		
		return new ResponseEntity<>("Usuario: " + nome + ", Salario: "+ salario, HttpStatus.OK);
	}
	*/

}