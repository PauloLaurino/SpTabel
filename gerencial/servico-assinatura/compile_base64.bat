@echo off
REM ================================================================
REM  Script de Compilacao - BASE64.DLL para NetExpress
REM ================================================================
REM
REM  Este script compila a DLL de conversao Base64 necessaria
REM  para o programa COBOL ASSTNX10.CBL
REM
REM  Requisitos:
REM    - Microsoft Visual C++ Compiler (cl.exe)
REM    - Ou GCC MinGW (gcc.exe)
REM
REM  Uso:
REM    compile_base64.bat
REM
REM ================================================================

echo ================================================================
echo   Compilando BASE64.DLL
echo ================================================================
echo.

REM Verificar se o compilador esta disponivel
where cl >nul 2>&1
if %ERRORLEVEL% == 0 (
    echo Compilador MSVC encontrado...
    cl /LD base64.c /Fe:base64.dll /EHsc /MT
) else (
    where gcc >nul 2>&1
    if %ERRORLEVEL% == 0 (
        echo Compilador GCC encontrado...
        gcc -shared -o base64.dll base64.c
    ) else (
        echo.
        echo ERRO: Nenhum compilador C encontrado!
        echo.
        echo Instale um dos seguintes:
        echo   1. Microsoft Visual C++ Build Tools
        echo   2. MinGW-w64 (gcc)
        echo.
        echo Ou compile manualmente:
        echo   MSVC:  cl /LD base64.c /Fe:base64.dll
        echo   GCC:   gcc -shared -o base64.dll base64.c
        echo.
        pause
        exit /b 1
    )
)

if %ERRORLEVEL% == 0 (
    echo.
    echo ================================================================
    echo   BASE64.DLL compilado com sucesso!
    echo ================================================================
    echo.
    echo Copie BASE64.DLL para o diretorio do sistema ou PATH
    echo.
) else (
    echo.
    echo ================================================================
    echo   ERRO na compilacao!
    echo ================================================================
)

pause
