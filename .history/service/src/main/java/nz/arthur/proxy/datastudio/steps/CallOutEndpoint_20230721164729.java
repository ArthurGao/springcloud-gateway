package nz.paymark.proxy.datastudio.steps;

import nz.paymark.proxy.datastudio.common.Context;
import nz.paymark.proxy.datastudio.steps.common.FilterBase;
import nz.paymark.proxy.datastudio.steps.poststeps.PostCallSplunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CallOutEndpoint extends FilterBase {

    @Autowired
    private PostCallSplunk postCallSplunk;

    final Logger logger = LoggerFactory.getLogger(CallOutEndpoint.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("CallOutEndpoint");

        ServerHttpResponse response = exchange.getResponse();

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    HttpStatus statusCode = response.getStatusCode();
                    logger.info("Response code: " + statusCode);
                    Context.getInstance().setVariable("ResponseStatusCode", statusCode.toString());
                    postCallSplunk.execute();
                    HttpHeaders headers = response.getHeaders();
                    headers.clear();
                    headers.set(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, private");
                    headers.set(HttpHeaders.PRAGMA, "no-cache");
                    headers.set(HttpHeaders.CONTENT_TYPE, "application/vnd.paymark_api+json");

                    postCallSplunk.execute();

                    headers.set("X-Frame-Options", "DENY");
                    headers.set("X-XSS-Protection", "1; mode=block");
                    headers.set("X-Content-Type-Options", "nosniff");
                    headers.set("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                    headers.set("Referrer-Policy", "no-referrer");


                }));
    }
}
