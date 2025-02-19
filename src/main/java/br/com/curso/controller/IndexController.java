package br.com.curso.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.curso.model.Usuario;
import br.com.curso.repository.UsuarioRepository;

//@CrossOrigin(origins = "*")
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
	
	@PostMapping(value="/" , produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario)
	{
		/*Quando tiver filhos relacionados (detalhes)*/
		for (int pos = 0; pos < usuario.getTelefones().size(); pos ++)
		{
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhacriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@PutMapping(value="/" , produces = "application/json")
	public ResponseEntity<?> atualizar(@RequestBody Usuario usuario)
	{
		
		/*Quando tiver filhos relacionados (detalhes)*/
		 usuario.getTelefones().forEach(telefone -> telefone.setUsuario(usuario));
	
		Usuario userTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());
		
		if (userTemporario == null)
		{
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Usuário não encontrado.\"}");
	    }
		
	    if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty())
	    {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Senha inválida. Não pode ser vazia.\"}");
	    }
		
		if (!userTemporario.getSenha().equals(usuario.getSenha()))
		{
			String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhacriptografada);
		}
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text" )
	public String delete (@PathVariable("id") Long id)
	{
		usuarioRepository.deleteById(id);
		
		return "ok";
	}
	
	
	/*
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity init(@RequestParam (value = "nome", required = false, defaultValue = "Nome não informado") String nome, @RequestParam(value="salario", required = false) Long salario  )
	{
		
		return new ResponseEntity<>("Usuario: " + nome + ", Salario: "+ salario, HttpStatus.OK);
	}
	*/

}