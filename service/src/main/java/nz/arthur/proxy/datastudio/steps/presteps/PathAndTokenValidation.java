package nz.arthur.proxy.datastudio.steps.presteps;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import nz.arthur.proxy.datastudio.common.Context;
import nz.arthur.proxy.datastudio.steps.common.FilterBase;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@Component
public class PathAndTokenValidation extends FilterBase {

    private static final Pattern[] SALESFORCE_ALLOWED_PATHS = {
            Pattern.compile("^/marketing/prepare$"),
            Pattern.compile("^/marketing/validate$"),
            Pattern.compile("^/reset$"),
            Pattern.compile("^/$")
    };

    private static final Pattern[] REGISTRATION_ALLOWED_PATHS = {
            Pattern.compile("^/legalname$"),
            Pattern.compile("^/eligible$"),
            Pattern.compile("^/verify$"),
            Pattern.compile("^/signup$"),
            Pattern.compile("^/complete$"),
            Pattern.compile("^/reset$"),
            Pattern.compile("^/[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}/details$"),
            Pattern.compile("^/marketing/details$"),
            Pattern.compile("^/marketing/signup$")
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String productName = Context.getInstance().getVariable("apigee.apiproduct.name");
        String path = extractDesiredPath(exchange.getRequest().getPath().value());

        if (!isValidPath(path, SALESFORCE_ALLOWED_PATHS) && !isValidPath(path, REGISTRATION_ALLOWED_PATHS)) {
            throwPathNotFound();
        }

        Pattern[] allowedPaths = new Pattern[0];
        if (productName != null && productName.contains("Salesforce")) {
            allowedPaths = SALESFORCE_ALLOWED_PATHS;
        } else if (productName != null && productName.contains("Registration")) {
            allowedPaths = REGISTRATION_ALLOWED_PATHS;
        } else {
            throwInvalidToken(productName, path);
        }

        if (!isValidPath(path, allowedPaths)) {
            throwInvalidToken(productName, path);
        }

        String legalName = exchange.getRequest().getQueryParams().getFirst("legalName");

        if (path.equals("/") && (legalName == null || legalName.trim().isEmpty())) {
            throw new RuntimeException("missing_legalname");
        }

        return chain.filter(exchange);
    }

    private static String extractDesiredPath(String fullPath) {
        // Find the index of the second occurrence of '/'
        int secondSlashIndex = fullPath.indexOf('/', 1);

        // If the second slash exists, extract the substring after it
        if (secondSlashIndex != -1) {
            return fullPath.substring(secondSlashIndex);
        }

        // If the second slash does not exist, return the full path
        return fullPath;
    }

    private boolean isValidPath(String path, Pattern[] allowedPaths) {
        for (Pattern allowedPath : allowedPaths) {
            if (allowedPath.matcher(path).matches()) {
                return true;
            }
        }
        return false;
    }

    private void throwPathNotFound() {
        throw new RuntimeException("not_found");
    }

    private void throwInvalidToken(String productName, String path) {
        throw new RuntimeException("invalid_token_for_path");
    }
}
