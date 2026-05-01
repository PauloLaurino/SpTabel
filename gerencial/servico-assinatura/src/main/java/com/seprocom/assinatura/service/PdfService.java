package com.seprocom.assinatura.service;

import com.seprocom.assinatura.dto.AgruparRequest;
import com.seprocom.assinatura.dto.CopiarPaginaRequest;
import com.seprocom.assinatura.exception.AssinaturaException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service
public class PdfService {

    public void copiarPaginas(CopiarPaginaRequest request) throws Exception {
        log.info("Iniciando cópia de páginas");
        log.info("Livro: {}", request.getCaminhoLivro());
        log.info("Páginas: {} a {}", request.getPaginaInicial(), request.getPaginaFinal());
        log.info("Saída: {}", request.getSaida());

        Path pathEntrada = Paths.get(request.getCaminhoLivro());
        if (!Files.exists(pathEntrada)) {
            throw new AssinaturaException("ARQ_001", "Arquivo de entrada não encontrado: " + request.getCaminhoLivro());
        }

        int paginaInicial = request.getPaginaInicial();
        int paginaFinal = request.getPaginaFinal() > 0 ? request.getPaginaFinal() : paginaInicial;

        if (paginaFinal < paginaInicial) {
            paginaFinal = paginaInicial;
        }

        try (PDDocument sourceDoc = Loader.loadPDF(pathEntrada.toFile())) {
            int totalPages = sourceDoc.getNumberOfPages();
            
            if (paginaInicial > totalPages) {
                throw new AssinaturaException("PDF_001", "Página inicial excede o número de páginas do documento: " + totalPages);
            }

            if (paginaFinal > totalPages) {
                paginaFinal = totalPages;
                log.warn("Página final ajustada para {}", totalPages);
            }

            try (PDDocument newDoc = new PDDocument()) {
                for (int i = paginaInicial; i <= paginaFinal; i++) {
                    PDPage page = sourceDoc.getPage(i - 1);
                    newDoc.addPage(page);
                }

                Path pathSaida = Paths.get(request.getSaida());
                Files.createDirectories(pathSaida.getParent());
                newDoc.save(pathSaida.toFile());
            }
        }

        log.info("Páginas copiadas com sucesso para: {}", request.getSaida());
    }

    public int agruparPdfs(AgruparRequest request) throws Exception {
        log.info("Iniciando agrupamento de PDFs");
        log.info("Pasta: {}", request.getPasta());
        log.info("Prefixo: {}", request.getPrefixo());
        log.info("Saída: {}", request.getSaida());

        Path pastaPath = Paths.get(request.getPasta());
        if (!Files.exists(pastaPath) || !Files.isDirectory(pastaPath)) {
            throw new AssinaturaException("ARQ_002", "Pasta não encontrada: " + request.getPasta());
        }

        File[] arquivos;
        if (request.getPrefixo() != null && !request.getPrefixo().isEmpty()) {
            arquivos = pastaPath.toFile().listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".pdf") && name.startsWith(request.getPrefixo())
            );
        } else {
            arquivos = pastaPath.toFile().listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".pdf")
            );
        }

        if (arquivos == null || arquivos.length == 0) {
            throw new AssinaturaException("PDF_002", "Nenhum arquivo PDF encontrado na pasta");
        }

        List<File> pdfsOrdenados = new ArrayList<>();
        for (File f : arquivos) {
            if (f.isFile()) {
                pdfsOrdenados.add(f);
            }
        }

        if (pdfsOrdenados.isEmpty()) {
            throw new AssinaturaException("PDF_002", "Nenhum arquivo PDF encontrado na pasta");
        }

        pdfsOrdenados.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(request.getSaida());

        for (File pdf : pdfsOrdenados) {
            log.info("Adicionando: {}", pdf.getName());
            merger.addSource(pdf);
        }

        merger.mergeDocuments(null);

        Path pathSaida = Paths.get(request.getSaida());
        Files.createDirectories(pathSaida.getParent());

        log.info("Agrupamento concluído: {} PDFs unidos em {}", pdfsOrdenados.size(), request.getSaida());
        return pdfsOrdenados.size();
    }
    public List<Map<String, Object>> listarPdfs(String caminho) throws IOException {
        Path pastaPath = Paths.get(caminho);
        if (!Files.exists(pastaPath) || !Files.isDirectory(pastaPath)) {
            throw new AssinaturaException("ARQ_002", "Pasta não encontrada ou não é um diretório: " + caminho);
        }

        try (Stream<Path> walk = Files.walk(pastaPath, 1)) {
            return walk
                    .filter(p -> !Files.isDirectory(p))
                    .filter(p -> p.toString().toLowerCase().endsWith(".pdf"))
                    .map(p -> {
                        Map<String, Object> info = new java.util.HashMap<>();
                        try {
                            info.put("nome", p.getFileName().toString());
                            info.put("caminho", p.toAbsolutePath().toString());
                            info.put("tamanho", Files.size(p));
                            info.put("dataModificacao", Files.getLastModifiedTime(p).toInstant().toString());
                        } catch (IOException e) {
                            log.error("Erro ao ler metadados do arquivo: {}", p, e);
                        }
                        return info;
                    })
                    .collect(Collectors.toList());
        }
    }

    public void converterParaPdfA(Path entrada, Path saida) throws Exception {
        log.info("Convertendo para PDF/A: {}", entrada.getFileName());
        // Lógica básica de conversão usando PDFBox
        // Para uma conversão PDF/A-1b completa, seria necessário o perfil ICC e metadados XMP
        // Por enquanto, faremos o re-save garantindo a estrutura básica
        try (PDDocument doc = Loader.loadPDF(entrada.toFile())) {
            // Aqui poderíamos adicionar o PDF/A identification metadata
            doc.save(saida.toFile());
        }
    }
}