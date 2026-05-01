# 💾 Pasta Refatorado - Código Gerado Aguardando Homologação

> **Atualizado**: 11 de Janeiro de 2026  
> **FormBuilder v2.0** - Geração de Código 5-Tabs + Workflow de Homologação Completo

---

## 📌 Propósito

Esta pasta contém código **GERADO AUTOMATICAMENTE** pelo **FormBuilder v2.0**, aguardando **homologação e testes** antes de ser movido para produção.

### ✨ Novidades (11/01/2026)

- ✅ **5 arquivos gerados simultaneamente** (TSX, CSS, JAVA, SQL, TYPES)
- ✅ **3 modos de trabalho** (DESIGN, PREVIEW, CODE)
- ✅ **Botão "💾 Salvar em Refatorado"** integrado no FormBuilder
- ✅ **Estrutura de pastas organizada** (components, types, backend, migrations)
- ✅ **Workflow completo de homologação**

---

## 🚫 REGRAS CRÍTICAS

### ❌ NUNCA Fazer

- ❌ Editar arquivos aqui manualmente (use FormBuilder)
- ❌ Commitar pasta `refatorado/` no Git (adicionar ao .gitignore)
- ❌ Mover para produção sem testar completamente
- ❌ Deletar arquivos sem backup

### ✅ SEMPRE Fazer

- ✅ Usar FormBuilder Editor para modificações
- ✅ Testar código gerado antes de aprovar
- ✅ Executar checklist de aprovação
- ✅ Criar backup do arquivo original antes de substituir
- ✅ Limpar `refatorado/` após mover para produção

---

## 📁 Estrutura Completa (5 Categorias)

```
src/refatorado/
├── components/
│   └── Forms/
│       ├── DepartamentosListForm.tsx    (TSX - Frontend React)
│       ├── ClientesListForm.tsx
│       └── FornecedoresListForm.tsx
│
├── styles/
│   ├── Departamentos.css                (CSS - Estilos completos)
│   ├── Clientes.css
│   └── Fornecedores.css
│
├── types/
│   ├── Departamentos.types.ts           (TypeScript Interfaces)
│   ├── Clientes.types.ts
│   └── Fornecedores.types.ts
│
├── backend/
│   ├── DepartamentosController.java     (JAVA - REST Controller)
│   ├── DepartamentosService.java        (JAVA - Business Logic)
│   ├── ClientesController.java
│   └── FornecedoresController.java
│
├── migrations/
│   ├── masdep_migration.sql             (SQL - CREATE TABLE + Dictionary)
│   ├── clientes_migration.sql
│   └── fornecedores_migration.sql
│
└── README.md  (Este arquivo)
```

---

## 🔄 Workflow de Homologação (5 Etapas)

### Pipeline Completo

```
1. GERAR       2. SALVAR       3. TESTAR      4. APROVAR   5. DEPLOY
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐ ┌──────────┐
│FormBuild │ → │Refato-   │ → │Testar    │ → │Aprovar   │→│Mover     │
│Gera 5    │   │rado/     │   │Código    │   │Código    │ │p/ Prod   │
│Arquivos  │   │5 Files   │   │Gerado    │   │Funcional │ │Location  │
└──────────┘   └──────────┘   └──────────┘   └──────────┘ └──────────┘
    ⚙️            💾             🧪             ✅            📦
```

---

### ETAPA 1: Gerar Código no FormBuilder

**Caminho**: `http://localhost:3000/ferramentas/form-builder-editor`

**Passos**:
1. Abrir FormBuilder v2.0
2. Selecionar tabela do dictionary (ex: `masdep`)
3. Importar campos automaticamente (☑️ marcar campos desejados)
4. **Modo DESIGN**: Posicionar campos no canvas (drag-and-drop)
5. **Modo PREVIEW**: Validar layout final (sem edição)
6. **Modo CODE**: Clicar **"⚙️ Gerar Código"**
7. Verificar 5 tabs de código gerado:
   - 📄 **TSX**: React Component com AG-Grid + Modal + CRUD
   - 🎨 **CSS**: Estilos completos (SearchBar, Buttons, Modal, Responsive)
   - ☕ **JAVA**: Controller + Service + Repository
   - 🗄️ **SQL**: CREATE TABLE + Dictionary Inserts
   - 📐 **TYPES**: TypeScript Interfaces

