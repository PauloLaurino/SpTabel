@echo off
REM =============================================
REM Script de inicializacao do Servico Assinatura
REM =============================================

echo ==========================================
echo   Servico de Assinatura Digital A3
echo ==========================================
echo.

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERRO: Java 17+ nao encontrado!
    echo Instale o JDK 17 ou atualize PATH
    pause
    exit /b 1
)

echo Java detectado
echo.

REM Verificar se JAR existe
if not exist "target\servico-assinatura-1.0.0.jar" (
    echo Compilando projeto...
    call mvnw.cmd clean package -DskipTests
    if errorlevel 1 (
        echo ERRO na compilacao!
        pause
        exit /b 1
    )
)

echo.
echo Iniciando servico na porta 8443...
echo Pressione Ctrl+C para parar
echo.

java -jar target\servico-assinatura-1.0.0.jar

pause
