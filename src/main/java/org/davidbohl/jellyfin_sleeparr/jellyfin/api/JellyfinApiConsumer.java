package org.davidbohl.jellyfin_sleeparr.jellyfin.api;

import jakarta.servlet.http.HttpSession;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.models.*;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
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

    private final ObjectFactory<HttpSession> httpSessionFactory;


    public JellyfinApiConsumer(RestTemplate restTemplate, ObjectFactory<HttpSession> httpSessionFactory) {
        this.restTemplate = restTemplate;
        this.httpSessionFactory = httpSessionFactory;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

//        String deviceId = UUID.randomUUID().toString();
//
//        try {
//            deviceId = httpSessionFactory.getObject().getId();
//        } catch (Throwable e) {
//            // ignore... i tried so hard and got so far
//        }

        headers.set("Authorization", "MediaBrowser Token=\"%s\", Client=\"sleeparr\", Version=\"0.0.1\", DeviceId=\"sleeparr\", Device=\"sleeparr\""
                .formatted(apiKey));
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

    public JellyfinUser getUserById(String id) {
        String url = jellyfinBaseUrl + "/Users/" + id;
        HttpEntity<Object> requestEntity = new HttpEntity<>(getHeaders());
        ResponseEntity<JellyfinUser> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, JellyfinUser.class);
        return response.getBody();
    }

    public List<JellyfinUser> getAllUsers() {
        String url = jellyfinBaseUrl + "/Users";
        HttpEntity<CustomQuery> requestEntity = new HttpEntity<>(getHeaders());
        ResponseEntity<JellyfinUser[]> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, JellyfinUser[].class);
        assert response.getBody() != null;
        return Arrays.stream(response.getBody()).toList();
    }

    public AuthenticationResponse authenticate(String username, String password) throws AuthenticationException {

        if (username == null || password == null) {
            throw new BadCredentialsException("Missing username or password");
        }

        String url = jellyfinBaseUrl + "/Users/AuthenticateByName";

        // Authenticate against Jellyfin API
        AuthenticateByNameRequest authenticateByNameRequest = new AuthenticateByNameRequest(username, password);
        HttpEntity<AuthenticateByNameRequest> requestEntity = new HttpEntity<>(authenticateByNameRequest, getHeaders());
        ResponseEntity<AuthenticationResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, AuthenticationResponse.class);

        if (responseEntity.getStatusCode().isError()) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return responseEntity.getBody();
    }

}
