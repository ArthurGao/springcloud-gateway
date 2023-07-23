package nz.arthur.proxy.datastudio.steps.presteps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import nz.arthur.proxy.datastudio.common.log.SplunkMessageLogService;
import nz.arthur.proxy.datastudio.steps.common.FilterBase;
import reactor.core.publisher.Mono;

@Component
public class CreateLogMessage extends FilterBase {
    final Logger logger = LoggerFactory.getLogger(CreateLogMessage.class);

    @Autowired
    private SplunkMessageLogService splunkMessageLogService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("CreateLogMessage");
        splunkMessageLogService.execute();
        return chain.filter(exchange);
    }
}
