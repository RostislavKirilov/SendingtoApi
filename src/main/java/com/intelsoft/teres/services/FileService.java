package com.intelsoft.teres.services;

import com.intelsoft.exceptions.NapServiceException;
import io.vavr.control.Try;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;

@Service
public class FileService {

    public String processAndSendFile(String fileContent, String signature) {
        String signedContent = addSignature(fileContent, signature);
        String napApiUrl = "https://httpbin.org/post";

        return Try.of(() -> {
            boolean isSent = sendToNap(napApiUrl, signedContent);
            if (isSent) {
                return "File sent successfully.";
            } else {
                throw new NapServiceException("Failed to send file to НАП.");
            }
        }).getOrElseThrow(e -> new NapServiceException("Error processing the file.", e));
    }

    private String addSignature(String content, String signature) {
        return content + "\nSignature: " + signature;
    }

    private boolean sendToNap(String url, String content) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> requestEntity = new HttpEntity<>(content, headers);

        return Try.of(() -> {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        }).getOrElseThrow(e -> new NapServiceException("Error sending file to НАП.", e));
    }
}