package com.selador.util;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SanitizerRunner {

    public static void main(String[] args) throws Exception {
        Path cwd = Path.of(System.getProperty("user.dir"));
        Path input = cwd.resolve("selados.JSON");
        Path fallback = cwd.resolve("selados.JSON12");
        Path output = cwd.resolve("selados.JSON12.sanitizado");

        if (!Files.exists(input)) {
            System.out.println("Arquivo 'selados.JSON' não encontrado, usando 'selados.JSON12' como input.");
            input = fallback;
        }

        if (!Files.exists(input)) {
            System.err.println("Nenhum arquivo de entrada encontrado (selados.JSON ou selados.JSON12). Abortando.");
            System.exit(2);
        }

        String original = Files.readString(input, StandardCharsets.UTF_8);
        String sanitizado = SeloJsonSanitizerNotas.sanitizar(original);

        Files.writeString(output, sanitizado, StandardCharsets.UTF_8);
        System.out.println("Sanitização concluída. Arquivo gravado em: " + output.toAbsolutePath());
    }
}
