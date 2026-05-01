      ******************************************************************
      *  PROGRAMA: ASST0010C - ASSINATURA DIGITAL DE DOCUMENTOS PDF    *
      *  OBJETIVO: Consumir servico REST de assinatura digital A3      *
      *  SISTEMA:  ERP COBOL (Micro Focus / ACUCOBOL)                   *
      *  DATA:     09/04/2026                                           *
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID.    ASST0010C.
       AUTHOR.        SEPROCOM.
       DATE-WRITTEN.  09/04/2026.
       DATE-COMPILED.  09/04/2026.

       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       SOURCE-COMPUTER. IBM-PC.
       OBJECT-COMPUTER. IBM-PC.

       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT ARQUIVO-PDF    ASSIGN TO PATH-PDF
                                 ORGANIZATION IS BINARY
                                 ACCESS MODE IS SEQUENTIAL
                                 FILE STATUS IS FS-PDF.

           SELECT ARQUIVO-SAIDA  ASSIGN TO PATH-SAIDA
                                 ORGANIZATION IS BINARY
                                 ACCESS MODE IS SEQUENTIAL
                                 FILE STATUS IS FS-SAIDA.

       DATA DIVISION.
       FILE SECTION.
       FD  ARQUIVO-PDF.
       01  REG-PDF                    PIC X(32767).

       FD  ARQUIVO-SAIDA.
       01  REG-SAIDA                  PIC X(32767).

       WORKING-STORAGE SECTION.
      ******************************************************************
      *  CONSTANTES                                                    *
      ******************************************************************
       01  WS-CONSTANTES.
           05  CTRF                   PIC X(01) VALUE X'0D'.
           05  URL-SERVICO            PIC X(100) VALUE 
               'http://localhost:8443/api/v1/assinar'.
           05  CONTENT-TYPE          PIC X(50) VALUE 
               'Content-Type: application/json'.
           05  ACCEPT                 PIC X(30) VALUE 
               'Accept: application/json'.

      ******************************************************************
      *  VARIAVEIS DE ARQUIVO                                          *
      ******************************************************************
       01  WS-FILE-VARS.
           05  PATH-PDF              PIC X(500).
           05  PATH-SAIDA           PIC X(500).
           05  FS-PDF                PIC X(02).
           05  FS-SAIDA             PIC X(02).

      ******************************************************************
      *  VARIAVEIS DO PROGRAMA                                         *
      ******************************************************************
       01  WS-PROGRAM-VARS.
           05  WS-PDF-SIZE           PIC 9(10) COMP.
           05  WS-BASE64-LEN         PIC 9(10) COMP.
           05  WS-QTD-LIDA           PIC 9(10) COMP.
           05  WS-BYTE-LIDO          PIC X(01).
           05  WS-EOF                PIC X(01) VALUE 'N'.
           05  WS-ERRO               PIC X(01) VALUE 'N'.
           05  WS-MSG-ERRO           PIC X(500).

      ******************************************************************
      *  VARIAVEIS WINHTTP                                             *
      ******************************************************************
       01  WS-WINHTTP.
           05  HINTERNET-SESSION     PIC X(08) COMP-5.
           05  HINTERNET-CONNECT      PIC X(08) COMP-5.
           05  HINTERNET-REQUEST      PIC X(08) COMP-5.
           05  HINTERNET-STATUS       PIC 9(08) COMP-5.
           05  WS-HTTP-VER           PIC X(20) VALUE 'HTTP/1.1'.
           05  WS-HOST               PIC X(100).
           05  WS-VERB               PIC X(10) VALUE 'POST'.
           05  WS-URI                PIC X(200).
           05  WS-REFERER            PIC X(100).
           05  WS-HEADERS            PIC X(500).
           05  WS-HEADERS-LEN        PIC 9(08) COMP-5.
           05  WS-DATA-LEN          PIC 9(10) COMP-5.
           05  WS-DATA-WRITTEN       PIC 9(10) COMP-5.
           05  WS-RESP-LEN          PIC 9(10) COMP-5.
           05  WS-RESP-STATUS        PIC 9(05).

      ******************************************************************
      *  BUFFER JSON REQUEST                                            *
      ******************************************************************
       01  WS-JSON-REQUEST.
           05  JR-DOCUMENTO          PIC X(200000).
           05  JR-CERTIFICADO        PIC X(10) VALUE 'A3'.
           05  JR-PIN                PIC X(20).
           05  JR-TIMESTAMP          PIC X(05) VALUE 'true'.
           05  JR-FILLER             PIC X(10).

       01  WS-JSON-STRING           PIC X(250000).

      ******************************************************************
      *  BUFFER JSON RESPONSE                                           *
      ******************************************************************
       01  WS-JSON-RESPONSE.
           05  JR-RESPOSTA          PIC X(100000).

      ******************************************************************
      *  AREA DE PASSAGEM DE PARAMETROS (LINKAGE)                       *
      ******************************************************************
       01  LINKAGE-PARAMS.
           05  LP-PATH-PDF-IN       PIC X(500).
           05  LP-PATH-PDF-OUT      PIC X(500).
           05  LP-PIN               PIC X(20).
           05  LP-INCLUI-TS         PIC X(01).
           05  LP-RETORNO           PIC 9(02).
           05  LP-MENSAGEM         PIC X(500).

      ******************************************************************
      *  AREA DE TRABALHO AUXILIAR                                      *
      ******************************************************************
       01  WS-AUX.
           05  WS-I                 PIC 9(10) COMP.
           05  WS-J                 PIC 9(10) COMP.
           05  WS-K                 PIC 9(10) COMP.
           05  WS-BASE64-TABLE.
               10  TB-BASE64       PIC X(64) VALUE 
               'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz
      -        '0123456789+/'.
           05  WS-PDF-BUFFER        PIC X(200000).
           05  WS-BASE64-BUFFER     PIC X(300000).
           05  WS-OUT-BUFFER        PIC X(300000).

      ******************************************************************
      *  AREA DE RETORNO DO SERVICO                                    *
      ******************************************************************
       01  WS-SERVICE-RESPONSE.
           05  SR-STATUS            PIC X(10).
           05  SR-DOC-ASSINADO      PIC X(300000).
           05  SR-CN                PIC X(200).
           05  SR-VALIDADE         PIC X(20).
           05  SR-TIMESTAMP         PIC X(50000).

       01  WS-PARSING.
           05  WP-POS               PIC 9(10) COMP.
           05  WP-LEN               PIC 9(10) COMP.
           05  WP-INI               PIC 9(10) COMP.
           05  WP-FIM               PIC 9(10) COMP.
           05  WP-VALUE             PIC X(5000).

      ******************************************************************
      *  AREA DE DEBUG                                                  *
      ******************************************************************
       01  WS-DEBUG.
           05  DB-REQUEST-SENT      PIC X(01) VALUE 'N'.
           05  DB-RESP-RECEIVED     PIC X(01) VALUE 'N'.

      ******************************************************************
      *  AREA DE TIMESTAMP                                             *
      ******************************************************************
       01  WS-TIMESTAMP-FORMAT.
           05  TS-DATE              PIC X(10).
           05  TS-FILLER1           PIC X(01) VALUE '-'.
           05  TS-TIME              PIC X(08).
           05  TS-FILLER2           PIC X(01) VALUE '.'.
           05  TS-MS                PIC X(03).

       PROCEDURE DIVISION USING LINKAGE-PARAMS.

       MAIN-PROCEDURE.

           PERFORM 1000-INICIALIZAR
           PERFORM 2000-VALIDAR-ENTRADA
           PERFORM 3000-LER-ARQUIVO-PDF
           PERFORM 4000-CONVERTER-BASE64
           PERFORM 5000-MONTAR-JSON
           PERFORM 6000-CHAMAR-SERVICO
           PERFORM 7000-PROCESSAR-RESPOSTA
           PERFORM 8000-GERAR-ARQUIVO-SAIDA
           PERFORM 9000-FINALIZAR

           GOBACK.

      ******************************************************************
      *  1000 - INICIALIZAR                                            *
      ******************************************************************
       1000-INICIALIZAR.

           MOVE SPACES TO LP-MENSAGEM
           MOVE 0    TO LP-RETORNO
           MOVE 'N'  TO WS-ERRO
           MOVE 'N'  TO DB-REQUEST-SENT
           MOVE 'N'  TO DB-RESP-RECEIVED

           DISPLAY '=========================================='
           DISPLAY '  ASSINATURA DIGITAL DE DOCUMENTOS PDF   '
           DISPLAY '=========================================='
           DISPLAY 'Data/Hora: ' FUNCTION CURRENT-DATE(1:16)
           DISPLAY 'PDF Entrada: '  FUNCTION TRIM(LP-PATH-PDF-IN)
           DISPLAY 'PDF Saida: '    FUNCTION TRIM(LP-PATH-PDF-OUT)
           DISPLAY 'PIN: ******' 
           DISPLAY 'Incluir TS: '   LP-INCLUI-TS
           DISPLAY '--------------------------------------'

           .
      ******************************************************************
      *  2000 - VALIDAR ENTRADA                                        *
      ******************************************************************
       2000-VALIDAR-ENTRADA.

      *---- Validar PDF entrada
           IF FUNCTION TRIM(LP-PATH-PDF-IN) = SPACES
               MOVE 1  TO LP-RETORNO
               MOVE 'Caminho do PDF de entrada obrigatorio' 
                 TO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *---- Validar PDF saida
           IF FUNCTION TRIM(LP-PATH-PDF-OUT) = SPACES
               MOVE 2  TO LP-RETORNO
               MOVE 'Caminho do PDF de saida obrigatorio' 
                 TO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *---- Validar PIN
           IF FUNCTION TRIM(LP-PIN) = SPACES
               MOVE 3  TO LP-RETORNO
               MOVE 'PIN do certificado obrigatorio' 
                 TO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *---- Validar Timestamp
           IF LP-INCLUI-TS = ' ' OR LP-INCLUI-TS = SPACES
               MOVE 'S' TO LP-INCLUI-TS
           END-IF

           .
      ******************************************************************
      *  3000 - LER ARQUIVO PDF                                        *
      ******************************************************************
       3000-LER-ARQUIVO-PDF.

           DISPLAY 'Lendo arquivo PDF...'

           MOVE FUNCTION TRIM(LP-PATH-PDF-IN) TO PATH-PDF

           OPEN INPUT ARQUIVO-PDF
           IF FS-PDF NOT = '00'
               MOVE 10 TO LP-RETORNO
               STRING 'Erro ao abrir PDF: ' FS-PDF 
                 DELIMITED BY SIZE INTO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *---- Calcular tamanho do arquivo
           MOVE 0 TO WS-PDF-SIZE
           MOVE 0 TO WS-QTD-LIDA

           READ ARQUIVO-PDF NEXT RECORD
           PERFORM UNTIL FS-PDF = '10'
               ADD 1 TO WS-QTD-LIDA
               READ ARQUIVO-PDF NEXT RECORD
           END-PERFORM

      *---- Ler arquivo completo para memoria
           MOVE 0 TO WS-PDF-SIZE
           MOVE 0 TO WS-QTD-LIDA
           MOVE SPACES TO WS-PDF-BUFFER

           READ ARQUIVO-PDF NEXT RECORD
           PERFORM UNTIL FS-PDF = '10'
               ADD 1 TO WS-QTD-LIDA
               READ ARQUIVO-PDF NEXT RECORD
           END-PERFORM

           CLOSE ARQUIVO-PDF

           DISPLAY 'PDF lido: ' WS-QTD-LIDA ' registros'

           .
      ******************************************************************
      *  4000 - CONVERTER PARA BASE64                                  *
      ******************************************************************
       4000-CONVERTER-BASE64.

           DISPLAY 'Convertendo para Base64...'

      *---- Copiar dados para buffer de trabalho
           MOVE WS-PDF-BUFFER(1:WS-QTD-LIDA) 
             TO WS-PDF-BUFFER(1:WS-QTD-LIDA)

      *---- Converter para Base64 usando rotina Assembly
           CALL 'BASE64ENCODE' USING WS-PDF-BUFFER
                                     WS-QTD-LIDA
                                     WS-BASE64-BUFFER
                                     WS-BASE64-LEN

           IF WS-BASE64-LEN = 0
               MOVE 11 TO LP-RETORNO
               MOVE 'Erro na conversao Base64' TO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

           DISPLAY 'Base64: ' WS-BASE64-LEN ' bytes'

           .
      ******************************************************************
      *  5000 - MONTAR JSON REQUEST                                     *
      ******************************************************************
       5000-MONTAR-JSON.

           DISPLAY 'Montando JSON...'

           MOVE SPACES TO WS-JSON-STRING

           STRING 
               '{"documento":"' 
               FUNCTION TRIM(WS-BASE64-BUFFER(1:WS-BASE64-LEN))
               '",'
               '"certificado":"A3",'
               '"pin":"' 
               FUNCTION TRIM(LP-PIN) 
               '",'
               '"timestamp":' 
               LP-INCLUI-TS 
               '}'
               DELIMITED BY SIZE
               INTO WS-JSON-STRING
           END-STRING

           MOVE LENGTH OF WS-JSON-STRING TO WS-DATA-LEN

           DISPLAY 'JSON: ' WS-DATA-LEN ' bytes'

           .
      ******************************************************************
      *  6000 - CHAMAR SERVICO REST                                    *
      ******************************************************************
       6000-CHAMAR-SERVICO.

           DISPLAY 'Chamando servico de assinatura...'
           DISPLAY 'URL: ' URL-SERVICO

      *===============================================================
      *  WINHTTP - ABRIR SESSAO
      *===============================================================
           CALL 'WinHttpOpen' 
               USING BY VALUE     0                     
                     BY REFERENCE 'ASST0010C/1.0'      
                     BY VALUE     0                     
                     BY VALUE     0                     
                     BY VALUE     0                     
               RETURNING HINTERNET-SESSION
           END-CALL

           IF HINTERNET-SESSION = 0
               MOVE 20 TO LP-RETORNO
               MOVE 'Erro WinHttpOpen' TO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *===============================================================
      *  WINHTTP - CONECTAR
      *===============================================================
           MOVE 'localhost' TO WS-HOST
           MOVE 8443      TO WS-HOST

           CALL 'WinHttpConnect' 
               USING BY VALUE     HINTERNET-SESSION       
                     BY REFERENCE WS-HOST                 
                     BY VALUE     8443                    
                     BY VALUE     0                       
               RETURNING HINTERNET-CONNECT
           END-CALL

           IF HINTERNET-CONNECT = 0
               MOVE 21 TO LP-RETORNO
               MOVE 'Erro WinHttpConnect' TO LP-MENSAGEM
               CALL 'WinHttpCloseHandle' 
                   USING BY VALUE HINTERNET-SESSION
               END-CALL
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *===============================================================
      *  WINHTTP - ABRIR REQUEST
      *===============================================================
           CALL 'WinHttpOpenRequest' 
               USING BY VALUE     HINTERNET-CONNECT       
                     BY REFERENCE 'POST'                  
                     BY REFERENCE '/api/v1/assinar'       
                     BY REFERENCE SPACES                   
                     BY REFERENCE SPACES                   
                     BY REFERENCE SPACES                   
                     BY VALUE     0                       
                     BY VALUE     0                       
               RETURNING HINTERNET-REQUEST
           END-CALL

           IF HINTERNET-REQUEST = 0
               MOVE 22 TO LP-RETORNO
               MOVE 'Erro WinHttpOpenRequest' TO LP-MENSAGEM
               CALL 'WinHttpCloseHandle' 
                   USING BY VALUE HINTERNET-CONNECT
               END-CALL
               CALL 'WinHttpCloseHandle' 
                   USING BY VALUE HINTERNET-SESSION
               END-CALL
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *===============================================================
      *  WINHTTP - ENVIAR REQUEST
      *===============================================================
           STRING 
               'Content-Type: application/json'       
               X'0D0A'                               
               'Accept: application/json'             
               X'0D0A'                               
               'X-API-Key: sua-api-key-aqui'         
               X'0D0A'                               
               DELIMITED BY SIZE                     
               INTO WS-HEADERS                       
           END-STRING

           MOVE LENGTH OF WS-HEADERS TO WS-HEADERS-LEN

           CALL 'WinHttpSendRequest' 
               USING BY VALUE     HINTERNET-REQUEST      
                     BY REFERENCE WS-HEADERS              
                     BY VALUE     WS-HEADERS-LEN          
                     BY REFERENCE WS-JSON-STRING          
                     BY VALUE     WS-DATA-LEN             
                     BY VALUE     WS-DATA-LEN             
               RETURNING HINTERNET-STATUS
           END-CALL

           IF HINTERNET-STATUS = 0
               MOVE 23 TO LP-RETORNO
               MOVE 'Erro WinHttpSendRequest' TO LP-MENSAGEM
               PERFORM 9000-FECHAR-HANDLES
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

           MOVE 'Y' TO DB-REQUEST-SENT

      *===============================================================
      *  WINHTTP - RECEBER RESPOSTA
      *===============================================================
           CALL 'WinHttpReceiveResponse' 
               USING BY VALUE     HINTERNET-REQUEST       
                     BY VALUE     0                       
               RETURNING HINTERNET-STATUS
           END-CALL

           IF HINTERNET-STATUS = 0
               MOVE 24 TO LP-RETORNO
               MOVE 'Erro WinHttpReceiveResponse' TO LP-MENSAGEM
               PERFORM 9000-FECHAR-HANDLES
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *---- Obter status HTTP
           CALL 'WinHttpQueryHeaders' 
               USING BY VALUE     HINTERNET-REQUEST       
                     BY VALUE     19                       
                     BY REFERENCE SPACES                   
                     BY REFERENCE SPACES                   
                     BY VALUE     0                       
                     BY VALUE     0                       
               RETURNING WS-RESP-STATUS
           END-CALL

           DISPLAY 'Status HTTP: ' WS-RESP-STATUS

           MOVE 'Y' TO DB-RESP-RECEIVED

      *---- Ler corpo da resposta
           MOVE SPACES TO WS-JSON-RESPONSE
           MOVE 0 TO WS-RESP-LEN

           CALL 'WinHttpReadData' 
               USING BY VALUE     HINTERNET-REQUEST       
                     BY REFERENCE WS-JSON-RESPONSE         
                     BY VALUE     LENGTH OF WS-JSON-RESPONSE
                     BY VALUE     0                       
               RETURNING WS-RESP-LEN
           END-CALL

           DISPLAY 'Resposta: ' WS-RESP-LEN ' bytes'

           PERFORM 9000-FECHAR-HANDLES

           .
      ******************************************************************
      *  7000 - PROCESSAR RESPOSTA                                     *
      ******************************************************************
       7000-PROCESSAR-RESPOSTA.

           DISPLAY 'Processando resposta...'

      *---- Verificar se houve erro na requisicao
           IF WS-RESP-STATUS >= 400
               MOVE 30 TO LP-RETORNO
               STRING 'Erro HTTP: ' WS-RESP-STATUS 
                 DELIMITED BY SIZE 
                 INTO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *---- Extrair documento assinado do JSON
           PERFORM 7100-PARSER-JSON

           IF SR-STATUS NOT = 'ok'
               MOVE 31 TO LP-RETORNO
               MOVE 'Servico retornou erro' TO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

           DISPLAY 'Documento assinado com sucesso!'
           DISPLAY 'CN Certificado: ' FUNCTION TRIM(SR-CN)

           .
      ******************************************************************
      *  7100 - PARSER JSON SIMPLES                                    *
      ******************************************************************
       7100-PARSER-JSON.

           MOVE 'ok' TO SR-STATUS

      *---- Procurar campo "documento_assinado"
           MOVE 1 TO WP-POS
           MOVE LENGTH OF WS-JSON-RESPONSE TO WP-LEN

           PERFORM VARYING WS-I FROM 1 BY 1 
                   UNTIL WS-I > WP-LEN OR 
                         WS-JSON-RESPONSE(WS-I:20) = '"documento_assi'

               CONTINUE
           END-PERFORM

           IF WS-I <= WP-LEN
               ADD 21 TO WS-I GIVING WP-INI
               
               PERFORM VARYING WS-J FROM WP-INI BY 1 
                       UNTIL WS-J > WP-LEN OR
                             WS-JSON-RESPONSE(WS-J:1) = '"'
                   CONTINUE
               END-PERFORM

               SUBTRACT 1 FROM WS-J GIVING WP-FIM
               SUBTRACT WP-INI FROM WP-FIM GIVING WS-K

               IF WS-K > 0 AND WS-K < 300000
                   MOVE WS-JSON-RESPONSE(WP-INI:WS-K) 
                     TO SR-DOC-ASSINADO(1:WS-K)
               END-IF
           END-IF

      *---- Procurar campo "cn" (nome do certificado)
           PERFORM VARYING WS-I FROM 1 BY 1 
                   UNTIL WS-I > WP-LEN OR 
                         WS-JSON-RESPONSE(WS-I:5) = '"cn":'

               CONTINUE
           END-PERFORM

           IF WS-I <= WP-LEN
               ADD 6 TO WS-I GIVING WP-INI
               
               PERFORM VARYING WS-J FROM WP-INI BY 1 
                       UNTIL WS-J > WP-LEN OR
                             WS-JSON-RESPONSE(WS-J:1) = '"'
                   CONTINUE
               END-PERFORM

               SUBTRACT 1 FROM WS-J GIVING WP-FIM
               SUBTRACT WP-INI FROM WP-FIM GIVING WS-K

               IF WS-K > 0 AND WS-K < 200
                   MOVE WS-JSON-RESPONSE(WP-INI:WS-K) 
                     TO SR-CN(1:WS-K)
               END-IF
           END-IF

           .
      ******************************************************************
      *  8000 - GERAR ARQUIVO SAIDA                                    *
      ******************************************************************
       8000-GERAR-ARQUIVO-SAIDA.

           DISPLAY 'Gerando arquivo PDF assinado...'

      *---- Converter Base64 para binario
           MOVE SPACES TO WS-OUT-BUFFER

           CALL 'BASE64DECODE' USING SR-DOC-ASSINADO
                                     FUNCTION TRIM(SR-DOC-ASSINADO)
                                     WS-OUT-BUFFER
                                     WS-QTD-LIDA

           IF WS-QTD-LIDA = 0
               MOVE 40 TO LP-RETORNO
               MOVE 'Erro ao decodificar resposta Base64' 
                 TO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *---- Salvar arquivo
           MOVE FUNCTION TRIM(LP-PATH-PDF-OUT) TO PATH-SAIDA

           OPEN OUTPUT ARQUIVO-SAIDA
           IF FS-SAIDA NOT = '00'
               MOVE 41 TO LP-RETORNO
               STRING 'Erro ao criar arquivo saida: ' FS-SAIDA 
                 DELIMITED BY SIZE INTO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *---- Dividir em registros de 32767 bytes
           PERFORM VARYING WS-I FROM 1 BY 32767 
                   UNTIL WS-I > WS-QTD-LIDA

               COMPUTE WS-J = WS-QTD-LIDA - WS-I + 1
               IF WS-J > 32767
                   MOVE 32767 TO WS-J
               END-IF

               WRITE REG-SAIDA 
                   FROM WS-OUT-BUFFER(WS-I:WS-J)
               END-WRITE

               IF FS-SAIDA NOT = '00'
                   MOVE 42 TO LP-RETORNO
                   STRING 'Erro ao gravar arquivo: ' FS-SAIDA 
                     DELIMITED BY SIZE INTO LP-MENSAGEM
                   CLOSE ARQUIVO-SAIDA
                   PERFORM 9000-FINALIZAR-ERRO
                   GOBACK
               END-IF

           END-PERFORM

           CLOSE ARQUIVO-SAIDA

           DISPLAY 'PDF assinado salvo: ' 
                   FUNCTION TRIM(LP-PATH-PDF-OUT)
           DISPLAY 'Tamanho: ' WS-QTD-LIDA ' bytes'

           MOVE 0 TO LP-RETORNO
           MOVE 'OK' TO LP-MENSAGEM

           .
      ******************************************************************
      *  9000 - ROTINAS DE FINALIZACAO                                  *
      ******************************************************************
       9000-FECHAR-HANDLES.

           IF HINTERNET-REQUEST NOT = 0
               CALL 'WinHttpCloseHandle' 
                   USING BY VALUE HINTERNET-REQUEST
               END-CALL
           END-IF

           IF HINTERNET-CONNECT NOT = 0
               CALL 'WinHttpCloseHandle' 
                   USING BY VALUE HINTERNET-CONNECT
               END-CALL
           END-IF

           IF HINTERNET-SESSION NOT = 0
               CALL 'WinHttpCloseHandle' 
                   USING BY VALUE HINTERNET-SESSION
               END-CALL
           END-IF

           .

       9000-FINALIZAR-ERRO.

           DISPLAY '=========================================='
           DISPLAY '  ERRO DURANTE PROCESSAMENTO            '
           DISPLAY '=========================================='
           DISPLAY 'Codigo: ' LP-RETORNO
           DISPLAY 'Mensagem: ' FUNCTION TRIM(LP-MENSAGEM)
           DISPLAY '--------------------------------------'

           MOVE 'Y' TO WS-ERRO

           .

       9000-FINALIZAR.

           IF FS-PDF = '00'
               CLOSE ARQUIVO-PDF
           END-IF

           IF FS-SAIDA = '00'
               CLOSE ARQUIVO-SAIDA
           END-IF

           IF DB-REQUEST-SENT = 'Y' AND DB-RESP-RECEIVED = 'N'
               PERFORM 9000-FECHAR-HANDLES
           END-IF

           IF WS-ERRO = 'N'
               DISPLAY '=========================================='
               DISPLAY '  PROCESSAMENTO CONCLUIDO COM SUCESSO  '
               DISPLAY '=========================================='
           ELSE
               DISPLAY '=========================================='
               DISPLAY '  PROCESSAMENTO FINALIZADO COM ERRO    '
               DISPLAY '=========================================='
           END-IF

           .
      ******************************************************************
      *  FIM DO PROGRAMA                                               *
      ******************************************************************
       END PROGRAM ASST0010C.
