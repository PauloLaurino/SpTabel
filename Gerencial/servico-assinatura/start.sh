#!/bin/bash
echo "=========================================="
echo "  Servico de Assinatura Digital A3"
echo "=========================================="
echo ""

if ! command -v java &> /dev/null; then
    echo "ERRO: Java 17+ nao encontrado!"
    exit 1
fi

if [ ! -f "target/servico-assinatura-1.0.0.jar" ]; then
    echo "Compilando projeto..."
    ./mvnw clean package -DskipTests
fi

echo ""
echo "Iniciando servico na porta 8443..."
echo "Pressione Ctrl+C para parar"
echo ""

java -jar target/servico-assinatura-1.0.0.jar
