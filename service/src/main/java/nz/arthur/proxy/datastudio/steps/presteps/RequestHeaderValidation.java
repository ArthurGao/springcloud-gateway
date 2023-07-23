package nz.arthur.proxy.datastudio.steps.presteps;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import nz.arthur.proxy.datastudio.steps.common.FilterBase;
import reactor.core.publisher.Mono;

@Component
public class RequestHeaderValidation extends FilterBase {

    // Constants for the valid values of headers
    private static final String VALID_CONTENT_TYPE = "application/vnd.arthur_api+json";
    private static final String VALID_ACCEPT = "application/vnd.arthur_api+json";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String contentType = headers.getFirst("Content-Type");
        String accept = headers.getFirst("Accept");
        String requestMethod = request.getMethodValue();

        if (("POST".equals(requestMethod) || "PUT".equals(requestMethod))) {
            validateContentType(contentType);
        }
        if (!"DELETE".equals(requestMethod)) {
            validateAccept(accept);
        }

        return chain.filter(exchange);
    }

    private void validateContentType(String contentType) {
        if (contentType == null || !contentType.trim().equals(VALID_CONTENT_TYPE)) {
            throwError("invalid_content_type");
        }
    }

    private void validateAccept(String accept) {
        if (accept == null || !accept.trim().equals(VALID_ACCEPT)) {
            throwError("invalid_accept");
        }
    }

    private void throwError(String errorId) {
        // Implement the logic to handle the error
        // For example, you can log the error or throw a custom exception.
        // This depends on your application's requirements.
        // Replace the following line with the actual logic.
        throw new RuntimeException(errorId);
    }
}
