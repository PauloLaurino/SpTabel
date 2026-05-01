@echo off
REM ================================================================
REM  Compilar BASE64CONV.EXE
REM ================================================================

echo Compilando BASE64CONV.EXE...

cd /d "C:\Desenvolvimento\Seprocom\Gerencial\servico-assinatura"

C:\mingw64\bin\gcc.exe -o base64conv.exe base64conv.c -Wall

if %ERRORLEVEL% == 0 (
    echo.
    echo SUCESSO! base64conv.exe gerado!
    dir base64conv.exe
    echo.
    echo Agora compile o COBOL ASSTNX10.CBL no NetExpress
) else (
    echo.
    echo ERRO na compilacao!
)

pause
