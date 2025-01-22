package br.com.curso.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.curso.model.Usuario;
import br.com.curso.repository.UsuarioRepository;

@RestController /*ARQUITETURA REST*/
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired /*se fosse CDI seria @inject*/
	private UsuarioRepository usuarioRepository;
	
	/*SERVIÇO RESTFULL*/
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> init(@PathVariable (value="id") Long id )
	{
		
		Optional<Usuario> usuario =  usuarioRepository.findById(id);
		
		return new ResponseEntity(usuario.get(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<Usuario>> usuario ()
	{
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	/*
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity init(@RequestParam (value = "nome", required = false, defaultValue = "Nome não informado") String nome, @RequestParam(value="salario", required = false) Long salario  )
	{
		
		return new ResponseEntity<>("Usuario: " + nome + ", Salario: "+ salario, HttpStatus.OK);
	}
	*/

}