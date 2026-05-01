# Serviço de Assinatura Digital - Manual de Integração

## Visão Geral

Serviço REST genérico para assinatura digital de documentos PDF com certificado A3 (token/smartcard).

## Arquitetura

```
┌─────────────────┐      HTTP POST       ┌──────────────────────────────┐
│   ERP COBOL     │ ──────────────────►  │   Serviço Assinatura (Java)   │
│                 │                      │   Porta: 8443                 │
└─────────────────┘                      └──────────────┬─────────────────┘
                                                      │
                            ┌──────────────────────────┼────────────────────┐
                            │                          │                    │
                            ▼                          ▼                    ▼
                    ┌───────────────┐          ┌───────────────┐    ┌─────────────┐
                    │ Token A3      │          │ Servidor TSA  │    │ Repositório │
                    │ (PKCS#11)     │          │ (Timestamp)    │    │ Documentos  │
                    └───────────────┘          └───────────────┘    └─────────────┘
```

## Requisitos

- Java 17+
- Maven 3.8+
- Driver PKCS#11 do token (SafeNet, GD, etc.)
- Certificado A3 configurado

## Configuração

Edite `src/main/resources/application.yml`:

```yaml
server:
  port: 8443

assinatura:
  sessao-minutos-validade: 30
  tsa:
    url: https://tsp.datalat.com.br/tsp
    timeout-segundos: 30
  token:
    tipo: PKCS11
    biblioteca: /usr/lib/libeToken.so  # Caminho do driver
```

## Build e Execução

```bash
# Compilar
mvn clean package -DskipTests

# Executar
java -jar target/servico-assinatura-1.0.0.jar

# Ou com Docker
docker build -t seprocom/servico-assinatura .
docker run -p 8443:8443 seprocom/servico-assinatura
```

## API REST

### Endpoint Principal

```
POST /api/v1/assinar
```

#### Request

```json
{
  "documento": "BASE64_DO_PDF",
  "certificado": "A3",
  "pin": "123456",
  "timestamp": true
}
```

#### Response

```json
{
  "status": "ok",
  "documento_assinado": "BASE64_DO_PDF_ASSINADO",
  "certificado": {
    "cn": "EMPRESA X LTDA",
    "cpf_cnpj": "12345678000199",
    "validade": "2026-12-31",
    "emissor": "ICP-Brasil",
    "algoritmo": "SHA256withRSA"
  },
  "timestamp": "BASE64_DO_TIMESTAMP"
}
```

### Outros Endpoints

```
GET  /api/v1/health          # Health check
POST /api/v1/sessao/abrir   # Abre sessão autenticada
POST /api/v1/sessao/fechar  # Fecha sessão
```

## Integração com COBOL

### Exemplo WinHTTP (COBOL)

```cobol
01  HTTP-REQUEST.
    03  URL            PIC X(200).
    03  METHOD         PIC X(10).
    03  BODY           PIC X(100000).
    03  BODY-LEN       PIC 9(5).

01  HTTP-RESPONSE.
    03  STATUS         PIC 9(3).
    03  RESP-BODY      PIC X(100000).

PROCEDURE DIVISION.

MAIN-PARA.

    MOVE "POST" TO METHOD
    MOVE "https://servidor:8443/api/v1/assinar" TO URL
    MOVE function base64-encode(documento-pdf) TO BODY
    STRING 
        '{"documento":"' BODY '",'
        '"certificado":"A3",'
        '"pin":"123456",'
        '"timestamp":true}'
        DELIMITED BY SIZE TO BODY
    MOVE LENGTH OF BODY TO BODY-LEN

    CALL "WINHTTP" USING HTTP-REQUEST, HTTP-RESPONSE

    IF STATUS = 200
        DISPLAY "Documento assinado com sucesso"
    ELSE
        DISPLAY "Erro: " STATUS
    END-IF.
```

## Clientes Disponíveis

### Java Client (para seus outros projetos)

```java
@Autowired
private AssinaturaClient assinaturaClient;

public void assinarDocumento(byte[] pdf) {
    assinaturaClient.configure("http://localhost:8443", "sua-api-key");
    
    AssinaturaResponse response = assinaturaClient.assinarDocumento(pdf, "123456", true);
    
    byte[] documentoAssinado = assinaturaClient.baixarDocumentoAssinado(response);
}
```

## Padrões Implementados

- **PDF**: PAdES (PDF Advanced Electronic Signature)
- **Assinatura**: PKCS#7 / CMS
- **Hash**: SHA-256
- **Timestamp**: RFC 3161

## Segurança

1. **Sessão autenticada**: PIN digitado uma vez, sessão válida por X minutos
2. **API Key**: Autenticação entre sistemas
3. **HTTPS**: Comunicação criptografada
4. **NÃO armazena**: PIN em banco ou arquivo

## Servidores TSA Recomendados

- SERPRO
- Certisign
- DataLat
- Valid

## Troubleshooting

| Problema | Solução |
|----------|---------|
| Token não detectado | Instalar driver PKCS#11 |
| Erro de PIN | Verificar PIN e tentativas restantes |
| Timestamp falhou | Verificar conectividade com TSA |
| Sessão expirada | Re-autenticar com PIN |
