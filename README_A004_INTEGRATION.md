**POC: A004 Clientes Balcão — Backend (Java) + Frontend (React/Vite)**

Instruções rápidas:

- Backend (Java/Spring Boot):
  - Build: `mvn -f backend/pom.xml clean package`
  - Run: `java -jar backend/target/a004-backend-0.1.0.jar` (ou `mvn -f backend spring-boot:run`)
  - Endpoint: `http://localhost:8080/api/forms/clientes_balcao`

- Frontend (React + Vite):
  - Entrar em `frontend`, rodar `npm install` e `npm run dev`.
  - O app consome o endpoint do backend e exibe formulários/propriedades/componentes.

Observações:
- O backend lê arquivos em `docs/a004_package` (gerados anteriormente). Se mover o diretório, ajuste o caminho em `FormController`.
- Esta é uma prova de conceito — o renderer React exibe os objetos raw; podemos mapear cada `COM_TIPO` para componentes React reais na próxima etapa.
