# Masfor - Formulário (refatorado)

Local de homologação do formulário `Tipo de Fornecedores` baseado na tabela `masfor`.

Arquivos gerados:

- `src/refatorado/frontend/masfor/MasforForm.tsx` - componente React (FormBuilder CODE mode)
- `src/refatorado/frontend/masfor/Masfor.css` - estilos
- `src/refatorado/frontend/masfor/types.ts` - tipos TS
- `src/main/java/br/com/spdealer/refatorado/masfor/*` - backend (entity, dto, repo, service, controller)
- `src/refatorado/sql/V20260116__create_masfor_notes.sql` - script de apoio/homologação

Fluxo recomendado (obrigatório pelo projeto):

1. Testar em ambiente de homologação com a tabela `masfor` já existente.
2. Validar endpoints: `GET /api/refatorado/masfor`, `POST /api/refatorado/masfor`, `PUT /api/refatorado/masfor/{id}`, `DELETE /api/refatorado/masfor/{id}`.
3. Ajustar `FormBuilder` (importar o TSX gerado) e testar inclusão/edição/exclusão.
4. Após homologação, gerar artefatos finais e mover para `src/components` / `src/main/java` seguindo processo de aprovação.
# Masfor (Tipo de Fornecedores) — Artefatos para homologação

Local: `src/refatorado/masfor` e `src/refatorado/frontend/masfor`

Conteúdo gerado:
- Java: `Masfor.java`, `MasforDTO.java`, `MasforRepository.java`, `MasforService.java`, `MasforController.java`
- Frontend: `types.ts`, `MasforForm.tsx`, `MasforView.tsx`, `Masfor.css`
- Dicionário: `dictionary_inserts.sql`

Passos para homologação:

1. Verificar que a tabela `masfor` já existe no banco (conforme informado).
2. Atualizar as entradas do dicionário executando `dictionary_inserts.sql` em homologação.
3. Compilar backend:

```powershell
taskkill /F /IM java.exe
mvn clean package -DskipTests
java -jar target\spdealer-1.0.0.jar --server.port=8080
```

4. Compilar frontend (se necessário):

```powershell
$env:PUBLIC_URL = '/spdealer'
$env:REACT_APP_API_URL = '/spdealer/api'
npm ci
npm run build
```

5. Testes rápidos:
- GET `/spdealer/api/refatorado/masfor` deve retornar lista (filtrada por `id_fil` da sessão).
- POST `/spdealer/api/refatorado/masfor` cria novo registro (verificar `id_fil` atribuído).

Observações:
- O controller usa `session.getAttribute("id_fil")` para filtrar/associar filial.
- Siga o fluxo FormBuilder v2 (CODE mode) para commits finais: após homologação, mova artefatos para `src/components` e `src/main/java/...` conforme política do projeto.
