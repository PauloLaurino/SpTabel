# Emissor NFS-e Nacional - Arquitetura e Esqueleto

Este documento apresenta o esqueleto do emissor de NFS-e para o ambiente nacional brasileiro, baseado no padrão ABRASF NFS-e 2.02.

## Visão Geral da Arquitetura

```
Emitir Nota - Componente Tabela Lista fin_reccab por intervalo de data inicial e final, filtrar fin_reccab.OBS_REC != 'CANCELADO'
ao clicar no registro abrir tela de detalhes da nota, com os dados do fin_reccab e fin_recitem, e um botão para emitir a nota.

Opção para selecionar varios registros (fin_reccab) para agrupar em uma NFS-e com o valor total da soma dos registros selecionados, com a descrição Emolumentos do periodo DD/MM/AAAA a DD/MM/AAAA.

O registro(s) selecionado(s) devem gerar um registro em notascab(Cabeçalho) e notasitem (Itens) com os dados do fin_reccab e fin_recitem para emitir a nota.

um formulario com abas Emitir Nota, Consultar NFS-e, Cancelar NFS-e, Listar Notas, cada aba com seu filtro e componente tabela listando registros nas abas Consulta , Cancelar e Listar as tabelas serão notascab(Cabeçalho) e notasitem (Itens)

#IMPORTANTE: a nota é enviada a partir do registro criado em notascab(Cabeçalho) e notasitem (Itens) e numero da nota deve ser registrado na tabela fin_reccab.nfse_rec e data em fin_reccab.nfse_data_rec datatime

┌─────────────────────────────────────────────────────────────────────────┐
│                           FRONTEND (React)                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │  Emitir     │  │  Consultar  │  │  Cancelar   │  │  Listar     │    │
│  │  Nota       │  │  NFS-e      │  │  NFS-e      │  │  Notas      │    │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘    │
└─────────┼────────────────┼────────────────┼────────────────┼───────────┘
          │                │                │                │
          ▼                ▼                ▼                ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                     REST API (NfseController)                           │
│  GET/POST/PUT/DELETE /api/nfse/*                                       │
└─────────────────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        SERVICE LAYER                                     │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐       │
│  │  NotasService    │  │ NfseEmissaoService│ │ JwtService       │       │
│  │  (CRUD notas)    │  │ (Emissão WS)     │  │ (Autenticação)  │       │
│  └────────┬─────────┘  └────────┬─────────┘  └──────────────────┘       │
└───────────┼──────────────────────┼─────────────────────────────────────┘
            │                      │
            ▼                      ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      DTOs (Data Transfer Objects)                       │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐       │
│  │  NotasCabDTO     │  │ NfseRequestDTO   │  │ NfseResponseDTO  │       │
│  │  NotasDetDTO    │  │ (RPS)            │  │ (NFS-e)          │       │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘       │
└─────────────────────────────────────────────────────────────────────────┘
            │                      │
            ▼                      ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                     CLIENTE SOAP (WS Layer)                            │
│  ┌─────────────────────────┐  ┌─────────────────────────┐               │
│  │ NfseNacionalClient      │  │ NfseAbrasfClient        │               │
│  │ (Genérico multi-município)│ │ (Específico SP)        │               │
│  └─────────────────────────┘  └─────────────────────────┘               │
│                                                                         │
│  Suporta:                                                              │
│  - São Paulo (3550308)                                                │
│  - Pato Branco (4118200)                                              │
│  - Curitiba (4314900)                                                 │
│  - Porto Alegre (4314902)                                             │
│  - Guarulhos (3518800)                                                │
│  - Londrina (4116900)                                                 │
│  - Maringá (4115200)                                                   │
│  - Campinas (3509502)                                                 │
│  - Sorocaba (3552205)                                                 │
│  - São Bernardo do Campo (3548702)                                    │
└─────────────────────────────────────────────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                     WEBSERVICE EXTERNO                                  │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  Padrão ABRASF NFS-e 2.02                                       │   │
│  │  - RecepcionarRps                                               │   │
│  │  - ConsultarNfsePorNumero                                       │   │
│  │  - ConsultarNfsePorRps                                          │   │
│  │  - ConsultarLoteRps                                             │   │
│  │  - CancelarNfse                                                 │   │
│  │  - ConsultarNfsePorData                                         │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
```

## Estrutura de Arquivos

```
Protesto/Gerencial/
├── src/main/java/com/seprocom/gerencial/
│   ├── config/
│   │   ├── NfseNacionalConfig.java        ← Configuração do cliente
│   │   └── JwtService.java                 ← Autenticação JWT
│   ├── controller/
│   │   └── NfseController.java             ← Endpoints REST
│   ├── dto/
│   │   ├── NotasCabDTO.java                ← DTO Cabeçalho nota
│   │   ├── NotasDetDTO.java                ← DTO Itens nota
│   │   ├── NfseRequestDTO.java             ← DTO Request RPS
│   │   └── NfseResponseDTO.java            ← DTO Response NFS-e
│   ├── entity/
│   │   ├── NotasCab.java                   ← Entidade JPA
│   │   └── NotasDet.java                   ← Entidade JPA
│   ├── exception/
│   │   └── BusinessException.java          ← Exceções de negócio
│   ├── repository/
│   │   ├── NotasCabRepository.java        ← Repository JPA
│   │   └── NotasDetRepository.java        ← Repository JPA
│   ├── service/
│   │   ├── NotasService.java              ← CRUD notas
│   │   ├── NfseEmissaoService.java        ← Emissão via WebService
│   │   └── JwtService.java                ← Autenticação
│   └── ws/
│       ├── NfseNacionalClient.java         ← Cliente genérico
│       └── NfseAbrasfClient.java           ← Cliente específico SP
└── src/main/resources/
    └── application-nfse.properties         ← Configurações
```

