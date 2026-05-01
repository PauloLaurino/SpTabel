package com.seprocom.assinatura.service;

import com.seprocom.assinatura.dto.AssinaturaCaminhoRequest;
import com.seprocom.assinatura.dto.AssinaturaRequest;
import com.seprocom.assinatura.dto.AssinaturaResponse;
import com.seprocom.assinatura.dto.DadosCertificado;
import com.seprocom.assinatura.exception.AssinaturaException;
import com.seprocom.assinatura.model.SessaoCertificado;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssinaturaService {

    private final CertificadoService certificadoService;
    private final TimestampService timestampService;
    private final PdfService pdfService;

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public AssinaturaResponse assinar(AssinaturaRequest request) {
        log.info("Iniciando processo de assinatura");
        
        try {
            byte[] documentoBytes = Base64.getDecoder().decode(request.getDocumento());
            
            String idSessao = request.getPin() != null && !request.getPin().isEmpty()
                    ? certificadoService.abrirSessao(request.getCertificado(), request.getPin())
                    : null;
            
            SessaoCertificado sessao = null;
            if (idSessao != null) {
                sessao = certificadoService.buscarSessao(idSessao)
                        .orElseThrow(() -> new AssinaturaException("SESS_001", "Sessão inválida ou expirada"));
            }
            
            byte[] hash = calcularHash(documentoBytes);
            
            byte[] assinatura = gerarAssinaturaPkcs7(hash, sessao);
            
            String timestamp = null;
            if (Boolean.TRUE.equals(request.getTimestamp())) {
                timestamp = timestampService.obterTimestamp(hash);
            }
            
            byte[] documentoAssinado = incorporarAssinatura(documentoBytes, assinatura);
            
            DadosCertificado dadosCert = certificadoService.extrairDadosCertificado(
                    sessao != null ? sessao.getCertificado() : null
            );
            
            if (idSessao != null) {
                certificadoService.fecharSessao(idSessao);
            }
            
            return AssinaturaResponse.builder()
                    .status("ok")
                    .documento_assinado(Base64.getEncoder().encodeToString(documentoAssinado))
                    .certificado(dadosCert)
                    .timestamp(timestamp)
                    .build();
                    
        } catch (AssinaturaException e) {
            throw e;
        } catch (Exception e) {
            throw new AssinaturaException("ASSIN_001", "Erro ao assinar documento: " + e.getMessage(), e);
        }
    }

    public void assinarPorCaminho(AssinaturaCaminhoRequest request) throws Exception {
        log.info("Iniciando assinatura por caminho");
        log.info("Arquivo entrada: {}", request.getCaminho());
        log.info("Arquivo saida: {}", request.getSaida());
        
        Path pathEntrada = Paths.get(request.getCaminho());
        if (!Files.exists(pathEntrada)) {
            throw new AssinaturaException("ARQ_001", "Arquivo de entrada não encontrado: " + request.getCaminho());
        }
        
        byte[] documentoBytes = Files.readAllBytes(pathEntrada);
        
        String idSessao = request.getPin() != null && !request.getPin().isEmpty()
                ? certificadoService.abrirSessao("A3", request.getPin())
                : null;
        
        SessaoCertificado sessao = null;
        if (idSessao != null) {
            sessao = certificadoService.buscarSessao(idSessao)
                    .orElseThrow(() -> new AssinaturaException("SESS_001", "Sessão inválida ou expirada"));
        }
        
        byte[] hash = calcularHash(documentoBytes);
        
        byte[] assinatura = gerarAssinaturaPkcs7(hash, sessao);
        
        if (Boolean.TRUE.equals(request.getTimestamp())) {
            String timestamp = timestampService.obterTimestamp(hash);
            log.info("Timestamp obtido: {}", timestamp != null ? "sim" : "não");
        }
        
        byte[] documentoAssinado = incorporarAssinatura(documentoBytes, assinatura);
        
        Path pathSaida = Paths.get(request.getSaida());
        Files.createDirectories(pathSaida.getParent());

        if (Boolean.TRUE.equals(request.getPdfa())) {
            log.info("Processando conversão para PDF/A");
            // Salva temporariamente e converte
            Path temp = Files.createTempFile("sign_", ".pdf");
            Files.write(temp, documentoAssinado);
            pdfService.converterParaPdfA(temp, pathSaida);
            Files.deleteIfExists(temp);
        } else {
            Files.write(pathSaida, documentoAssinado);
        }
        
        log.info("Arquivo assinado salvo em: {}", request.getSaida());
        
        if (idSessao != null) {
            certificadoService.fecharSessao(idSessao);
        }
    }

    private byte[] calcularHash(byte[] dados) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(dados);
    }

    private byte[] gerarAssinaturaPkcs7(byte[] hash, SessaoCertificado sessao) 
            throws Exception {
        
        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        
        if (sessao != null) {
            KeyStore keyStore = KeyStore.getInstance("PKCS11", "SunPKCS11");
            keyStore.load(null, null);
            
            String alias = keyStore.aliases().nextElement();
            PrivateKey chavePrivada = (PrivateKey) keyStore.getKey(alias, null);
            X509Certificate certificado = (X509Certificate) keyStore.getCertificate(alias);
            
            generator.addSignerInfoGenerator(
                new JcaSignerInfoGeneratorBuilder(
                    new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()
                ).build(
                    new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(chavePrivada),
                    certificado
                )
            );
            
            generator.addCertificates(new JcaCertStore(Collections.singletonList(certificado)));
            
            CMSSignedData signedData = generator.generate(
                new CMSProcessableByteArray(hash),
                true
            );
            
            return signedData.getEncoded();
        }
        
        throw new AssinaturaException("ASSIN_002", "Sessão de certificado não disponível");
    }

    private byte[] incorporarAssinatura(byte[] pdfBytes, byte[] assinaturaPkcs7) 
            throws IOException {
        
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }
}
