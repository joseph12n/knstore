package com.mycompany.knstore.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Removes invalid or expired bearer tokens from public catalog requests so the resource server
 * treats them as anonymous instead of failing with a 401 for a corrupt Authorization header.
 * Registered as a servlet filter before the Spring Security chain.
 */
@Component
@Order(-101)
public class InvalidBearerTokenFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(InvalidBearerTokenFilter.class);

    private static final Pattern PUBLIC_CATALOG_PATHS = Pattern.compile("^/api/(categorias|subcategorias|productos|marcas)(/.*)?$");

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtDecoder jwtDecoder;

    public InvalidBearerTokenFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !PUBLIC_CATALOG_PATHS.matcher(path).matches();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (
            authorization != null &&
            authorization.startsWith(BEARER_PREFIX) &&
            !isValidToken(authorization.substring(BEARER_PREFIX.length()))
        ) {
            LOG.debug("Removing invalid bearer token from public catalog request {}", request.getRequestURI());
            HttpServletRequestWrapper sanitizedRequest = new HttpServletRequestWrapper(request) {
                @Override
                public String getHeader(String name) {
                    if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name)) {
                        return null;
                    }
                    return super.getHeader(name);
                }
            };
            filterChain.doFilter(sanitizedRequest, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isValidToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
