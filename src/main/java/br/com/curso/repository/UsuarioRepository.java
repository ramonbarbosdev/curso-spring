package br.com.curso.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.curso.model.Usuario;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface  UsuarioRepository extends CrudRepository<Usuario, Long>  {

	//consultar usuario por login
	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);
	
}
