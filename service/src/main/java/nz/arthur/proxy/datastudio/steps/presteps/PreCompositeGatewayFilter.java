package nz.paymark.proxy.datastudio.steps.presteps;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class PreCompositeGatewayFilter implements GatewayFilter {

    private final List<GatewayFilter> filters;

    public PreCompositeGatewayFilter(List<GatewayFilter> filters) {
        this.filters = filters;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (filters.isEmpty()) {
            return chain.filter(exchange);
        }

        GatewayFilterChain filterChain = new GatewayFilterChain() {
            private int index = 0;

            @Override
            public Mono<Void> filter(ServerWebExchange exchange) {
                if (index >= filters.size()) {
                    return chain.filter(exchange);
                }

                GatewayFilter nextFilter = filters.get(index);
                index++;
                return nextFilter.filter(exchange, this);
            }
        };

        return filterChain.filter(exchange);
    }
}
