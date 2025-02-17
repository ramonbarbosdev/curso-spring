package br.com.curso.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.com.curso.service.ImplementacaoUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity {

    @Bean
    public UserDetailsService userDetailsService() {
        return new ImplementacaoUserDetailsService();
    }

    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Configura a proteção CSRF
                .disable()  // Desabilita CSRF (talvez necessário para API)
            )
            .authorizeRequests(auth -> auth
        		.requestMatchers("/", "/index").permitAll()  // Permite acesso público para a página inicial
                .anyRequest().authenticated()  // Exige autenticação para qualquer outra URL
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))  // Define o caminho para logout
                .logoutSuccessUrl("/index")  // Redireciona para a página inicial após logout
                .permitAll()
            )
            .addFilterBefore(new JWTLoginFilter("/login", authenticationManager(null)), UsernamePasswordAuthenticationFilter.class)  // Adiciona o filtro de login com JWT
            .addFilterBefore(new JwtApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class)  // Adiciona o filtro de autenticação JWT
            .httpBasic();  // Permite autenticação básica (se necessário)

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
