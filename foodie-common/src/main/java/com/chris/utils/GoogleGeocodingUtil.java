package com.chris.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class GoogleGeocodingUtil {

    @Value("${chris.google.maps.api-key}")
    private String apiKey;

    public Optional<double[]> fetchLatLng(String address) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                    encodedAddress + "&key=" + apiKey;
            HttpGet request = new HttpGet(url);

            String json = httpClient.execute(request, response ->
                    EntityUtils.toString(response.getEntity())
            );
            // 用 Jackson 解析
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            String status = root.path("status").asText();
            if (!"OK".equals(status)) return Optional.empty();

            JsonNode results = root.path("results");
            if (!results.isArray() || results.isEmpty()) return Optional.empty();

            JsonNode location = results.get(0).path("geometry").path("location");
            double lat = location.path("lat").asDouble();
            double lng = location.path("lng").asDouble();
            return Optional.of(new double[]{lat, lng});
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
