package com.example.ecommerce_backend.config;

import com.example.ecommerce_backend.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends GenericFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        String path = httpReq.getRequestURI();

        // Skip token validation for login/signup
        if (path.equals("/account") || path.equals("/token")) {
            chain.doFilter(request, response);
            return;
        }

        // Extract Authorization header
        String authHeader = httpReq.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println(" Missing or invalid Authorization header.");
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String email = jwtUtil.validateToken(token);
            if (email == null) {
                System.out.println("Invalid token: " + token);
                chain.doFilter(request, response);
                return;
            }

            System.out.println("Authenticated user: " + email);

            // Set Authentication in SecurityContextHolder
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpReq));
            SecurityContextHolder.getContext().setAuthentication(authentication); // Set user in security context

        } catch (Exception e) {
            System.out.println("JWT validation failed: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }
}