**Resultado**: 5 arquivos prontos para salvar

---

### ETAPA 2: Salvar em Refatorado

**Ação**: Clicar botão **"💾 Salvar em Refatorado"** no FormBuilder

**O Que Acontece**:
```typescript
// Frontend envia POST /api/formbuilder/save-generated-code
{
  tableName: "masdep",
  componentName: "Departamentos",
  files: {
    tsx: "código TSX gerado...",
    css: "código CSS gerado...",
    java: "código Java gerado...",
    sql: "código SQL gerado...",
    types: "código TypeScript gerado..."
  }
}

// Backend salva em:
src/refatorado/components/Forms/DepartamentosListForm.tsx
src/refatorado/styles/Departamentos.css
src/refatorado/types/Departamentos.types.ts
src/refatorado/backend/DepartamentosController.java
src/refatorado/migrations/masdep_migration.sql
```

**Mensagem de Sucesso**:
```
✅ 5 arquivos salvos com sucesso em src/refatorado/!
📁 DepartamentosListForm.tsx
📁 Departamentos.css
📁 Departamentos.types.ts
📁 DepartamentosController.java
📁 masdep_migration.sql
```

---

### ETAPA 3: Testar Código Gerado

#### 3.1 - Executar SQL Migration

```powershell
# Executar script SQL para criar tabela + dictionary
Get-Content "src\refatorado\migrations\masdep_migration.sql" | `
  mysql -h 100.126.166.63 -u root -pk15720 erp

# Verificar tabela criada
mysql -h 100.126.166.63 -u root -pk15720 erp -e "DESC masdep;"
```

#### 3.2 - Copiar Backend Java para Projeto

```powershell
# Copiar Controller e Service para src/main/java
cp src/refatorado/backend/DepartamentosController.java `
   src/main/java/br/com/spdealer/controller/

cp src/refatorado/backend/DepartamentosService.java `
   src/main/java/br/com/spdealer/service/

# Compilar backend
mvn clean package -DskipTests
```

#### 3.3 - Testar Frontend

```powershell
# Importar componente em página de teste
# Editar: src/pages/TesteForms.tsx
```

```typescript
import { DepartamentosListForm } from '../refatorado/components/Forms/DepartamentosListForm';
import '../refatorado/styles/Departamentos.css';

export const TesteForms = () => {
  return (
    <div className="page-container">
      <h1>🧪 Teste: Departamentos (Gerado por FormBuilder)</h1>
      <DepartamentosListForm />
    </div>
  );
};
```

```powershell
# Iniciar aplicação
npm start

# Acessar: http://localhost:3000/teste-forms
```

#### 3.4 - Validar Funcionalidades

**Testes Manuais**:
- [ ] SearchBar renderiza corretamente
- [ ] Botão "➕ Incluir" abre modal
- [ ] AG-Grid carrega dados do backend
- [ ] Colunas do grid estão corretas (dictionary)
- [ ] Botão "✏️ Editar" (penúltima coluna) abre modal com dados
- [ ] Botão "🗑️ Excluir" (última coluna) deleta registro
- [ ] Modal fecha com "❌ Cancelar" ou ESC
- [ ] Modal salva com "✔️ Salvar" ou CTRL+G
- [ ] Duplo clique no grid abre edição
- [ ] Validações funcionam (campos obrigatórios, máscaras)
- [ ] Responsive funciona (< 768px mobile)

**Testes Backend**:
```powershell
# Testar endpoints REST
Invoke-WebRequest -Uri "http://localhost:8080/api/departamentos" -Method Get

# Criar novo departamento
$body = @{ codigo_dep = "001"; descr_dep = "TI" } | ConvertTo-Json
Invoke-WebRequest -Uri "http://localhost:8080/api/departamentos" `
  -Method Post -Body $body -ContentType "application/json"
```

---

### ETAPA 4: Checklist de Aprovação

**Preencher TODOS os itens antes de aprovar:**

