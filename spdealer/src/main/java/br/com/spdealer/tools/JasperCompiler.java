package br.com.spdealer.tools;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class JasperCompiler {

    public static void main(String[] args) {
        Path src = Paths.get("src/main/resources/reports");
        if (!Files.exists(src)) {
            System.out.println("Nenhum diretório 'src/main/resources/reports' encontrado. Pulando compilação JRXML.");
            return;
        }

        try {
            Files.walkFileTree(src, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String name = file.getFileName().toString().toLowerCase();
                    if (name.endsWith(".jrxml")) {
                        Path relative = src.relativize(file);
                        Path targetDir = Paths.get("target/classes/reports").resolve(relative).getParent();
                        if (targetDir != null && !Files.exists(targetDir)) {
                            Files.createDirectories(targetDir);
                        }
                        Path jasperOut = Paths.get("target/classes/reports").resolve(relative.toString().replaceAll("\\.jrxml$",".jasper"));
                        try {
                            System.out.println("Compilando JRXML: " + file + " -> " + jasperOut);
                            JasperCompileManager.compileReportToFile(file.toString(), jasperOut.toString());
                            System.out.println("OK: " + jasperOut);
                        } catch (JRException e) {
                            System.err.println("ERRO CRÍTICO ao compilar " + file.toAbsolutePath() + ": " + e.getMessage());
                        } catch (Throwable t) {
                            System.err.println("Erro inesperado em " + file.toAbsolutePath() + ": " + t.getMessage());
                            t.printStackTrace();
                        }
                    } else if (name.endsWith(".jrtx")) {
                        Path relative = src.relativize(file);
                        Path targetFile = Paths.get("target/classes/reports").resolve(relative);
                        Path targetDir = targetFile.getParent();
                        if (targetDir != null && !Files.exists(targetDir)) {
                            Files.createDirectories(targetDir);
                        }
                        System.out.println("Copiando JRTX: " + file + " -> " + targetFile);
                        Files.copy(file, targetFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Throwable t) {
            // Fail-safe: do not rethrow. Log and continue so Maven build can proceed.
            System.err.println("Erro durante varredura de JRXMLs: " + t.getMessage());
        }
    }
}
