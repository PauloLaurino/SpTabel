@echo off
REM ================================================================
REM  Compilar BASE64.DLL usando MinGW
REM ================================================================

echo Compilando BASE64.DLL com MinGW...

cd /d "%~dp0"

if exist "C:\mingw\bin\gcc.exe" (
    set PATH=C:\mingw\bin;%PATH%
    gcc --version
) else if exist "C:\MinGW\bin\gcc.exe" (
    set PATH=C:\MinGW\bin;%PATH%
    gcc --version
) else (
    echo.
    echo ERRO: MinGW nao encontrado em C:\mingw ou C:\MinGW
    echo Verifique a instalacao do MinGW
    echo.
    pause
    exit /b 1
)

echo.
echo Compilando base64.c...
gcc -shared -o base64.dll base64.c -Wall

if %ERRORLEVEL% == 0 (
    echo.
    echo SUCESSO! BASE64.DLL gerado!
    echo.
    dir base64.dll
    echo.
    echo Copie BASE64.DLL para C:\Windows\System32\ ou para o PATH
) else (
    echo.
    echo ERRO na compilacao!
)

pause
