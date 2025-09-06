package com.chris.utils;

import com.chris.properties.GoogleGeocodingProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class GoogleMapAPIUtil {

    private final String apiKey;

    public GoogleMapAPIUtil(GoogleGeocodingProperties props) {
        this.apiKey = props.getApiKey();
    }

    /**
     * 获取两点间的预计耗时（秒），mode 支持 driving/walking/bicycling/transit
     */
    public Optional<Integer> estimateDurationSeconds(
            double originLat, double originLng,
            double destLat, double destLng,
            String mode // driving/walking/bicycling/transit
    ) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String origin = originLat + "," + originLng;
            String destination = destLat + "," + destLng;

            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + URLEncoder.encode(origin, StandardCharsets.UTF_8) +
                    "&destination=" + URLEncoder.encode(destination, StandardCharsets.UTF_8) +
                    "&mode=" + mode +
                    "&key=" + apiKey;

            HttpGet request = new HttpGet(url);
            String json = httpClient.execute(request, response -> EntityUtils.toString(response.getEntity()));

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            if (!"OK".equals(root.path("status").asText())) return Optional.empty();

            JsonNode routes = root.path("routes");
            if (!routes.isArray() || routes.isEmpty()) return Optional.empty();

            JsonNode legs = routes.get(0).path("legs");
            if (!legs.isArray() || legs.isEmpty()) return Optional.empty();

            int duration = legs.get(0).path("duration").path("value").asInt(); // 秒
            return Optional.of(duration);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * 获取两点间的预计距离（米）
     */
    public Optional<Integer> estimateDistanceMeters(
            double originLat, double originLng,
            double destLat, double destLng,
            String mode
    ) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String origin = originLat + "," + originLng;
            String destination = destLat + "," + destLng;

            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + URLEncoder.encode(origin, StandardCharsets.UTF_8) +
                    "&destination=" + URLEncoder.encode(destination, StandardCharsets.UTF_8) +
                    "&mode=" + mode +
                    "&key=" + apiKey;

            HttpGet request = new HttpGet(url);
            String json = httpClient.execute(request, response -> EntityUtils.toString(response.getEntity()));

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            if (!"OK".equals(root.path("status").asText())) return Optional.empty();

            JsonNode routes = root.path("routes");
            if (!routes.isArray() || routes.isEmpty()) return Optional.empty();

            JsonNode legs = routes.get(0).path("legs");
            if (!legs.isArray() || legs.isEmpty()) return Optional.empty();

            int distance = legs.get(0).path("distance").path("value").asInt(); // 米
            return Optional.of(distance);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
