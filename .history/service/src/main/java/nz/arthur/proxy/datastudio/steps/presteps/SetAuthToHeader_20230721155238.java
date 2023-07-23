package nz.paymark.proxy.datastudio.steps.presteps;

import nz.paymark.proxy.datastudio.steps.common.FilterBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class SetAuthToHeader extends FilterBase {

    final Logger logger = LoggerFactory.getLogger(SetAuthToHeader.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("SetAuthToHeader");
        // Get the existing headers
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        // Create a new HttpHeaders instance with the modified headers
        HttpHeaders modifiedHeaders = new HttpHeaders();
        modifiedHeaders.add(HttpHeaders.AUTHORIZATION, "password");
        modifiedHeaders.add("Partner", "TestProductRegistrationService");
        modifiedHeaders.add("Paymark-suigeneris", String.valueOf(UUID.randomUUID()));
        // Create a new ServerHttpRequest with the modified headers
        ServerHttpRequest modifiedRequest = request.mutate()
                .headers(httpHeaders -> httpHeaders.addAll(modifiedHeaders))
                .build();

        // Update the exchange with the modified request
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(modifiedRequest)
                .build();
        return chain.filter(exchange);

    }
}
