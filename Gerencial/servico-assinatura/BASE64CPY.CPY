      ******************************************************************
      *  COPY: BASE64CPY.CPY - Rotinas Base64 em COBOL PURO            *
      *  Incluir no programa com: COPY "BASE64CPY.CPY"                 *
      ******************************************************************

      ******************************************************************
      *  TABELA BASE64                                                 *
      ******************************************************************
       01  TB-BASE64-CHARS.
           05  FILLER PIC X(64) VALUE 
           'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'.

       01  TB-BASE64-TABLE REDEFINES TB-BASE64-CHARS.
           05  TB-CHAR OCCURS 64 PIC X.

       01  TB-DECODE.
           05  TB-DEC-TABLE PIC X(256).
           05  TB-DEC-TABLE-R REDEFINES TB-DEC-TABLE.
               10  TB-DEC OCCURS 256 PIC S9(4) COMP.

      ******************************************************************
      *  9900-CONVERTER-BASE64 - Converte binario para Base64         *
      ******************************************************************
       9900-CONVERTER-BASE64.

           MOVE 0 TO WS-OUT-LEN
           MOVE SPACES TO WS-OUT-BUFFER
           MOVE L-INPUT-LEN TO WS-INPUT-LEN

           IF WS-INPUT-LEN = 0 OR WS-INPUT-LEN > 300000
               GO TO 9900-ENCODE-EXIT
           END-IF

      *---- Inicializar tabela de decodificacao
           PERFORM 9901-INIT-DECODE

      *---- Processar de 3 em 3 bytes
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

       9900-ENCODE-EXIT.
           EXIT
           .

      ******************************************************************
      *  9901-INIT-DECODE - Inicializa tabela de decodificacao        *
      ******************************************************************
       9901-INIT-DECODE.
           MOVE -1 TO WS-K
           PERFORM VARYING WS-K FROM 1 BY 1 UNTIL WS-K > 256
               MOVE -1 TO TB-DEC(WS-K)
           END-PERFORM

           PERFORM VARYING WS-K FROM 1 BY 1 UNTIL WS-K > 64
               MOVE WS-K TO TB-DEC(FUNCTION ORD(TB-CHAR(WS-K)) + 1)
           END-PERFORM
           .

      ******************************************************************
      *  9902-DECODE-BASE64 - Converte Base64 para binario            *
      ******************************************************************
       9902-DECODE-BASE64.

           MOVE 0 TO WS-OUT-LEN
           MOVE SPACES TO WS-OUT-BUFFER
           MOVE L-INPUT-LEN TO WS-INPUT-LEN

      *---- Inicializar tabela
           PERFORM 9901-INIT-DECODE

      *---- Remover espacos e contar padding
           MOVE SPACES TO L-OUTPUT
           MOVE 0 TO WS-N

           PERFORM VARYING WS-I FROM 1 BY 1 UNTIL WS-I > WS-INPUT-LEN
               IF L-INPUT(WS-I:1) NOT = ' ' AND L-INPUT(WS-I:1) NOT = X'0D' 
                  AND L-INPUT(WS-I:1) NOT = X'0A' AND L-INPUT(WS-I:1) NOT = X'09'
                   ADD 1 TO WS-N
                   MOVE L-INPUT(WS-I:1) TO L-OUTPUT(WS-N:1)
               END-IF
           END-PERFORM

      *---- Remover padding '=' do final
           PERFORM UNTIL WS-N = 0 OR L-OUTPUT(WS-N:1) NOT = '='
               SUBTRACT 1 FROM WS-N
           END-PERFORM

      *---- Processar de 4 em 4 caracteres
           PERFORM VARYING WS-I FROM 1 BY 4 UNTIL WS-I > WS-N
               MOVE L-OUTPUT(WS-I:4) TO WS-QUADRUPLE

               MOVE TB-DEC(FUNCTION ORD(WS-QUADRUPLE(1:1)) + 1) TO WS-B0
               MOVE TB-DEC(FUNCTION ORD(WS-QUADRUPLE(2:1)) + 1) TO WS-B1
               MOVE TB-DEC(FUNCTION ORD(WS-QUADRUPLE(3:1)) + 1) TO WS-B2
               MOVE TB-DEC(FUNCTION ORD(WS-QUADRUPLE(4:1)) + 1) TO WS-B3

               IF WS-B0 < 0 OR WS-B1 < 0
                   GO TO 9902-DECODE-EXIT
               END-IF

               COMPUTE WS-J = (WS-B0 * 4) + (WS-B1 / 16)
               MOVE FUNCTION CHAR(WS-J + 1) TO WS-OUT-BUFFER(WS-OUT-LEN + 1:1)
               ADD 1 TO WS-OUT-LEN

               IF WS-QUADRUPLE(3:1) NOT = '=' AND WS-B2 >= 0
                   COMPUTE WS-J = ((WS-B1 * 16) + (WS-B2 / 4))
                   MOVE FUNCTION CHAR(WS-J + 1) TO WS-OUT-BUFFER(WS-OUT-LEN + 1:1)
                   ADD 1 TO WS-OUT-LEN
               END-IF

               IF WS-QUADRUPLE(4:1) NOT = '=' AND WS-B3 >= 0
                   COMPUTE WS-J = ((WS-B2 * 64) + WS-B3)
                   MOVE FUNCTION CHAR(WS-J + 1) TO WS-OUT-BUFFER(WS-OUT-LEN + 1:1)
                   ADD 1 TO WS-OUT-LEN
               END-IF

           END-PERFORM

           MOVE WS-OUT-BUFFER(1:WS-OUT-LEN) TO L-OUTPUT

       9902-DECODE-EXIT.
           EXIT
           .
