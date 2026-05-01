/*
 *  BASE64CONV.C - Conversor Base64 via linha de comando
 *  
 *  Compilar: gcc -o base64conv.exe base64conv.c
 *  
 *  Uso via COBOL:
 *    CALL 'SYSTEM' USING 'base64conv.exe -e arquivo.pdf base64.txt'
 *    CALL 'SYSTEM' USING 'base64conv.exe -d base64.txt arquivo.pdf'
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

static const char base64_chars[] = 
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

/* Converte binario para Base64 */
int encode_base64(const char* input_file, const char* output_file) {
    FILE* fin = fopen(input_file, "rb");
    if (!fin) {
        fprintf(stderr, "Erro ao abrir arquivo de entrada: %s\n", input_file);
        return 1;
    }
    
    FILE* fout = fopen(output_file, "w");
    if (!fout) {
        fprintf(stderr, "Erro ao criar arquivo de saida: %s\n", output_file);
        fclose(fin);
        return 1;
    }
    
    unsigned char buffer[3000];
    int bytes_read;
    
    while ((bytes_read = fread(buffer, 1, 3000, fin)) > 0) {
        int i;
        for (i = 0; i < bytes_read; i += 3) {
            unsigned char b0 = buffer[i];
            unsigned char b1 = (i + 1 < bytes_read) ? buffer[i + 1] : 0;
            unsigned char b2 = (i + 2 < bytes_read) ? buffer[i + 2] : 0;
            
            char out[4];
            out[0] = base64_chars[b0 >> 2];
            out[1] = base64_chars[((b0 & 0x03) << 4) | (b1 >> 4)];
            out[2] = base64_chars[((b1 & 0x0f) << 2) | (b2 >> 6)];
            out[3] = base64_chars[b2 & 0x3f];
            
            if (i + 1 >= bytes_read) out[2] = '=';
            if (i + 2 >= bytes_read) out[3] = '=';
            
            fwrite(out, 1, 4, fout);
        }
    }
    
    fprintf(fout, "\n");
    
    fclose(fin);
    fclose(fout);
    return 0;
}

/* Converte Base64 para binario */
int decode_base64(const char* input_file, const char* output_file) {
    FILE* fin = fopen(input_file, "r");
    if (!fin) {
        fprintf(stderr, "Erro ao abrir arquivo de entrada: %s\n", input_file);
        return 1;
    }
    
    /* Ler todo o conteudo */
    char* content = malloc(500000);
    int total = 0;
    int c;
    while ((c = fgetc(fin)) != EOF && total < 500000 - 1) {
        if (c != ' ' && c != '\n' && c != '\r' && c != '\t') {
            content[total++] = (char)c;
        }
    }
    content[total] = '\0';
    fclose(fin);
    
    /* Remover padding */
    while (total > 0 && content[total - 1] == '=') {
        total--;
    }
    
    FILE* fout = fopen(output_file, "wb");
    if (!fout) {
        fprintf(stderr, "Erro ao criar arquivo de saida: %s\n", output_file);
        free(content);
        return 1;
    }
    
    /* Criar tabela de decodificacao */
    int tab[256];
    int i;
    for (i = 0; i < 256; i++) tab[i] = -1;
    for (i = 0; i < 64; i++) {
        tab[(unsigned char)base64_chars[i]] = i;
    }
    
    int j;
    for (j = 0; j < total; j += 4) {
        if (j + 3 >= total) break;
        
        char block[4];
        block[0] = content[j];
        block[1] = content[j + 1];
        block[2] = content[j + 2];
        block[3] = content[j + 3];
        
        unsigned char out[3];
        out[0] = (unsigned char)((tab[(unsigned char)block[0]] << 2) | 
                                 (tab[(unsigned char)block[1]] >> 4));
        fputc(out[0], fout);
        
        if (block[2] != '=') {
            out[1] = (unsigned char)(((tab[(unsigned char)block[1]] & 0x0f) << 4) | 
                                     (tab[(unsigned char)block[2]] >> 2));
            fputc(out[1], fout);
        }
        
        if (block[3] != '=') {
            out[2] = (unsigned char)(((tab[(unsigned char)block[2]] & 0x03) << 6) | 
                                     tab[(unsigned char)block[3]]);
            fputc(out[2], fout);
        }
    }
    
    free(content);
    fclose(fout);
    return 0;
}

int main(int argc, char* argv[]) {
    if (argc < 4) {
        printf("Uso:\n");
        printf("  base64conv -e <arquivo_entrada> <arquivo_saida>\n");
        printf("  base64conv -d <arquivo_entrada> <arquivo_saida>\n");
        printf("\nExemplos:\n");
        printf("  base64conv -e documento.pdf documento.b64\n");
        printf("  base64conv -d documento.b64 documento.pdf\n");
        return 1;
    }
    
    if (strcmp(argv[1], "-e") == 0) {
        return encode_base64(argv[2], argv[3]);
    } else if (strcmp(argv[1], "-d") == 0) {
        return decode_base64(argv[2], argv[3]);
    } else {
        fprintf(stderr, "Opcao invalida: %s\n", argv[1]);
        return 1;
    }
}
