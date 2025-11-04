package com.enigmaOne.enigmaOne.Config;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

/**
 * AuthorizationManager que permite:
 * - Si el status es APROBADO o RECHAZADO -> permite a todos
 * - Si el status es PENDIENTE -> permite solo a los roles JEF.../GERENTE.../ADMINISTRADOR
 * Busca el parámetro `status` en query param (?status=...), y si no existe intenta
 * obtenerlo del último segmento de la URI (/.../{status}).
 */
public class OrderApprovalAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final Set<String> allowedRoles = Set.of(
            "ROLE_JEFE_DE_PROYECTO",
            "ROLE_GERENTE_GENERAL",
            "ROLE_ADMINISTRADOR"
    );

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();

        // Intentar obtener status por query param
        String status = request.getParameter("status");
        if (status == null || status.isBlank()) {
            // intentar obtener del último segmento de la URI
            String path = request.getRequestURI();
            if (path != null && !path.isBlank()) {
                String[] parts = path.split("/");
                if (parts.length > 0) {
                    status = parts[parts.length - 1];
                }
            }
        }

        if (status == null || status.isBlank()) {
            return new AuthorizationDecision(false);
        }

        String s = status.trim().toUpperCase();

        // Si es APROBADO o RECHAZADO permitir a todos
        if (s.equals("APROBADO") || s.equals("RECHAZADO")) {
            return new AuthorizationDecision(true);
        }

        // Si es PENDIENTE, verificar roles permitidos
        if (s.equals("PENDIENTE")) {
            Authentication auth = authenticationSupplier == null ? null : authenticationSupplier.get();
            if (auth == null || !auth.isAuthenticated()) {
                return new AuthorizationDecision(false);
            }
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            for (GrantedAuthority ga : authorities) {
                if (allowedRoles.contains(ga.getAuthority())) {
                    return new AuthorizationDecision(true);
                }
            }
            return new AuthorizationDecision(false);
        }

        // Por defecto denegar
        return new AuthorizationDecision(false);
    }
}
