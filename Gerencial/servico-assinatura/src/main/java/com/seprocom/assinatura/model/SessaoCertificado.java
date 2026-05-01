package com.seprocom.assinatura.model;

import lombok.Data;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SessaoCertificado {
    private String idSessao;
    private X509Certificate certificado;
    private LocalDateTime momentoAutenticacao;
    private LocalDateTime expiracao;
    private String tipoToken;
    private boolean autenticado;

    public SessaoCertificado(X509Certificate certificado, String tipoToken, int minutosValidade) {
        this.idSessao = UUID.randomUUID().toString();
        this.certificado = certificado;
        this.tipoToken = tipoToken;
        this.momentoAutenticacao = LocalDateTime.now();
        this.expiracao = LocalDateTime.now().plusMinutes(minutosValidade);
        this.autenticado = false;
    }

    public boolean estaExpirada() {
        return LocalDateTime.now().isAfter(this.expiracao);
    }

    public void autenticar() {
        this.autenticado = true;
    }
}
