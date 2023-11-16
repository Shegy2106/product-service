package com.springmicroservices.apigateway.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Value("${keycloak.jwks_endpoint}")
    private String keycloakJwksEndpoint;
    //private final String YOUR_JWKS_ENDPOINT = "http://localhost:8181/realms/spring-boot-microservices-realm/protocol/openid-connect/certs";

    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/eureka/**")
                        .permitAll()
                        .anyExchange()
                        .authenticated());

        http.oauth2ResourceServer(httpSecurityOauth2ResourceServerConfigurer -> httpSecurityOauth2ResourceServerConfigurer.jwt(jwtConfigurer -> jwtConfigurer.jwkSetUri(keycloakJwksEndpoint)));

        return http.build();
    }

}
