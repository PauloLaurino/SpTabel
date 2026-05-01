package com.seprocom.assinatura.client;

import com.seprocom.assinatura.dto.AssinaturaRequest;
import com.seprocom.assinatura.dto.AssinaturaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
public class AssinaturaClient {

    private final RestTemplate restTemplate;
    private String baseUrl;
    private String apiKey;

    public AssinaturaClient() {
        this.restTemplate = new RestTemplate();
    }

    public void configure(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public AssinaturaResponse assinarDocumento(byte[] documentoPdf, String pin, boolean incluirTimestamp) {
        String documentoBase64 = Base64.getEncoder().encodeToString(documentoPdf);
        
        AssinaturaRequest request = AssinaturaRequest.builder()
                .documento(documentoBase64)
                .certificado("A3")
                .pin(pin)
                .timestamp(incluirTimestamp)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", apiKey);

        HttpEntity<AssinaturaRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<AssinaturaResponse> response = restTemplate.exchange(
                baseUrl + "/api/v1/assinar",
                HttpMethod.POST,
                entity,
                AssinaturaResponse.class
        );

        return response.getBody();
    }

    public AssinaturaResponse assinarDocumentoSimples(byte[] documentoPdf, String pin) {
        return assinarDocumento(documentoPdf, pin, true);
    }

    public byte[] baixarDocumentoAssinado(AssinaturaResponse response) {
        return Base64.getDecoder().decode(response.getDocumento_assinado());
    }
}
