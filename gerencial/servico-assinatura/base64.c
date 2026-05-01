/*
 *  BASE64.C - Rotinas de conversao Base64 para NetExpress/ACUCOBOL
 *  
 *  Compilar: gcc -shared -o base64.dll base64.c -Wall
 */

#include <windows.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

/* Tabela Base64 */
static const char base64_chars[] = 
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

/*
 *  BASE64ENC - Converte binario para Base64
 */
__declspec(dllexport) void WINAPI BASE64ENC(
    const char* input,
    int* inlen,
    char* output,
    int* outlen)
{
    int i;
    int j;
    int len = *inlen;
    int result_len;
    
    if (input == NULL || len <= 0 || output == NULL) {
        *outlen = 0;
        return;
    }
    
    /* Calcular tamanho do resultado */
    result_len = ((len + 2) / 3) * 4;
    *outlen = result_len;
    
    j = 0;
    for (i = 0; i < len; i += 3) {
        unsigned char b0 = (unsigned char)input[i];
        unsigned char b1 = (i + 1 < len) ? (unsigned char)input[i + 1] : 0;
        unsigned char b2 = (i + 2 < len) ? (unsigned char)input[i + 2] : 0;
        
        char block[4];
        block[0] = base64_chars[b0 >> 2];
        block[1] = base64_chars[((b0 & 0x03) << 4) | (b1 >> 4)];
        block[2] = base64_chars[((b1 & 0x0f) << 2) | (b2 >> 6)];
        block[3] = base64_chars[b2 & 0x3f];
        
        /* Preencher com '=' se necessario */
        if (i + 1 >= len) block[2] = '=';
        if (i + 2 >= len) block[3] = '=';
        
        output[j++] = block[0];
        output[j++] = block[1];
        output[j++] = block[2];
        output[j++] = block[3];
    }
}

/*
 *  BASE64DEC - Converte Base64 para binario
 */
__declspec(dllexport) void WINAPI BASE64DEC(
    const char* input,
    int* inlen,
    char* output,
    int* outlen)
{
    int i;
    int j;
    int len = *inlen;
    int result_len;
    
    if (input == NULL || len <= 0 || output == NULL) {
        *outlen = 0;
        return;
    }
    
    /* Criar tabela de decodificacao */
    int tab[256];
    for (i = 0; i < 256; i++) tab[i] = -1;
    for (i = 0; i < 64; i++) {
        tab[(unsigned char)base64_chars[i]] = i;
    }
    
    /* Remover espacos e contar padding */
    char* clean = (char*)malloc(len + 1);
    int clean_len = 0;
    for (i = 0; i < len; i++) {
        if (input[i] != ' ' && input[i] != '\n' && 
            input[i] != '\r' && input[i] != '\t') {
            clean[clean_len++] = input[i];
        }
    }
    
    /* Contar padding */
    int padding = 0;
    if (clean_len > 0 && clean[clean_len - 1] == '=') padding++;
    if (clean_len > 1 && clean[clean_len - 2] == '=') padding++;
    
    /* Remover padding da contagem */
    while (clean_len > 0 && clean[clean_len - 1] == '=') {
        clean_len--;
    }
    
    if (clean_len % 4 != 0) {
        free(clean);
        *outlen = 0;
        return;
    }
    
    result_len = (clean_len / 4) * 3;
    if (padding > 0) result_len -= padding;
    *outlen = result_len;
    
    j = 0;
    for (i = 0; i < clean_len; i += 4) {
        char block[4];
        block[0] = clean[i];
        block[1] = clean[i + 1];
        block[2] = (i + 2 < clean_len) ? clean[i + 2] : 'A';
        block[3] = (i + 3 < clean_len) ? clean[i + 3] : 'A';
        
        unsigned char out[3];
        int out_len = 0;
        
        out[0] = (unsigned char)((tab[(unsigned char)block[0]] << 2) | 
                                 (tab[(unsigned char)block[1]] >> 4));
        out_len = 1;
        
        if (block[2] != '=') {
            out[1] = (unsigned char)(((tab[(unsigned char)block[1]] & 0x0f) << 4) | 
                                     (tab[(unsigned char)block[2]] >> 2));
            out_len = 2;
        }
        
        if (block[3] != '=') {
            out[2] = (unsigned char)(((tab[(unsigned char)block[2]] & 0x03) << 6) | 
                                     tab[(unsigned char)block[3]]);
            out_len = 3;
        }
        
        output[j++] = out[0];
        if (out_len > 1) output[j++] = out[1];
        if (out_len > 2) output[j++] = out[2];
    }
    
    free(clean);
}

/* DllMain - ponto de entrada da DLL */
BOOL WINAPI DllMain(HINSTANCE hinstDLL, DWORD fdwReason, LPVOID lpvReserved) {
    return TRUE;
}
