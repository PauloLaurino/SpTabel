package com.seprocom.assinatura.service;

import com.seprocom.assinatura.config.ConfigProperties;
import com.seprocom.assinatura.dto.DadosCertificado;
import com.seprocom.assinatura.exception.AssinaturaException;
import com.seprocom.assinatura.model.SessaoCertificado;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificadoService {

    private final ConfigProperties config;
    private final Map<String, SessaoCertificado> sessoesAtivas = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("Inicializando serviço de certificado A3");
        listarTokensDisponiveis();
    }

    private void listarTokensDisponiveis() {
        for (Provider provider : Security.getProviders()) {
            log.info("Provider: {} - {}", provider.getName(), provider.getVersion());
        }
    }

    public String abrirSessao(String tipoToken, String pin) {
        log.info("Abrindo sessão para token: {}", tipoToken);
        
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS11", "SunPKCS11");
            keyStore.load(null, pin.toCharArray());
            
            String alias = keyStore.aliases().nextElement();
            X509Certificate certificado = (X509Certificate) keyStore.getCertificate(alias);
            
            if (certificado == null) {
                throw new AssinaturaException("CERT_001", "Certificado não encontrado no token");
            }
            
            SessaoCertificado sessao = new SessaoCertificado(
                    certificado, 
                    tipoToken, 
                    config.getSessaoMinutosValidade()
            );
            sessao.autenticar();
            
            String idSessao = sessao.getIdSessao();
            sessoesAtivas.put(idSessao, sessao);
            
            log.info("Sessão aberta com sucesso: {}", idSessao);
            return idSessao;
            
        } catch (KeyStoreException e) {
            throw new AssinaturaException("CERT_002", "Erro ao acessar token: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new AssinaturaException("CERT_003", "Erro ao abrir sessão: " + e.getMessage(), e);
        }
    }

    public Optional<SessaoCertificado> buscarSessao(String idSessao) {
        SessaoCertificado sessao = sessoesAtivas.get(idSessao);
        
        if (sessao != null && !sessao.estaExpirada() && sessao.isAutenticado()) {
            return Optional.of(sessao);
        }
        
        if (sessao != null) {
            sessoesAtivas.remove(idSessao);
        }
        
        return Optional.empty();
    }

    public void fecharSessao(String idSessao) {
        log.info("Fechando sessão: {}", idSessao);
        sessoesAtivas.remove(idSessao);
    }

    public DadosCertificado extrairDadosCertificado(X509Certificate certificado) {
        return DadosCertificado.builder()
                .cn(certificado.getSubjectX500Principal().getName())
                .validade(certificado.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ISO_DATE))
                .emissor(certificado.getIssuerX500Principal().getName())
                .algoritmo(certificado.getSigAlgName())
                .cpfCnpj(extrairCpfCnpj(certificado))
                .build();
    }

    private String extrairCpfCnpj(X509Certificate certificado) {
        String dn = certificado.getSubjectX500Principal().getName();
        
        if (dn.contains("CNPJ=")) {
            return dn.contains("CNPJ=") 
                    ? dn.substring(dn.indexOf("CNPJ=") + 5, dn.indexOf(","))
                    : null;
        }
        if (dn.contains("CPF=")) {
            return dn.contains("CPF=") 
                    ? dn.substring(dn.indexOf("CPF=") + 4, dn.indexOf(","))
                    : null;
        }
        return null;
    }

    public int getSessoesAtivasCount() {
        return sessoesAtivas.size();
    }
}
