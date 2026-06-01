package com.asistente_viajes_api.project.security.config;

import com.asistente_viajes_api.project.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity , AuthenticationProvider authenticationProvider ) throws Exception {

        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Auth público
                        .requestMatchers("/auth/**").permitAll()

                        // Usuarios: solo ADMIN
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")

                        // Clientes
                        .requestMatchers(HttpMethod.GET, "/clientes/**")
                        .hasAnyRole("ADMIN", "ASESOR", "LECTOR")

                        .requestMatchers(HttpMethod.POST, "/clientes/**")
                        .hasAnyRole("ADMIN", "ASESOR")

                        .requestMatchers(HttpMethod.PUT, "/clientes/**")
                        .hasAnyRole("ADMIN", "ASESOR")

                        .requestMatchers(HttpMethod.DELETE, "/clientes/**")
                        .hasRole("ADMIN")

                        // Destinos: consultar todos, modificar solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/destinos/**")
                        .hasAnyRole("ADMIN", "ASESOR", "LECTOR")

                        .requestMatchers(HttpMethod.POST, "/destinos/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/destinos/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/destinos/**")
                        .hasRole("ADMIN")

                        // Consultas de viaje
                        .requestMatchers(HttpMethod.GET, "/consultas-viaje/**")
                        .hasAnyRole("ADMIN", "ASESOR", "LECTOR")

                        .requestMatchers(HttpMethod.POST, "/consultas-viaje/*/generar-recomendacion")
                        .hasAnyRole("ADMIN", "ASESOR")

                        .requestMatchers(HttpMethod.POST, "/consultas-viaje/**")
                        .hasAnyRole("ADMIN", "ASESOR")

                        .requestMatchers(HttpMethod.PUT, "/consultas-viaje/**")
                        .hasAnyRole("ADMIN", "ASESOR")

                        .requestMatchers(HttpMethod.DELETE, "/consultas-viaje/**")
                        .hasRole("ADMIN")

                        // Cualquier otra request necesita estar autenticada
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager( AuthenticationConfiguration authenticationConfiguration)throws Exception {

        return authenticationConfiguration.getAuthenticationManager();

    }




    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
