package com.enigmaOne.enigmaOne.Config;



import com.enigmaOne.enigmaOne.persistence.types.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final JwtFilter jwtFilter;
    private final CorsConfig corsConfig;


    public SecurityConfig (JwtFilter jwtFilter,CorsConfig corsConfig){
        this.jwtFilter = jwtFilter;
        this.corsConfig =corsConfig;
    }

    //Encriptar contraseña
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors-> cors
                        .configurationSource(this.corsConfig.corsConfigurationSource())
                )
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers("/authentication/**").permitAll()
                                .requestMatchers("/check-status").authenticated()
                                .requestMatchers("/employees/createEmployee").hasAnyRole(Role.JEFE_DE_PROYECTO.name(),Role.GERENTE_GENERAL.name())
                                .requestMatchers("/employees/updateEmployee/**").hasAnyRole(Role.JEFE_DE_PROYECTO.name(),Role.GERENTE_GENERAL.name())
                                .requestMatchers("/employees/deleteEmployee").hasAnyRole(Role.JEFE_DE_PROYECTO.name(),Role.GERENTE_GENERAL.name())
                                .requestMatchers("/employees/{id}").permitAll()
                                .requestMatchers("/employees/searchForTerm/**").permitAll()
                                .requestMatchers("/employees/searchForName/**").permitAll()
                                .requestMatchers("/orders/**").hasAnyRole(Role.JEFE_DE_PROYECTO.name(),Role.GERENTE_GENERAL.name())
                                .requestMatchers("/materialOrders/**").permitAll()
                                .requestMatchers("/serviceOrders/**").permitAll()
                                .requestMatchers("/trackings/**").permitAll()
                                .requestMatchers("/movements/**").permitAll()
                                .requestMatchers("/detailExitMaterials/**").permitAll()
                                .requestMatchers("/detailEntryMaterials/**").permitAll()
                                .requestMatchers("/warehouses/**").permitAll()
                                .requestMatchers("/stocks/**").permitAll()
                                .requestMatchers("/positions/**").permitAll()
                                .anyRequest().authenticated()
                )
               // .httpBasic(Customizer.withDefaults());
               .addFilterBefore(this.jwtFilter,UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

}
