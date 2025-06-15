package com.medOnTime.apiGateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Allow login/register requests without token
        if (
                request.getURI().getPath().contains("/auth/login") ||
                        request.getURI().getPath().contains("/user/register") ||
                        request.getURI().getPath().contains("/pharmacy/add-pharmacy") ||
                        request.getURI().getPath().contains("/pharmacy/get-all-pharmacies-for-selection") ||
                        request.getURI().getPath().contains("/pharmacy/get-pharmacy-key") ||
                        request.getURI().getPath().contains("/pharmacy/get-all-pharmacies") ||
                        request.getURI().getPath().contains("/pharmacy/set-approval") ||
                        request.getURI().getPath().contains("/pharmacy/set-rejection") ||
                        request.getURI().getPath().contains("/pharmacy/certificate-url/{licenseNumber}")
        ) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);
            String path = request.getURI().getPath();

            if (!isAllowed(role, path)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // You can also pass the user ID or email downstream if needed
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", claims.get("userId", String.class))
                    .header("X-User-Email", claims.getSubject())
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isAllowed(String role, String path) {
        if (path.startsWith("/medicine") && role.equals("PATIENT")) return true;
        if (path.startsWith("/user") && (role.equals("ADMIN") || role.equals("PHARMACIST") || role.equals("PATIENT"))) return true;
        if (path.startsWith("/reminder") && role.equals("PATIENT")) return true;
        if (path.startsWith("/pharmacy/set-approval") && role.equals("ADMIN")) return true;
        if (path.startsWith("/pharmacy/set-rejection") && role.equals("ADMIN")) return true;
        if (path.startsWith("/pharmacy/certificate-url") && role.equals("ADMIN")) return true;
        if (path.startsWith("/chatbot") && (role.equals("ADMIN") || role.equals("PHARMACIST") || role.equals("PATIENT"))) return true;
        // Add more access rules here
        return false;
    }
}

