package nz.paymark.proxy.datastudio.steps.common;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public abstract class FilterBase implements GatewayFilter, Ordered {
    protected int order;

    @Override
    public abstract Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain);

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
