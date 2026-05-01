@echo off
REM ================================================================
REM  Localizar e Compilar BASE64.DLL
REM ================================================================

echo ================================================================
echo   Localizando Compilador GCC
echo ================================================================
echo.

set GCC_FOUND=

REM Procurar em possiveis locais
for %%p in (
    "C:\mingw\bin\gcc.exe"
    "C:\MinGW\bin\gcc.exe"
    "C:\msys64\mingw64\bin\gcc.exe"
    "C:\Program Files\mingw64\bin\gcc.exe"
    "C:\Program Files (x86)\mingw64\bin\gcc.exe"
) do (
    if exist %%p (
        echo Encontrado: %%p
        set GCC_FOUND=%%p
        goto :compilar
    )
)

echo.
echo GCC nao encontrado!
echo.
echo Por favor, verifique onde o MinGW foi instalado.
echo Tente executar este comando no prompt:
echo.
echo    where gcc
echo.
echo Ou procure manualmente o arquivo gcc.exe
echo.

set /p GCC_PATH=Digite o caminho completo do gcc.exe: 

if exist "%GCC_PATH%" (
    set GCC_FOUND=%GCC_PATH%
    goto :compilar
) else (
    echo.
    echo Arquivo nao encontrado!
    pause
    exit /b 1
)

:compilar
echo.
echo ================================================================
echo   Compilando BASE64.DLL
echo ================================================================
echo.

cd /d "%~dp0"

"%GCC_FOUND%" -shared -o base64.dll base64.c -Wall

if %ERRORLEVEL% == 0 (
    echo.
    echo SUCESSO!
    echo.
    dir base64.dll
    echo.
) else (
    echo.
    echo ERRO na compilacao!
)

pause