## Fluxo de Emissão de NFS-e

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Usuário    │────▶│  Controller  │────▶│   Service   │────▶│    WS       │
│  (Frontend) │     │  (REST API)   │     │  (Negocio)   │     │  (SOAP)     │
└──────────────┘     └──────────────┘     └──────────────┘     └──────────────┘
     │                     │                    │                   │
     │  1. Criar nota      │                    │                   │
     │───────────────────▶│                    │                   │
     │                     │  2. Salvar nota    │                   │
     │                     │───────────────────▶│                   │
     │                     │                    │  3. Emitir NFS-e  │
     │                     │                    │──────────────────▶│
     │                     │                    │                   │
     │                     │                    │  4. Receber RPS   │
     │                     │                    │◀──────────────────│
     │                     │                    │                   │
     │                     │  5. Atualizar nota │                   │
     │                     │◀───────────────────│                   │
     │                     │                    │                   │
     │  6. Retornar nota   │                    │                   │
     │◀────────────────────│                    │                   │
```

## Configuração por Município

### application.properties

```properties
# Configuração Nacional NFS-e


Todos os dados do Emitente estão disponiveis na tabela parametros
Tabela parametros
    CAMINHOCERTIFICADO_PAR
	NFSe_IM
	NFSe_USUARIO
	NFSe_SENHA
	NFSe_AMBIENTE
    CNPJ_PAR
    CODIGO_CID
INNER JOIN Cidade ON parametros.CODIGO_CID = cidade.CODIGO_CID
LIMIT 1

nfse.nacional.municipio-default=PATO_BRANCO

# Pato Branco - Paraná (IBGE: 4118200)
nfse.nacional.endpoint= IF (parametros.NFSe_AMBIENTE='P', cidade.endpoint_nfs , cidade.endpoint_nfs_hom)   (https://patobranco.atende.net/ws/nfse)
nfse.nacional.cnpj-empresa= parametros.CNPJ_PAR
nfse.nacional.inscricao-municipal= parametros.NFSe_IM
nfse.nacional.usuario= parametros.NFSe_USUARIO
nfse.nacional.senha= parametros.NFSe_SENHA
nfse.nacional.codigo-municipio= parametros.CODIGO_CID
nfse.nacional.tempo-timeout=60000
nfse.nacional.ambiente=2  # 1=produção, 2=homologação
```

### Lista de Municípios Suportados
Tabela cidade (CODIGO_CID = Código IBGE, endpoint_nfs = Endpoint Produção, endpoint_nfs_hom = Endpoint Homologação)
| Município | Código IBGE | Endpoint Produção |
|-----------|-------------|-------------------|
| São Paulo | 3550308 | https://nfe.prefeitura.sp.gov.br/ws/lotenfe.asmx |
| Pato Branco | 4118200 | https://patobranco.atende.net/ws/nfse |
| Curitiba | 4314900 | https://isscuritiba.curitiba.pr.gov.br/iss/nfse |
| Porto Alegre | 4314902 | https://nfse.poa.br/ws/nfse |
| Guarulhos | 3518800 | https://nfse.guarulhos.sp.gov.br/ws/nfse |
| Londrina | 4116900 | https://nfse.londrina.pr.gov.br/ws/nfse |
| Maringá | 4115200 | https://nfse.maringa.pr.gov.br/ws/nfse |
| Campinas | 3509502 | https://nfse.campinas.sp.gov.br/ws/nfse |
| Sorocaba | 3552205 | https://nfse.sorocaba.sp.gov.br/ws/nfse |
| São Bernardo do Campo | 3548702 | https://nfse.saobernardo.sp.gov.br/ws/nfse |

## Endpoints REST

| Método | Endpoint | Descrição |
|--------|----------|------------|
| GET | /api/nfse/notas | Lista notas com paginação |
| GET | /api/nfse/notas/{id} | Busca nota por ID |
| POST | /api/nfse/notas | Cria nova nota |
| PUT | /api/nfse/notas/{id} | Atualiza nota |
| DELETE | /api/nfse/notas/{id} | Exclui nota |
| POST | /api/nfse/notas/{id}/emitir | Emite NFS-e via WebService |
| POST | /api/nfse/notas/{id}/cancelar | Cancela NFS-e |
| GET | /api/nfse/notas/{id}/consultar | Consulta status na Receita |
| POST | /api/nfse/notas/emissao-lote | Emite múltiplas notas |
| GET | /api/nfse/nfse/consultar | Consulta NFS-e por número |
| GET | /api/nfse/nfse/lote/{protocolo} | Consulta processamento de lote |

## Próximos Passos para Implementação

1. **Implementar comunicação SOAP** - Usar SAAJ, CXF ou Axis2
2. **Adicionar certificado digital** - Configurar keystore para HTTPS
3. **Implementar retry e tratamento de erros**
4. **Adicionar logs detalhados de comunicação**
5. **Criar testes unitários**
6. **Configurar Swagger/OpenAPI**
7. **Implementar autenticação JWT**

## Tecnologias Recomendadas

- **Cliente SOAP**: Apache CXF ou SAAJ (javax.xml.soap)
- **Parser XML**: JAXB ou DOM/SAX nativo
- **HTTP Client**: Apache HttpClient ou OkHttp
- **SSL/TLS**: Java KeyStore com certificado A1/A3
- **Logging**: SLF4J com Logback

---

Este é um esqueleto inicial que precisa ser complementado com a implementação real da comunicação SOAP e a conexão com o WebService de cada município.