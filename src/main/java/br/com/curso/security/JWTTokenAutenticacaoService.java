package br.com.curso.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.BeansException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import br.com.curso.ApplicationContextLoad;
import br.com.curso.model.Usuario;
import br.com.curso.repository.UsuarioRepository;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
@Component
public class JWTTokenAutenticacaoService {

	//tempo de validade do tokem em 2 dias
	private static final long EXPIRATION_TIME = 172800000;
	
	//Uma senha unica para compor a autenticacao e ajudar na segurança 
	private static final String SECRET = "SenhaSecreta"; 
	
	//Prefixo padrao de token 
	private static final String TOKEN_PREFIX =  "Bearer";
	
	private static final String HEADER_STRING = "Authorization"; 
	
	//Gerando o token de autenticaado e adiccionando ao cabeçalho
	public void addAuthentication(HttpServletResponse response, String username) throws Exception
	{
		//Geração de token
		String JWT = Jwts.builder()
							.setSubject(username)
							.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
							.signWith(SignatureAlgorithm.HS512, SECRET).compact();
		
		//Junta token com prefixo
		String token = TOKEN_PREFIX + " " + JWT; /*Bearer 4845eevevevevsfawqaacw*/
		
		//Adiciona no cabeçalho http
		response.addHeader(HEADER_STRING, token); /*Authorization: Bearer 4845eevevevevsfawqaacw*/
		
		//escreve o token como resposta no corpo http
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
	}
	
	//Retorna o usuario validado com token ou caso nao seja valido retorna null
	public Authentication getAuthentication(HttpServletRequest request)
	{
		String token = request.getHeader(HEADER_STRING);
		
		if (token != null)
		{
			String user =  Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody().getSubject();
			

			if(user != null)
			{
				Usuario usuario = ApplicationContextLoad.getApplicationContext()
 									.getBean(UsuarioRepository.class).findUserByLogin(user);
				
				if(usuario != null)
				{
					return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
				}
			}
								
		}

		//não authorizado
		return null;
		
	}

	
}
