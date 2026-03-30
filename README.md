# 🏷️ SISTEMA SELADOR - Documentação Completa

## 📋 VISÃO GERAL
Sistema de selagem automática de apontamentos com integração Maker 5.

## 🚀 COMEÇAR RÁPIDO

### 1. PRÉ-REQUISITOS
- Java 8
- MariaDB 10.5 (banco spprot)
- Tomcat 8/9
- Maven 3.6+

### 2. INSTALAÇÃO RÁPIDA
```bash
# 1. Clone o projeto
git clone [repositorio]

# 2. Configure o banco
cp src/main/resources/config/database.properties.example src/main/resources/config/database.properties
# Edite com suas credenciais

# 3. Compile
mvn clean package

# 4. Deploy
cp target/selador.war $TOMCAT_HOME/webapps/

# 5. Acesse
http://localhost:8080/selador/views/selador.html