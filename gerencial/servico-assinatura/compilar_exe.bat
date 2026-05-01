@echo off
REM ================================================================
REM  Compilar BASE64CONV.EXE
REM ================================================================
cd /d C:\Desenvolvimento\Seprocom\Gerencial\servico-assinatura
C:\mingw64\bin\gcc.exe -o base64conv.exe base64conv.c -Wall
if errorlevel 1 goto erro
dir base64conv.exe
echo.
echo SUCESSO!
goto fim
:erro
echo ERRO na compilacao!
:fim
pause
