package org.davidbohl.jellyfin_sleeparr.jellyfin.api;

import org.davidbohl.jellyfin_sleeparr.jellyfin.api.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class JellyfinApiConsumer {

    private final RestTemplate restTemplate;

    @Value("${sleeparr.jellyfin.endpoint}")
    private String jellyfinBaseUrl;

    @Value("${sleeparr.jellyfin.apiKey}")
    private String apiKey;

    public JellyfinApiConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Emby-Token", apiKey);
        return headers;
    }

    public List<Session> getActiveSessions() {
        String url = jellyfinBaseUrl + "/Sessions";

        HttpEntity<Void> requestEntity = new HttpEntity<>(getHeaders());
        ResponseEntity<Session[]> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Session[].class);

        assert response.getBody() != null;
        return Arrays.asList(response.getBody());
    }

    public void goHome(String sessionId) {
        String url = jellyfinBaseUrl + "/Sessions/" + sessionId + "/Command";
        HttpEntity<Command> requestEntity = new HttpEntity<>(new Command("GoHome"), getHeaders());
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
    }

    public void pausePlayback(String sessionId) {
        String url = jellyfinBaseUrl + "/Sessions/" + sessionId + "/Playing/Pause";
        HttpEntity<Void> requestEntity = new HttpEntity<>(getHeaders());
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
    }

    public void sendMessage(String sessionId, String header, String message, long durations) {
        String url = jellyfinBaseUrl + "/Sessions/" + sessionId + "/Message";
        HttpEntity<Message> requestEntity = new HttpEntity<>(new Message(header, message, durations), getHeaders());
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
    }

    public CustomQueryResult postCustomQuery(CustomQuery customQuery) {
        String url = jellyfinBaseUrl + "/user_usage_stats/submit_custom_query";
        HttpEntity<CustomQuery> requestEntity = new HttpEntity<>(customQuery, getHeaders());
        ResponseEntity<CustomQueryResult> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, CustomQueryResult.class);
        return response.getBody();
    }
}
