package nz.paymark.proxy.datastudio;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HealthCheckController {

    @GetMapping("/check")
    public Mono<ResponseEntity<String>> getResource() {
        return Mono.just(ResponseEntity.ok()
                .body("UP"));

    }
}