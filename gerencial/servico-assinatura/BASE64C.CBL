      ******************************************************************
      *  MODULO: BASE64C.CBL - Rotinas Base64 em COBOL PURO           *
      *  OBJETIVO: Conversao Base64 sem necessidade de DLL externa      *
      *  SISTEMA:  NetExpress 3.0 - ACUCOBOL                          *
      *  DATA:     09/04/2026                                         *
      ******************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID.    BASE64C.
       AUTHOR.        SEPROCOM.
       DATE-WRITTEN.  09/04/2026.

       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       SOURCE-COMPUTER. IBM-PC
           .
       OBJECT-COMPUTER. IBM-PC
           .

       DATA DIVISION.
       WORKING-STORAGE SECTION.
      ******************************************************************
      *  TABELA BASE64                                                 *
      ******************************************************************
       01  TB-BASE64-CHARS.
           05  FILLER PIC X(64) VALUE 
           'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'.

       01  TB-BASE64-TABLE REDEFINES TB-BASE64-CHARS.
           05  TB-CHAR OCCURS 64 PIC X.

       01  TB-DECODE.
           05  TB-DEC-TABLE PIC X(128).
           05  TB-DEC-TABLE-R REDEFINES TB-DEC-TABLE.
               10  TB-DEC OCCURS 128 PIC S9(4) COMP.

      ******************************************************************
      *  VARIAVEIS DE TRABALHO                                         *
      ******************************************************************
       01  WS-INDICES.
           05  WS-I             PIC 9(10).
           05  WS-J             PIC 9(10).
           05  WS-K             PIC 9(10).
           05  WS-L             PIC 9(10).
           05  WS-N             PIC 9(10).

       01  WS-BUFFER.
           05  WS-INPUT-BUFFER  PIC X(300000).
           05  WS-OUT-BUFFER    PIC X(400000).

       01  WS-OUT-LEN           PIC 9(10).

       01  WS-BYTE-VALS.
           05  WS-B0             PIC 9(3).
           05  WS-B1             PIC 9(3).
           05  WS-B2             PIC 9(3).
           05  WS-B3             PIC 9(3).
           05  WS-BYTE-INPUT    PIC X(1).
           05  WS-BYTE-VALUE    REDEFINES WS-BYTE-INPUT PIC X(1).

       01  WS-TEMP-OUT         PIC X(4).
       01  WS-PADDING          PIC 9(3).

       01  WS-INPUT-LEN        PIC 9(10).
       01  WS-REMAINDER        PIC 9(3).

       01  WS-QUADRUPLE        PIC X(4).

      ******************************************************************
      *  LINKAGE SECTION                                               *
      ******************************************************************
       LINKAGE SECTION.
       01  L-INPUT            PIC X(300000).
       01  L-INPUT-LEN        PIC 9(10).
       01  L-OUTPUT           PIC X(400000).
       01  L-OUTPUT-LEN       PIC 9(10).

      ******************************************************************
      *  PROGRAMA PRINCIPAL                                             *
      ******************************************************************
       PROCEDURE DIVISION USING L-INPUT L-INPUT-LEN L-OUTPUT L-OUTPUT-LEN.

       MAIN-PROCEDURE.

      *---- Inicializar tabela de decodificacao
           PERFORM INIT-DECODE-TABLE

      *---- Executar conversao
           PERFORM ENCODE-BASE64

      *---- Retornar resultado
           MOVE WS-OUT-LEN TO L-OUTPUT-LEN

           GOBACK
           .

      ******************************************************************
      *  INIT-DECODE-TABLE - Inicializa tabela de decodificacao        *
      ******************************************************************
       INIT-DECODE-TABLE.
           MOVE -1 TO WS-I
           PERFORM VARYING WS-I FROM 1 BY 1 UNTIL WS-I > 128
               MOVE -1 TO TB-DEC(WS-I)
           END-PERFORM

           PERFORM VARYING WS-I FROM 1 BY 1 UNTIL WS-I > 64
               MOVE WS-I TO TB-DEC(FUNCTION ORD(TB-CHAR(WS-I)) + 1)
           END-PERFORM
           .

      ******************************************************************
      *  ENCODE-BASE64 - Converte binario para Base64                   *
      ******************************************************************
       ENCODE-BASE64.

           MOVE 0 TO WS-OUT-LEN
           MOVE SPACES TO WS-OUT-BUFFER
           MOVE L-INPUT-LEN TO WS-INPUT-LEN

           IF WS-INPUT-LEN = 0 OR WS-INPUT-LEN > 300000
               GO TO ENCODE-EXIT
           END-IF

           PERFORM VARYING WS-I FROM 1 BY 3 UNTIL WS-I > WS-INPUT-LEN
               MOVE 0 TO WS-B0 WS-B1 WS-B2 WS-B3
               MOVE 0 TO WS-PADDING

      *---- Primeiro byte
               MOVE L-INPUT(WS-I:1) TO WS-BYTE-INPUT
               MOVE FUNCTION ORD(WS-BYTE-VALUE) TO WS-B0

      *---- Segundo byte (se existir)
               IF WS-I + 1 <= WS-INPUT-LEN
                   MOVE L-INPUT(WS-I + 1:1) TO WS-BYTE-INPUT
                   MOVE FUNCTION ORD(WS-BYTE-VALUE) TO WS-B1
               ELSE
                   MOVE 64 TO WS-PADDING
               END-IF

      *---- Terceiro byte (se existir)
               IF WS-I + 2 <= WS-INPUT-LEN
                   MOVE L-INPUT(WS-I + 2:1) TO WS-BYTE-INPUT
                   MOVE FUNCTION ORD(WS-BYTE-VALUE) TO WS-B2
               ELSE
                   MOVE 64 TO WS-PADDING
               END-IF

      *---- Calcular 4 caracteres Base64
               COMPUTE WS-J = (WS-B0 / 4) + 1
               MOVE TB-CHAR(WS-J) TO WS-OUT-BUFFER(WS-OUT-LEN + 1:1)

               COMPUTE WS-J = ((WS-B0 - 1) * 4) + (WS-B1 / 16) + 1
               MOVE TB-CHAR(WS-J) TO WS-OUT-BUFFER(WS-OUT-LEN + 2:1)

               IF WS-PADDING = 0
                   COMPUTE WS-J = ((WS-B1 - 1) * 16) + (WS-B2 / 4) + 1
                   MOVE TB-CHAR(WS-J) TO WS-OUT-BUFFER(WS-OUT-LEN + 3:1)
                   COMPUTE WS-J = ((WS-B2 - 1) * 64) + 1
                   MOVE TB-CHAR(WS-J) TO WS-OUT-BUFFER(WS-OUT-LEN + 4:1)
               ELSE
                   IF WS-PADDING = 64
                       MOVE '=' TO WS-OUT-BUFFER(WS-OUT-LEN + 3:1)
                   ELSE
                       COMPUTE WS-J = ((WS-B1 - 1) * 16) + (WS-B2 / 4) + 1
                       MOVE TB-CHAR(WS-J) TO WS-OUT-BUFFER(WS-OUT-LEN + 3:1)
                   END-IF
                   MOVE '=' TO WS-OUT-BUFFER(WS-OUT-LEN + 4:1)
               END-IF

               ADD 4 TO WS-OUT-LEN

           END-PERFORM

           MOVE WS-OUT-BUFFER(1:WS-OUT-LEN) TO L-OUTPUT

       ENCODE-EXIT.
           EXIT
           .

       END PROGRAM BASE64C.
