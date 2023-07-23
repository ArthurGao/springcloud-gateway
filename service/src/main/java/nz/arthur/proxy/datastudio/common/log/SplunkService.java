package nz.arthur.proxy.datastudio.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SplunkService {
    final Logger logger = LoggerFactory.getLogger(SplunkService.class);

    private final RestTemplate restTemplate;
    private final String splunkHost;
    private final String splunkPath;
    private final String splunkCollector;
    private final String splunkToken;

    public SplunkService(
            RestTemplate restTemplate,
            @Value("${splunk.host}") String splunkHost,
            @Value("${splunk.path}") String splunkPath,
            @Value("${splunk.collector}") String splunkCollector,
            @Value("${splunk.token}") String splunkToken
    ) {
        this.restTemplate = restTemplate;
        this.splunkHost = splunkHost;
        this.splunkPath = splunkPath;
        this.splunkCollector = splunkCollector;
        this.splunkToken = splunkToken;
    }

    public void sendEventToSplunk(int retryCount, String retryCondition, String logData) {
        String url = String.format("https://%s/%s/%s", splunkHost, splunkPath, splunkCollector);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", splunkToken);
        headers.set("User-Agent", "apigee {apiproxy.name}/{apiproxy.revision} ({organization.name}; {environment.name})");

        String payload = String.format("{\"event\": {\"retryCount\": %d, \"retryCondition\": \"%s\", \"logData\": \"%s\"}}",
                retryCount, retryCondition, logData);

        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            logger.debug("Event sent to Splunk successfully");
        } else {
            logger.debug("Failed to send event to Splunk. Status code: " + responseEntity.getStatusCode());
        }
    }
}
