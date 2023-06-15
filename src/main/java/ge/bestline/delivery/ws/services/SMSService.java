package ge.bestline.delivery.ws.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import ge.bestline.delivery.ws.dto.SendSmsResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
public class SMSService {
    private final String sender = "ExLine.ge";
    private final String urgent = "true";
    @Value("${sms.office.key}")
    private String apiKey;

    public HttpResponse<String> send(String destPhoneNum, String text) throws URISyntaxException, IOException, InterruptedException {
        Map<String, String> params = new HashMap<>();
        params.put("key", apiKey);
        params.put("sender", sender);
        params.put("urgent", urgent);
        params.put("destination", destPhoneNum);
        params.put("content", text);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://smsoffice.ge/api/v2/send/"))
                .POST(getParamsUrlEncoded(params))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .build();
        log.info("Sending SMS with params: " + request.toString());
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        SendSmsResponse responseObj = objectMapper.readValue(response.body(), SendSmsResponse.class);
        log.info("SMS SENDING RESPOSE: " + responseObj.toString());
        return response;
    }

    private HttpRequest.BodyPublisher getParamsUrlEncoded(Map<String, String> parameters) {
        String urlEncoded = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        return HttpRequest.BodyPublishers.ofString(urlEncoded);
    }
}

