package com.seprocom.assinatura.service;

import com.seprocom.assinatura.config.ConfigProperties;
import com.seprocom.assinatura.exception.AssinaturaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimestampService {

    private final ConfigProperties config;

    public String obterTimestamp(byte[] hash) {
        log.info("Obtendo timestamp para hash de {} bytes", hash.length);
        
        ConfigProperties.TsaConfig tsa = config.getTsa();
        
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(tsa.getTimeoutSegundos()))
                    .build();

            String body = buildTsaRequest(hash);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(tsa.getUrl()))
                    .header("Content-Type", "application/timestamp-query")
                    .header("Accept", "application/timestamp-reply")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            
            if (response.statusCode() != 200) {
                throw new AssinaturaException("TSA_001", 
                        "Erro ao obter timestamp: HTTP " + response.statusCode());
            }
            
            byte[] timestampReply = response.body();
            String timestampBase64 = Base64.getEncoder().encodeToString(timestampReply);
            
            log.info("Timestamp obtido com sucesso");
            return timestampBase64;
            
        } catch (AssinaturaException e) {
            throw e;
        } catch (IOException e) {
            throw new AssinaturaException("TSA_002", "Erro de comunicação com TSA: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new AssinaturaException("TSA_003", "Erro ao obter timestamp: " + e.getMessage(), e);
        }
    }

    private String buildTsaRequest(byte[] hash) {
        return Base64.getEncoder().encodeToString(hash);
    }
}
