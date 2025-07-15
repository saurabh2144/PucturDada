package com.example.saurabh.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class TranslationService {

    private static final String TRANSLATE_URL = "http://localhost:5000/translate";
    private static final String DETECT_URL = "http://localhost:5000/detect";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public TranslationService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // Language Detection
    public String detectLanguage(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("q", text);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(DETECT_URL, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            if (root.isArray() && root.size() > 0) {
                return root.get(0).get("language").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "en"; // fallback
    }

    // Translation
    public String translate(String text, String targetLang) {
        try {
            String sourceLang = detectLanguage(text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("q", text);
            body.put("source", sourceLang);
            body.put("target", targetLang);
            body.put("format", "text");

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(TRANSLATE_URL, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.get("translatedText").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text; // original if error
    }
}
