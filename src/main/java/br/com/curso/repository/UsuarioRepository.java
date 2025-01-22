package br.com.curso.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.curso.model.Usuario;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface  UsuarioRepository extends CrudRepository<Usuario, Long>  {

}
