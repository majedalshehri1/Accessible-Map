package com.wakeb.yusradmin.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public class ApiClient {

    private final String baseUrl;
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    private final ObjectMapper om = new ObjectMapper();


    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public <T> T get(String path, TypeReference<T> type) throws Exception {
        var req = base(path).GET().build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() >= 200 && res.statusCode() < 300) {
            return om.readValue(res.body(), type);
        }
        throw new RuntimeException("GET " + path + " failed: " + res.statusCode() + " -> " + res.body());
    }

    public <T> T put(String path, Object body, TypeReference<T> type) throws Exception {
        var json = om.writeValueAsString(body);
        var req = base(path).PUT(HttpRequest.BodyPublishers.ofString(json)).build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() >= 200 && res.statusCode() < 300) {
            return (type == null || res.body().isBlank()) ? null : om.readValue(res.body(), type);
        }
        throw new RuntimeException("PUT " + path + " failed: " + res.statusCode() + " -> " + res.body());
    }

    public void delete(String path) throws Exception {
        var req = base(path).DELETE().build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new RuntimeException("DELETE " + path + " failed: " + res.statusCode() + " -> " + res.body());
        }
    }

    private HttpRequest.Builder base(String path) {
        var b = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");
        var jwt = AuthService.getInstance().getCurrentToken();
        if (jwt != null && !jwt.isBlank()) {
            b.header("Authorization", "Bearer " + jwt);
        }
        return b;
    }
    // In ApiClient.java, add this method:
    public HttpResponse<String> getRaw(String path) throws Exception {
        var req = base(path).GET().build();
        System.out.println("Requesting URL: " + req.uri()); // Add this line for debugging
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }
}