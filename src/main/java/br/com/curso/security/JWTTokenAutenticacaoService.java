package br.com.curso.security;

import java.io.IOException;
import java.util.Date;

import org.apache.logging.log4j.util.Base64Util;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.fasterxml.jackson.databind.deser.Deserializers.Base;

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
import javax.crypto.spec.SecretKeySpec;

@Service
public class JWTTokenAutenticacaoService {

    // Tempo de validade do token em 2 dias
    private static final long EXPIRATION_TIME = 172800000;

    // Prefixo padrão de token
    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String HEADER_STRING = "Authorization";

    // Chave secreta forte

    private static final String SECRET_KEY_BASE64 = "HaqrDaAaICtFZNXjm5Q3dPNgAZX+bnf6efMy2HuIO1Iq928rcmtTltoAFhsROHxN\r\nwtcHjB6FWudgjqxBMXAP8w==";
    
  
    public static SecretKeySpec createSecretKey()
    {
  
    	 String cleanedKey = SECRET_KEY_BASE64.replaceAll("\\s", "");

         byte[] decodedKey = java.util.Base64.getDecoder().decode(cleanedKey); 


         return new SecretKeySpec(decodedKey,  "HmacSHA512");
    }
    public void addAuthentication(HttpServletResponse response, String username) throws Exception {
      
    	SecretKeySpec secretKey = createSecretKey();
    	
    	// Geração de token
        String JWT = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, secretKey) // Usando chave simples
                .compact();
        
        String token = TOKEN_PREFIX + " " + JWT;
        response.addHeader(HEADER_STRING, token);
        liberacaoCors(response);
        response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
    }

    // Retorna o usuário validado com token ou, caso não seja válido, retorna null
    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
       
    	SecretKeySpec secretKey = createSecretKey();
    	
    	String token = request.getHeader(HEADER_STRING);
    	//System.out.println("Token recebido: " + token);  
    	
        if (token != null && token.startsWith(TOKEN_PREFIX))
        {
        	
            // Remove o prefixo "Bearer " e faz o parsing do token
            String jwt = token.replace(TOKEN_PREFIX, "").trim();

            try
            {
                String user = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(jwt)  // Use a variável `jwt`
                        .getBody()
                        .getSubject();

                if (user != null)
                {
                    Usuario usuario = ApplicationContextLoad.getApplicationContext()
                            .getBean(UsuarioRepository.class)
                            .findUserByLogin(user);

                    if (usuario != null)
                    {
                    	//comparar o token gerado com o token no banco de dados
                    	if(jwt.equalsIgnoreCase(usuario.getToken())) 
                    	{
                    		 return new UsernamePasswordAuthenticationToken(
                                     usuario.getLogin(),
                                     usuario.getSenha(),
                                     usuario.getAuthorities());
                    	}
                    	
                       
                    }
                }
                
            }
            catch (Exception e)
            {
            	 System.out.println("Erro na autenticação: " + e.getMessage());
        		 try {
					response.getOutputStream().println("TOKEN expirado, faça um novo login!");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}             
        
            		 
            }
        }
        
        liberacaoCors(response);

        // Não autorizado
        return null;
    }
	private void liberacaoCors(HttpServletResponse response)
	{
		
		if (response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		if (response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		
		
		if (response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}

		
	}
}
