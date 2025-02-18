package br.com.curso.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;



import org.hibernate.annotations.ForeignKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.UniqueConstraint;

@Entity
public class Usuario implements UserDetails {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String login;
	
	private String senha;
	
	private String nome;
	
	
	
	/*Relacionamento Um pra muitos - Inicio */
	@OneToMany(mappedBy = "usuario", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Telefone> telefones = new ArrayList<Telefone>();
	
	public List<Telefone> getTelefones()
	{
		return telefones;
	}
	
	public void setTelefones(List<Telefone> telefones)
	{
		this.telefones = telefones;
	}
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable( name = "usuarios_role",
				uniqueConstraints = @UniqueConstraint(
													columnNames = {"usuario_id", "role_id"},
													name = "unique_role_user"
													),
				joinColumns = @JoinColumn(
										name = "usuario_id", 
										referencedColumnName = "id", 
										table = "usuario",
										unique = false,										
										foreignKey = @jakarta.persistence.ForeignKey(name = "usuario_fk", value =  ConstraintMode.CONSTRAINT)
										),
				inverseJoinColumns = @JoinColumn(name = "role_id",
												referencedColumnName = "id",
												table = "role",
												unique = false,
												//updatable = false,
												foreignKey = @jakarta.persistence.ForeignKey(name = "role_fk", value =  ConstraintMode.CONSTRAINT)											
												)								
				)
	private List<Role> roles;
	
	
	/*Relacionamento Um pra muitos - Fim */
	 
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
	

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(id, other.id);
	}

	/*Autorização / Permissao / Autenticacao*/
	//São os acessos do usuario ROLE_ADMIN, ROLE_FUNCIONARIO..
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return roles;
	}

	@Override
	public String getPassword() {
		
		return this.senha;
	}

	@Override
	public String getUsername() {
		
		return this.login;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		
		//return UserDetails.super.isAccountNonExpired();
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		
		//return UserDetails.super.isAccountNonLocked();
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		
		//return UserDetails.super.isCredentialsNonExpired();
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		
		//return UserDetails.super.isEnabled();
		return true;
	}
}
