package br.com.curso.security;

import java.io.IOException;
import java.util.Date;



import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.curso.ApplicationContextLoad;
import br.com.curso.model.Usuario;
import br.com.curso.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;




@Service
public class JWTTokenAutenticacaoService {

    // Tempo de validade do token em 2 dias
    private static final long EXPIRATION_TIME = 172800000;

    // Prefixo padrão de token
    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String HEADER_STRING = "Authorization";

    // Chave secreta forte
    
   // private static final String SECRET = "*UmaSenhaSuperSecretaComPeloMenos64CaracteresParaHS512"; // A chave secreta
    private static final SecretKey SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public void addAuthentication(HttpServletResponse response, String username) throws Exception {
        // Geração de token
        String JWT = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET) // Usando chave simples
                .compact();
        
        String token = TOKEN_PREFIX + " " + JWT;
        response.addHeader(HEADER_STRING, token);
        response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
    }

    // Retorna o usuário validado com token ou, caso não seja válido, retorna null
    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
       
    	String token = request.getHeader(HEADER_STRING);
    	//System.out.println("Token recebido: " + token);  
    	
        if (token != null && token.startsWith(TOKEN_PREFIX))
        {
        	
                // Remove o prefixo "Bearer " e faz o parsing do token
                String jwt = token.replace(TOKEN_PREFIX, "").trim();

                try {
                    String user = Jwts.parserBuilder()
                            .setSigningKey(SECRET)
                            .build()
                            .parseClaimsJws(jwt)  // Use a variável `jwt`
                            .getBody()
                            .getSubject();

                    if (user != null) {
                        Usuario usuario = ApplicationContextLoad.getApplicationContext()
                                .getBean(UsuarioRepository.class)
                                .findUserByLogin(user);

                        if (usuario != null) {
                            return new UsernamePasswordAuthenticationToken(
                                    usuario.getLogin(),
                                    usuario.getSenha(),
                                    usuario.getAuthorities());
                        }
                    }
                } catch (Exception e) {
                    // Token inválido ou expirado
                    System.out.println("Erro na autenticação: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return null;
                }
        }

        // Não autorizado
        return null;
    }
}
