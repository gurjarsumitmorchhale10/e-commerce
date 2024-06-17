package com.luv2code.apigateway.filter;

import com.luv2code.apigateway.exception.AuthenticationException;
import com.luv2code.apigateway.util.JWTUtil;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);

    private final List<String> openEndpoints = List.of("/auth/register", "/auth/authenticate", "/eureka", "/actuator");
    private final Predicate<ServerHttpRequest> securedRequestPredicate = serverHttpRequest ->  openEndpoints.stream()
            .noneMatch(openEndpoint -> serverHttpRequest.getURI().getPath().contains(openEndpoint));

    public AuthorizationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();
            if (securedRequestPredicate.test(request)) {
                if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new AuthenticationException("Unauthorized, missing authorization header!");
                }
                String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (token == null || token.isEmpty()) {
                    throw new AuthenticationException("Unauthorized, token is empty!");
                }
                if (token.startsWith("Bearer ")) {
                    token = token.replaceFirst("Bearer ", "");
                }
                try {
                    JWTUtil.validateToken(token);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    throw new AuthenticationException("Authentication failed!");
                }

                request = request.mutate()
                        .header("logged-in-user", JWTUtil.extractUsername(token))
                        .build();
            }

            return chain.filter(exchange.mutate().request(request).build());
        });
    }


    public static class Config {

    }
}