#### Frontend (React + TypeScript)
- [ ] ✅ Código compila sem erros TypeScript
- [ ] ✅ AG-Grid renderiza com todos os campos
- [ ] ✅ Modal abre/fecha corretamente
- [ ] ✅ Botões "Incluir", "Editar", "Excluir" funcionam
- [ ] ✅ CRUD completo (Create, Read, Update, Delete)
- [ ] ✅ Validações funcionam (required, masks, patterns)
- [ ] ✅ Keyboard shortcuts funcionam (CTRL+G, CTRL+X, ESC)
- [ ] ✅ Duplo clique no grid abre edição
- [ ] ✅ Responsive funciona em 3 breakpoints (320px, 768px, 1920px)
- [ ] ✅ Console sem erros ou warnings

#### Backend (Spring Boot + Java)
- [ ] ✅ Endpoints REST respondem 200 OK
- [ ] ✅ GET /api/{resource} retorna lista
- [ ] ✅ GET /api/{resource}/{id} retorna item
- [ ] ✅ POST /api/{resource} cria novo registro
- [ ] ✅ PUT /api/{resource}/{id} atualiza registro
- [ ] ✅ DELETE /api/{resource}/{id} remove registro
- [ ] ✅ Validações backend funcionam (400 Bad Request em dados inválidos)
- [ ] ✅ Tratamento de erros funciona (500 Internal Server Error)

#### Database (MariaDB)
- [ ] ✅ SQL migration executou sem erros
- [ ] ✅ Tabela criada com estrutura correta
- [ ] ✅ dictionary_tables possui entry para tabela
- [ ] ✅ dictionary_columns possui entry para cada campo
- [ ] ✅ Primary Key e Foreign Keys corretas
- [ ] ✅ Índices criados (se necessário)

#### Qualidade de Código
- [ ] ✅ Código gerado segue padrão SPDealer
- [ ] ✅ Sem código duplicado ou desnecessário
- [ ] ✅ Comentários úteis (não excessivos)
- [ ] ✅ Nomes de variáveis/funções semânticos
- [ ] ✅ Indentação correta (2 espaços TypeScript, 4 espaços Java)

---

### ETAPA 5: Deploy para Produção

**Quando Aprovar**: TODOS os itens do checklist marcados ✅

#### 5.1 - Backup do Código Original (se existir)

```powershell
# Se arquivo original já existe, fazer backup
$originalFile = "src/pages/Departamentos/DepartamentosListForm.tsx"
if (Test-Path $originalFile) {
  $backupFile = "${originalFile}.bak.$(Get-Date -Format 'yyyyMMdd-HHmmss')"
  Copy-Item $originalFile $backupFile
  Write-Host "✅ Backup criado: $backupFile"
}
```

#### 5.2 - Mover Arquivos para Produção

```powershell
# Criar pastas se não existem
New-Item -ItemType Directory -Force -Path "src/pages/Departamentos"
New-Item -ItemType Directory -Force -Path "src/types"
New-Item -ItemType Directory -Force -Path "src/main/java/br/com/spdealer/controller"
New-Item -ItemType Directory -Force -Path "src/main/java/br/com/spdealer/service"

# Mover Frontend
Move-Item -Force `
  "src/refatorado/components/Forms/DepartamentosListForm.tsx" `
  "src/pages/Departamentos/"

Move-Item -Force `
  "src/refatorado/styles/Departamentos.css" `
  "src/pages/Departamentos/"

# Mover Types
Move-Item -Force `
  "src/refatorado/types/Departamentos.types.ts" `
  "src/types/"

# Mover Backend Java
Move-Item -Force `
  "src/refatorado/backend/DepartamentosController.java" `
  "src/main/java/br/com/spdealer/controller/"

Move-Item -Force `
  "src/refatorado/backend/DepartamentosService.java" `
  "src/main/java/br/com/spdealer/service/"

# SQL migration já foi executado (ETAPA 3.1)
# Pode arquivar ou deletar
Move-Item -Force `
  "src/refatorado/migrations/masdep_migration.sql" `
  "database/migrations/executed/$(Get-Date -Format 'yyyyMMdd')_masdep.sql"
```

#### 5.3 - Recompilar Aplicação

```powershell
# Recompilar frontend
npm run build

# Recompilar backend
mvn clean package -DskipTests

# Reiniciar aplicação
.\start_system.ps1 DEV
```

#### 5.4 - Validar em Produção

