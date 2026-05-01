      ******************************************************************
      *  PROGRAMA: ASSTNX10 - ASSINATURA DIGITAL DE DOCUMENTOS PDF     *
      *  OBJETIVO: Consumir servico REST de assinatura digital A3       *
      *  SISTEMA:  ERP COBOL (NetExpress 3.0 - ACUCOBOL)              *
      *  NOTA:     COBOL PURO - usa caminhos de arquivo               *
      *            PDF fica no disco, servico Java faz a assinatura    *
      *  DATA:     09/04/2026                                         *
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID.    ASSTNX10.
       AUTHOR.        SEPROCOM.
       DATE-WRITTEN.  09/04/2026.

       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       SOURCE-COMPUTER. IBM-PC.
       OBJECT-COMPUTER. IBM-PC.

       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT ARQUIVO-PDF    ASSIGN TO DISK
                                 ORGANIZATION IS LINE SEQUENTIAL
                                 FILE STATUS IS FS-PDF.

           SELECT ARQUIVO-SAIDA  ASSIGN TO DISK
                                 ORGANIZATION IS LINE SEQUENTIAL
                                 FILE STATUS IS FS-SAIDA.

       DATA DIVISION.
       FILE SECTION.
       FD  ARQUIVO-PDF.
       01  REG-PDF                    PIC X(100).

       FD  ARQUIVO-SAIDA.
       01  REG-SAIDA                  PIC X(100).

       WORKING-STORAGE SECTION.
      ******************************************************************
      *  CONSTANTES                                                    *
      ******************************************************************
       01  WS-CONSTANTES.
           05  URL-SERVICO      PIC X(120) VALUE 
               'http://localhost:8443/api/v1/assinar'.
           05  URL-STATUS       PIC X(120) VALUE 
               'http://localhost:8443/api/v1/status'.
           05  CRLF             PIC X(02) VALUE X'0D25'.
           05  VERB-POST        PIC X(04) VALUE 'POST'.
           05  MAX-JSON         PIC 9(10) VALUE 2000.

      ******************************************************************
      *  VARIAVEIS DE ARQUIVO                                          *
      ******************************************************************
       01  WS-FILE-VARS.
           05  PATH-PDF          PIC X(500).
           05  PATH-SAIDA        PIC X(500).
           05  FS-PDF            PIC X(02).
           05  FS-SAIDA          PIC X(02).

      ******************************************************************
      *  VARIAVEIS DO PROGRAMA                                         *
      ******************************************************************
       01  WS-PROGRAM-VARS.
           05  WS-DATA-LEN       PIC S9(10) COMP.
           05  WS-ERRO            PIC X(01) VALUE 'N'.
           05  WS-API-STATUS     PIC 9(05).

      ******************************************************************
      *  VARIAVEIS WININET                                              *
      ******************************************************************
       01  WS-WININET.
           05  WI-HINTERNET      PIC S9(10) COMP.
           05  WI-HCONNECT       PIC S9(10) COMP.
           05  WI-HREQUEST       PIC S9(10) COMP.
           05  WI-AGENT         PIC X(50) VALUE 'ASSTNX10/1.0'.
           05  WI-SERVER         PIC X(100).
           05  WI-OBJECT        PIC X(200).
           05  WI-HEADERS       PIC X(500).
           05  WI-HEADERS-LEN   PIC S9(10) COMP.
           05  WI-DATA          PIC X(2000).
           05  WI-RESP-BUFFER   PIC X(2000).
           05  WI-RESP-SIZE     PIC S9(10) COMP.
           05  WI-RESP-STATUS   PIC 9(05).

      ******************************************************************
      *  AREA DE PASSAGEM DE PARAMETROS (LINKAGE)                     *
      ******************************************************************
       01  LINKAGE-PARAMS.
           05  LP-PATH-PDF-IN   PIC X(500).
           05  LP-PATH-PDF-OUT  PIC X(500).
           05  LP-PIN           PIC X(20).
           05  LP-INCLUI-TS     PIC X(01).
           05  LP-RETORNO       PIC 9(02).
           05  LP-MENSAGEM      PIC X(500).

      ******************************************************************
      *  AREA DE TRABALHO                                               *
      ******************************************************************
       01  WS-AUX.
           05  WS-I             PIC 9(10).
           05  WS-J             PIC 9(10).
           05  WS-LEN           PIC 9(10).
           05  WS-TEMP          PIC X(500).

      ******************************************************************
      *  MENSAGENS DE ERRO                                              *
      ******************************************************************
       01  MSG-ERRO-TABLE.
           05  MSG-01 PIC X(60) VALUE 
               'Caminho do PDF de entrada obrigatorio'.
           05  MSG-02 PIC X(60) VALUE 
               'Caminho do PDF de saida obrigatorio'.
           05  MSG-03 PIC X(60) VALUE 
               'PIN do certificado obrigatorio'.
           05  MSG-04 PIC X(60) VALUE 
               'PDF de entrada nao existe'.
           05  MSG-06 PIC X(60) VALUE 
               'Erro WinInet InternetOpen'.
           05  MSG-07 PIC X(60) VALUE 
               'Erro WinInet InternetConnect'.
           05  MSG-08 PIC X(60) VALUE 
               'Erro WinInet HttpOpenRequest'.
           05  MSG-09 PIC X(60) VALUE 
               'Erro WinInet HttpSendRequest'.
           05  MSG-10 PIC X(60) VALUE 
               'Erro WinInet HttpQueryStatus'.
           05  MSG-14 PIC X(60) VALUE 
               'Servico retornou erro'.
           05  MSG-20 PIC X(60) VALUE 
               'Assinatura nao encontrada na resposta'.

       PROCEDURE DIVISION USING LINKAGE-PARAMS.

       MAIN-PROCEDURE.

           PERFORM 1000-INICIALIZAR
           PERFORM 2000-VALIDAR-ENTRADA
           PERFORM 3000-VERIFICAR-ARQUIVO
           PERFORM 4000-MONTAR-JSON
           PERFORM 5000-CHAMAR-SERVICO
           PERFORM 6000-PROCESSAR-RESPOSTA
           PERFORM 7000-BAIXAR-ARQUIVO
           PERFORM 8000-FINALIZAR

           GOBACK
           .

      ******************************************************************
      *  1000 - INICIALIZAR                                             *
      ******************************************************************
       1000-INICIALIZAR.

           MOVE 0      TO LP-RETORNO
           MOVE 'N'    TO WS-ERRO

           DISPLAY '=========================================='
           DISPLAY '  ASSINATURA DIGITAL DE DOCUMENTOS PDF  '
           DISPLAY '=========================================='
           DISPLAY 'PDF Entrada: ' LP-PATH-PDF-IN
           DISPLAY 'PDF Saida: ' LP-PATH-PDF-OUT
           DISPLAY 'PIN: ******'
           DISPLAY 'Incluir TS: ' LP-INCLUI-TS
           DISPLAY '--------------------------------------'

           .

      ******************************************************************
      *  2000 - VALIDAR ENTRADA                                        *
      ******************************************************************
       2000-VALIDAR-ENTRADA.

           IF LP-PATH-PDF-IN = SPACES OR LP-PATH-PDF-IN = LOW-VALUES
               MOVE 01 TO LP-RETORNO
               MOVE MSG-01 TO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

           IF LP-PATH-PDF-OUT = SPACES OR LP-PATH-PDF-OUT = LOW-VALUES
               MOVE 02 TO LP-RETORNO
               MOVE MSG-02 TO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

           IF LP-PIN = SPACES OR LP-PIN = LOW-VALUES
               MOVE 03 TO LP-RETORNO
               MOVE MSG-03 TO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

           IF LP-INCLUI-TS = ' ' OR LP-INCLUI-TS = LOW-VALUES
               MOVE 'S' TO LP-INCLUI-TS
           END-IF

           .

      ******************************************************************
      *  3000 - VERIFICAR SE ARQUIVO EXISTE                            *
      ******************************************************************
       3000-VERIFICAR-ARQUIVO.

           DISPLAY 'Verificando arquivo PDF...'

           MOVE LP-PATH-PDF-IN TO PATH-PDF
           OPEN INPUT ARQUIVO-PDF

           IF FS-PDF NOT = '00'
               MOVE 04 TO LP-RETORNO
               MOVE MSG-04 TO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

           CLOSE ARQUIVO-PDF

           DISPLAY 'Arquivo encontrado'

           .

      ******************************************************************
      *  4000 - MONTAR JSON REQUEST                                     *
      ******************************************************************
       4000-MONTAR-JSON.

           DISPLAY 'Montando JSON...'

           MOVE SPACES TO WI-DATA

      *---- Montar JSON com caminho do arquivo
           STRING 
               '{"caminho":"' 
               DELIMITED BY SIZE
               INTO WI-DATA
           END-STRING

      *---- Adicionar caminho do PDF
           MOVE 1 TO WS-I
           MOVE LP-PATH-PDF-IN TO WS-TEMP
           PERFORM 4500-TRIM

           STRING 
               WI-DATA(1:14)
               WS-TEMP(1:WS-LEN)
               '","saida":"'
               DELIMITED BY SIZE
               INTO WI-DATA
           END-STRING

      *---- Adicionar caminho de saida
           MOVE LP-PATH-PDF-OUT TO WS-TEMP
           PERFORM 4500-TRIM

           STRING 
               WI-DATA(1:50)
               WS-TEMP(1:WS-LEN)
               '","pin":"'
               DELIMITED BY SIZE
               INTO WI-DATA
           END-STRING

      *---- Adicionar PIN e timestamp
           STRING 
               WI-DATA(1:80)
               LP-PIN
               '","timestamp":"'
               LP-INCLUI-TS
               '"}'
               DELIMITED BY SIZE
               INTO WI-DATA
           END-STRING

      *---- Calcular tamanho
           MOVE 0 TO WS-LEN
           PERFORM 4600-CALC-LEN

           MOVE WS-LEN TO WS-DATA-LEN

           DISPLAY 'JSON: ' WS-DATA-LEN ' bytes'

           .

      ******************************************************************
      *  4500-TRIM - Remove espacos a direita e conta tamanho         *
      ******************************************************************
       4500-TRIM.
           MOVE 0 TO WS-LEN
           MOVE LENGTH OF WS-TEMP TO WS-I
           PERFORM UNTIL WS-I = 0
               IF WS-TEMP(WS-I:1) NOT = SPACES AND WS-TEMP(WS-I:1) NOT = LOW-VALUES
                   MOVE WS-I TO WS-LEN
                   EXIT PERFORM
               END-IF
               SUBTRACT 1 FROM WS-I
           END-PERFORM
           .

      ******************************************************************
      *  4600-CALC-LEN - Calcula tamanho real da string               *
      ******************************************************************
       4600-CALC-LEN.
           MOVE 0 TO WS-LEN
           MOVE LENGTH OF WI-DATA TO WS-I
           PERFORM UNTIL WS-I = 0
               IF WI-DATA(WS-I:1) NOT = SPACES AND WI-DATA(WS-I:1) NOT = LOW-VALUES
                   MOVE WS-I TO WS-LEN
                   EXIT PERFORM
               END-IF
               SUBTRACT 1 FROM WS-I
           END-PERFORM
           .

      ******************************************************************
      *  5000 - CHAMAR SERVICO REST (WinInet)                         *
      ******************************************************************
       5000-CHAMAR-SERVICO.

           DISPLAY 'Chamando servico de assinatura...'
           DISPLAY 'URL: ' URL-SERVICO

      *---- InternetOpen
           CALL 'INTERNETOPEN' 
               USING BY REFERENCE WI-AGENT
                     BY VALUE     0
                     BY VALUE     0
                     BY VALUE     0
                     BY VALUE     0
               RETURNING WI-HINTERNET
           END-CALL

           IF WI-HINTERNET = 0 OR WI-HINTERNET < 0
               MOVE 06 TO LP-RETORNO
               MOVE MSG-06 TO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *---- InternetConnect
           MOVE 'localhost' TO WI-SERVER

           CALL 'INTERNETCONNECT' 
               USING BY VALUE     WI-HINTERNET
                     BY REFERENCE WI-SERVER
                     BY VALUE     8443
                     BY VALUE     0
                     BY VALUE     0
                     BY VALUE     'HTTP'
                     BY VALUE     0
               RETURNING WI-HCONNECT
           END-CALL

           IF WI-HCONNECT = 0 OR WI-HCONNECT < 0
               MOVE 07 TO LP-RETORNO
               MOVE MSG-07 TO LP-MENSAGEM
               CALL 'INTERNETCLOSEHANDLE' USING BY VALUE WI-HINTERNET
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *---- HttpOpenRequest
           MOVE '/api/v1/assinar-caminho' TO WI-OBJECT

           CALL 'HTTPOPENREQUEST' 
               USING BY VALUE     WI-HCONNECT
                     BY REFERENCE VERB-POST
                     BY REFERENCE WI-OBJECT
                     BY VALUE     0
                     BY VALUE     0
                     BY VALUE     0
                     BY VALUE     0
                     BY VALUE     0
               RETURNING WI-HREQUEST
           END-CALL

           IF WI-HREQUEST = 0 OR WI-HREQUEST < 0
               MOVE 08 TO LP-RETORNO
               MOVE MSG-08 TO LP-MENSAGEM
               CALL 'INTERNETCLOSEHANDLE' USING BY VALUE WI-HCONNECT
               CALL 'INTERNETCLOSEHANDLE' USING BY VALUE WI-HINTERNET
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *---- HttpSendRequest
           MOVE SPACES TO WI-HEADERS
           STRING 
               'Content-Type: application/json' CRLF
               'Accept: application/json' CRLF
               'X-API-Key: sua-api-key-aqui' CRLF
               DELIMITED BY SIZE
               INTO WI-HEADERS
           END-STRING

           MOVE LENGTH OF WI-HEADERS TO WI-HEADERS-LEN

           CALL 'HTTPSENDREQUEST' 
               USING BY REFERENCE WI-HEADERS
                     BY VALUE     WI-HEADERS-LEN
                     BY REFERENCE WI-DATA
                     BY VALUE     WS-DATA-LEN
               RETURNING WS-API-STATUS
           END-CALL

           IF WS-API-STATUS = 0
               MOVE 09 TO LP-RETORNO
               MOVE MSG-09 TO LP-MENSAGEM
               PERFORM 9100-FECHAR-HANDLES
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *---- HttpQueryStatus
           CALL 'HTTPQUERYSTATUS' 
               USING BY VALUE     WI-HREQUEST
                     BY VALUE     0
               RETURNING WI-RESP-STATUS
           END-CALL

           DISPLAY 'Status HTTP: ' WI-RESP-STATUS

      *---- InternetReadFile
           MOVE 0 TO WI-RESP-SIZE
           MOVE SPACES TO WI-RESP-BUFFER

           CALL 'INTERNETREADFILE' 
               USING BY VALUE     WI-HREQUEST
                     BY REFERENCE WI-RESP-BUFFER
                     BY REFERENCE WI-RESP-SIZE
           END-CALL

           DISPLAY 'Resposta: ' WI-RESP-SIZE ' bytes'

           PERFORM 9100-FECHAR-HANDLES

           .

      ******************************************************************
      *  6000 - PROCESSAR RESPOSTA                                     *
      ******************************************************************
       6000-PROCESSAR-RESPOSTA.

           DISPLAY 'Processando resposta...'

           IF WI-RESP-STATUS >= 400
               MOVE 14 TO LP-RETORNO
               MOVE MSG-14 TO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

      *---- Verificar se resposta contem "sucesso"
           MOVE 1 TO WS-I
           MOVE 0 TO WS-LEN
           PERFORM UNTIL WS-I > WI-RESP-SIZE OR WS-I > 2000
               IF WI-RESP-BUFFER(WS-I:7) = '"status"'
                   MOVE WS-I TO WS-J
                   ADD 10 TO WS-J
                   PERFORM UNTIL WS-J > WI-RESP-SIZE
                       OR WI-RESP-BUFFER(WS-J:1) = '"'
                       ADD 1 TO WS-J
                   END-PERFORM
                   SUBTRACT WS-I FROM WS-J GIVING WS-LEN
                   SUBTRACT 11 FROM WS-LEN
                   EXIT PERFORM
               END-IF
               ADD 1 TO WS-I
           END-PERFORM

           IF WS-LEN > 0
               DISPLAY 'Status: ' WI-RESP-BUFFER(WS-J:WS-LEN)
           END-IF

      *---- Verificar se arquivo de saida foi criado
           MOVE LP-PATH-PDF-OUT TO PATH-SAIDA
           OPEN INPUT ARQUIVO-SAIDA

           IF FS-SAIDA = '00'
               CLOSE ARQUIVO-SAIDA
               DISPLAY 'Documento assinado com sucesso!'
               MOVE 0 TO LP-RETORNO
               MOVE 'OK' TO LP-MENSAGEM
           ELSE
               MOVE 20 TO LP-RETORNO
               MOVE MSG-20 TO LP-MENSAGEM
               PERFORM 9000-FINALIZAR-ERRO
               GOBACK
           END-IF

           .

      ******************************************************************
      *  7000 - BAIXAR ARQUIVO (se servico salvar em disco)          *
      ******************************************************************
       7000-BAIXAR-ARQUIVO.

      *---- O servico Java salva o arquivo em LP-PATH-PDF-OUT
      *---- Se precisar baixar via HTTP, implementar aqui

           DISPLAY 'Arquivo salvo em: ' LP-PATH-PDF-OUT

           .

      ******************************************************************
      *  8000 - FINALIZAR                                               *
      ******************************************************************
       8000-FINALIZAR.

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
      *  9000 - ROTINAS DE FINALIZACAO                                 *
      ******************************************************************
       9000-FINALIZAR-ERRO.

           DISPLAY '=========================================='
           DISPLAY '  ERRO DURANTE PROCESSAMENTO            '
           DISPLAY '=========================================='
           DISPLAY 'Codigo: ' LP-RETORNO
           DISPLAY 'Mensagem: ' LP-MENSAGEM

           MOVE 'Y' TO WS-ERRO

           .

      9100-FECHAR-HANDLES.

           IF WI-HREQUEST > 0
               CALL 'INTERNETCLOSEHANDLE' 
                   USING BY VALUE WI-HREQUEST
               END-CALL
           END-IF

           IF WI-HCONNECT > 0
               CALL 'INTERNETCLOSEHANDLE' 
                   USING BY VALUE WI-HCONNECT
               END-CALL
           END-IF

           IF WI-HINTERNET > 0
               CALL 'INTERNETCLOSEHANDLE' 
                   USING BY VALUE WI-HINTERNET
               END-CALL
           END-IF

           .

       END PROGRAM ASSTNX10.
