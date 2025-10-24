package com.enigmaOne.enigmaOne.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtFilter extends OncePerRequestFilter {


    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Validar que sea un Header Authorization valido
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 2. Extraer token de forma segura
            String[] parts = authHeader.split(" ");
            if (parts.length < 2) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = parts[1].trim();

            // 3. Validar que el JWT sea valido
            boolean valid = false;
            try {
                valid = this.jwtUtil.isValidToken(jwt);
            } catch (Exception ex) {
                // si jwtUtil lanza, no bloquear la petición aquí; dejar que pase sin auth
                filterChain.doFilter(request, response);
                return;
            }

            if (!valid) {
                filterChain.doFilter(request, response);
                return;
            }

            // 4. Cargar el usuario del UserDetailsService de forma segura
            String email = null;
            try {
                email = this.jwtUtil.getUsername(jwt);
            } catch (Exception ex) {
                filterChain.doFilter(request, response);
                return;
            }

            if (email == null || email.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails user = null;
            try {
                user = this.userDetailsService.loadUserByUsername(email);
            } catch (Exception ex) {
                // no se pudo cargar usuario -> continuar sin auth
                filterChain.doFilter(request, response);
                return;
            }

            if (user == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 5. Cargar al usuario en el contexto de seguridad.
            Object principalForToken = user;
            // Si tu UserDetails personalizado expone el id en getId(), podrías ponerlo como principal
            // Pero como user puede ser de distintos tipos, usamos el username como principal por seguridad
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (Exception e) {
            // En caso de error inesperado, no interrumpir la petición: log y seguir
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);

    }


}