```powershell
# Testar URL final
Start-Process "http://localhost:3000/departamentos"

# Verificar logs
Get-Content logs/npm-dev-*.log -Tail 20
Get-Content logs/maven-dev-*.log -Tail 20
```

#### 5.5 - Git Commit

```powershell
# Stage arquivos
git add src/pages/Departamentos/
git add src/types/Departamentos.types.ts
git add src/main/java/br/com/spdealer/controller/DepartamentosController.java
git add src/main/java/br/com/spdealer/service/DepartamentosService.java

# Commit estruturado
git commit -m "feat: Formulario Departamentos via FormBuilder v2.0

- Frontend: DepartamentosListForm.tsx (TSX + CSS)
- Backend: DepartamentosController.java + Service
- Database: masdep table + dictionary entries
- Types: Departamentos.types.ts

Gerado automaticamente via FormBuilder v2.0 (11/01/2026)
Checklist de aprovacao: 100% OK
Testes: Frontend + Backend + Database validados"

# Push
git push origin main
```

#### 5.6 - Limpar `src/refatorado/`

```powershell
# Deletar arquivos já movidos
Remove-Item -Recurse -Force "src/refatorado/components/Forms/Departamentos*"
Remove-Item -Recurse -Force "src/refatorado/styles/Departamentos*"
Remove-Item -Recurse -Force "src/refatorado/types/Departamentos*"
Remove-Item -Recurse -Force "src/refatorado/backend/Departamentos*"

# Verificar pasta vazia
Get-ChildItem -Recurse "src/refatorado/"
```

---

## 📊 Conteúdo dos 5 Arquivos Gerados

### 📄 1. TSX (React Frontend)

**Arquivo**: `{Name}ListForm.tsx` (~500-800 linhas)

**Componentes Incluídos**:
- ✅ SearchBar com input de busca + botão "➕ Incluir"
- ✅ AG-Grid React com colunas do dictionary
- ✅ Botões "✏️ Editar" (penúltima coluna, pinned right)
- ✅ Botão "🗑️ Excluir" (última coluna, pinned right)
- ✅ Modal com formulário completo (campos dinâmicos)
- ✅ Handlers CRUD completos (useState, useEffect, fetch)
- ✅ Keyboard shortcuts (CTRL+G salvar, CTRL+X/ESC cancelar)
- ✅ Duplo clique no grid abre edição
- ✅ Validações inline (required, patterns)

**Tecnologias**: React 18, TypeScript, AG-Grid React, Bootstrap 5

---

### 🎨 2. CSS (Estilos Completos)

**Arquivo**: `{Name}.css` (~200-300 linhas)

