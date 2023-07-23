package nz.arthur.proxy.datastudio.steps.presteps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import nz.arthur.proxy.datastudio.common.Context;
import nz.arthur.proxy.datastudio.common.log.SplunkService;
import nz.arthur.proxy.datastudio.steps.common.FilterBase;
import reactor.core.publisher.Mono;

@Component
public class CallSplunk extends FilterBase {

    @Autowired
    private SplunkService splunkService;
    final Logger logger = LoggerFactory.getLogger(CallSplunk.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("Call Splunk");
        String logData = Context.getInstance().getVariable("logData");
        logger.info("logData: " + logData);
        // splunkService.sendEventToSplunk(1, "", Context.getInstance().getVariable("logData"));
        return chain.filter(exchange);

    }
}