**Estilos Incluídos**:
- ✅ Container principal (.departamentos-container)
- ✅ SearchBar (.search-bar, input, botão)
- ✅ Botão Incluir (verde #28a745, hover)
- ✅ AG-Grid customizado (.ag-theme-alpine)
- ✅ Botões Editar (azul #007bff) e Excluir (vermelho #dc3545)
- ✅ Modal overlay (fundo escurecido rgba(0,0,0,0.5))
- ✅ Modal form (campos, labels, botões)
- ✅ Responsive design (< 768px mobile, flex-direction: column)

**Browser Support**: Chrome, Firefox, Safari, Edge

---

### ☕ 3. JAVA (Backend Spring Boot)

**Arquivo**: `{Name}Controller.java` (~150-250 linhas)

**Endpoints REST**:
- ✅ `GET /api/{resource}` - Listar todos
- ✅ `GET /api/{resource}/{id}` - Buscar por ID
- ✅ `POST /api/{resource}` - Criar novo
- ✅ `PUT /api/{resource}/{id}` - Atualizar
- ✅ `DELETE /api/{resource}/{id}` - Remover

**Tecnologias**: Spring Boot 3.1.4, JPA, Hibernate, MariaDB

**Service** (`{Name}Service.java`):
- ✅ Lógica de negócio (validações, regras)
- ✅ Integração com Repository (JPA)

---

### 🗄️ 4. SQL (Database Schema)

**Arquivo**: `{table}_migration.sql` (~50-150 linhas)

**SQL Gerado**:
- ✅ `CREATE TABLE {table}` com todos os campos (tipos corretos)
- ✅ Primary Key (`id INT UNSIGNED AUTO_INCREMENT`)
- ✅ Foreign Keys (se houver relações)
- ✅ `INSERT INTO dictionary_tables` (1 entry para tabela)
- ✅ `INSERT INTO dictionary_columns` (1 entry por campo)
- ✅ Índices para otimização (campos de busca frequentes)

**Database**: MariaDB 10.x

---

### 📐 5. TYPES (TypeScript Interfaces)

**Arquivo**: `{Name}.types.ts` (~50-100 linhas)

**Interfaces Geradas**:
- ✅ `{Name}Data` - Estrutura de dados principal (match com SQL)
- ✅ `{Name}FormProps` - Props do componente de formulário
- ✅ `{Name}ListProps` - Props do componente de listagem
- ✅ `OperationStatus` - Estados de operação (loading, success, error)
- ✅ `FormMode` - Modo do formulário ('create' | 'edit' | 'view')

**Exemplo**:
```typescript
export interface DepartamentosData {
  id?: number;
  codigo_dep: string;
  descr_dep: string;
  created_at?: Date;
  updated_at?: Date;
}

export interface DepartamentosFormProps {
  mode: FormMode;
  data?: DepartamentosData;
  onSave: (data: DepartamentosData) => void;
  onCancel: () => void;
}
```

---

## 🎯 3 Modos de Trabalho do FormBuilder

### 🎨 Modo DESIGN (Edição Visual)

**Funcionalidade**: Criar/editar formulários com drag-and-drop

**Recursos**:
- Canvas com position:absolute
- Arrastar campos (drag-and-drop)
- Redimensionar campos (resize handles)
- Menu de contexto (botão direito)
- Editar propriedades
- Criar abas/tabs

---

### 👁️ Modo PREVIEW (Visualização)

**Funcionalidade**: Visualizar resultado final sem edição

**Recursos**:
- Renderização pixel-perfect (igual ao DESIGN)
- Position:absolute com coordenadas exatas
- Abas funcionais (clicáveis)
- Visual final (sem borders de edição)
- **SEM** drag, resize ou menu de contexto

---

### ⚙️ Modo CODE (Geração de Código)

**Funcionalidade**: Gerar 5 arquivos de código pronto

**Recursos**:
- 5 tabs de código (TSX, CSS, JAVA, SQL, TYPES)
- Botão "📋 Copiar {TAB}" (copia tab ativa)
- Botão "💾 Salvar em Refatorado" (salva 5 arquivos)
- Código segue padrão SPDealer

---

## ⚠️ Observações Importantes

### ✅ Boas Práticas

1. **Sempre testar** código gerado antes de aprovar
2. **Backup** de arquivos originais antes de substituir
3. **Limpar** `src/refatorado/` após deploy
4. **Git commit** estruturado com mensagem detalhada
5. **Validar** funcionalidades (frontend + backend + database)

### ⚠️ Atenção

- **Arquivos são TEMPORÁRIOS** - não deixar acumular em `refatorado/`
- **NUNCA commitar** `src/refatorado/` no Git (adicionar ao .gitignore)
- **Não editar manualmente** - use FormBuilder para modificações
- **Testar em ambiente de desenvolvimento** antes de produção

### 🔗 Documentação Relacionada

- 📄 [FORMBUILD_GUIA_VISUAL.md](../../docs/FORMBUILD_GUIA_VISUAL.md) - Guia visual completo
- 📄 [FORMBUILD_ATUALIZACAO_11JAN2026.md](../../docs/FORMBUILD_ATUALIZACAO_11JAN2026.md) - Detalhes das atualizações
- 📄 [00_RESUMO_SIDEBAR_COLLAPSE_11JAN2026.md](../../00_RESUMO_SIDEBAR_COLLAPSE_11JAN2026.md) - Melhorias de interface
- 📄 [PADRAO_ESQUELETO_FORMULARIO.md](../../docs/PADRAO_ESQUELETO_FORMULARIO.md) - Padrão de código gerado

---

**Última Atualização**: 11 de Janeiro de 2026  
**FormBuilder**: v2.0 - Geração de Código 5-Tabs  
**Workflow**: Homologação Completo Implementado
