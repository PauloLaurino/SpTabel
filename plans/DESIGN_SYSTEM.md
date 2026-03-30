# Design System - Sistema Selador

## Identidade Visual Baseada no Selador

Este Design System define os padrões visuais e componentes para manter consistência com o sistema Selador existente.

---

## 1. Paleta de Cores

### Cores Principais
```css
--primary-gradient: linear-gradient(135deg, #6a11cb, #2575fc);
--primary-start: #6a11cb;
--primary-end: #2575fc;
--disabled-gradient: linear-gradient(135deg, #bdc3c7, #95a5a6);
--disabled-start: #bdc3c7;
--disabled-end: #95a5a6;
```

### Cores de Status
```css
--success: #2ecc71;      /* Verde - Online */
--info: #3498db;         /* Azul - Database */
--warning: #f1c40f;      /* Amarelo - Time */
--danger: #e74c3c;       /* Vermelho - Erro */
--purple: #9b59b6;       /* Roxo - Usuário */
```

### Cores de Background
```css
--bg-body: #f5f5f5;
--bg-card: #ffffff;
--bg-header: linear-gradient(135deg, #2c3e50, #34495e);
--bg-input: #ffffff;
--bg-table-header: #f8f9fa;
--bg-table-hover: #f8f9fa;
```

### Cores de Texto
```css
--text-primary: #333333;
--text-secondary: #666666;
--text-white: #ffffff;
--text-muted: #999999;
```

### Cores de Borda
```css
--border-light: #e0e0e0;
--border-medium: #cccccc;
--border-dark: #999999;
```

---

## 2. Tipografia

### Fontes
```css
--font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
--font-mono: 'Courier New', monospace;
```

### Tamanhos
```css
--font-size-xs: 10px;
--font-size-sm: 11px;
--font-size-base: 12px;
--font-size-md: 13px;
--font-size-lg: 14px;
--font-size-xl: 16px;
--font-size-2xl: 18px;
```

### Pesos
```css
--font-weight-normal: 400;
--font-weight-medium: 500;
--font-weight-semibold: 600;
--font-weight-bold: 700;
```

### Line Heights
```css
--line-height-tight: 1.2;
--line-height-base: 1.3;
--line-height-relaxed: 1.5;
```

---

## 3. Espaçamento

```css
--spacing-xs: 4px;
--spacing-sm: 8px;
--spacing-md: 12px;
--spacing-lg: 16px;
--spacing-xl: 20px;
--spacing-2xl: 24px;
--spacing-3xl: 32px;
```

---

## 4. Bordas e Sombras

### Bordas
```css
--border-radius-sm: 3px;
--border-radius-md: 4px;
--border-radius-lg: 6px;
--border-radius-xl: 8px;
```

### Sombras
```css
--shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.1);
--shadow-md: 0 2px 4px rgba(106, 17, 203, 0.3);
--shadow-lg: 0 4px 6px rgba(0, 0, 0, 0.15);
--shadow-focus: 0 0 0 3px rgba(106, 17, 203, 0.5);
```

---

## 5. Transições e Animações

### Durações
```css
--transition-fast: 0.15s;
--transition-base: 0.18s;
--transition-slow: 0.3s;
--transition-slower: 0.5s;
```

### Easing
```css
--ease-in-out: cubic-bezier(0.4, 0, 0.2, 1);
--ease-out: cubic-bezier(0, 0, 0.2, 1);
--ease-in: cubic-bezier(0.4, 0, 1, 1);
```

### Animações

#### Fade In
```css
@keyframes fadeIn {
    from { opacity: 0; transform: translateY(8px); }
    to { opacity: 1; transform: translateY(0); }
}
```

#### Spin
```css
@keyframes spin {
    to { transform: rotate(360deg); }
}
```

#### Dot Pulse
```css
@keyframes dot-pulse {
    0%, 80%, 100% { 
        transform: scale(0.8);
        opacity: 0.5;
    }
    40% { 
        transform: scale(1.2);
        opacity: 1;
    }
}
```

---

## 6. Componentes

### Botão Principal (btn-acao)
```css
.btn-acao {
    padding: 10px 12px;
    border: none;
    border-radius: 6px;
    font-size: 13px;
    font-weight: 700;
    color: white;
    height: 44px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    transition: all 0.18s;
    cursor: pointer;
    box-shadow: none;
}

.btn-acao.btn-roxo {
    background: linear-gradient(135deg, #6a11cb, #2575fc);
}

.btn-acao.btn-roxo:hover {
    background: linear-gradient(135deg, #5a0cb9, #1c6ea4);
    transform: translateY(-1px);
    box-shadow: 0 2px 4px rgba(106, 17, 203, 0.3);
}

.btn-acao.btn-cinza {
    background: linear-gradient(135deg, #bdc3c7, #95a5a6);
    cursor: not-allowed;
    opacity: 0.7;
    pointer-events: none;
}
```

### Header
```css
.app-header {
    background: linear-gradient(135deg, #2c3e50, #34495e);
    color: white;
    padding: 0 12px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
```

### Input
```css
.input-field {
    padding: 8px 12px;
    border: 1px solid #e0e0e0;
    border-radius: 4px;
    font-size: 12px;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    transition: all 0.2s;
}

.input-field:focus {
    outline: none;
    border-color: #6a11cb;
    box-shadow: 0 0 0 3px rgba(106, 17, 203, 0.5);
}
```

### Card
```css
.card {
    background: white;
    border-radius: 6px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    padding: 16px;
    margin-bottom: 16px;
}
```

### Tabela
```css
.table {
    width: 100%;
    border-collapse: collapse;
    background: white;
    border-radius: 6px;
    overflow: hidden;
}

.table thead {
    background: #f8f9fa;
}

.table th {
    padding: 10px 12px;
    text-align: left;
    font-weight: 600;
    font-size: 12px;
    color: #333;
    border-bottom: 2px solid #e0e0e0;
}

.table td {
    padding: 10px 12px;
    border-bottom: 1px solid #e0e0e0;
    font-size: 12px;
}

.table tr:hover {
    background: #f8f9fa;
}
```

### Badge
```css
.badge {
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 10px;
    font-weight: 600;
    display: inline-block;
}

.badge-success {
    background: rgba(46, 204, 113, 0.15);
    border: 1px solid rgba(46, 204, 113, 0.3);
    color: #2ecc71;
}

.badge-info {
    background: rgba(52, 152, 219, 0.15);
    border: 1px solid rgba(52, 152, 219, 0.3);
    color: #3498db;
}

.badge-warning {
    background: rgba(241, 196, 15, 0.15);
    border: 1px solid rgba(241, 196, 15, 0.3);
    color: #f1c40f;
}

.badge-danger {
    background: rgba(231, 76, 60, 0.15);
    border: 1px solid rgba(231, 76, 60, 0.3);
    color: #e74c3c;
}
```

### Scrollbar
```css
::-webkit-scrollbar {
    width: 3px;
}

::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 1px;
}

::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 1px;
}

::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
}
```

---

## 7. Layout

### Container
```css
.container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 16px;
}
```

### Flexbox
```css
.flex {
    display: flex;
}

.flex-col {
    flex-direction: column;
}

.items-center {
    align-items: center;
}

.justify-center {
    justify-content: center;
}

.justify-between {
    justify-content: space-between;
}

.gap-sm {
    gap: 8px;
}

.gap-md {
    gap: 12px;
}

.gap-lg {
    gap: 16px;
}
```

### Grid
```css
.grid {
    display: grid;
}

.grid-cols-2 {
    grid-template-columns: repeat(2, 1fr);
}

.grid-cols-3 {
    grid-template-columns: repeat(3, 1fr);
}

.grid-cols-4 {
    grid-template-columns: repeat(4, 1fr);
}
```

---

## 8. Responsividade

### Breakpoints
```css
--breakpoint-sm: 480px;
--breakpoint-md: 768px;
--breakpoint-lg: 1024px;
--breakpoint-xl: 1200px;
```

### Media Queries
```css
@media (max-width: 768px) {
    .app-header {
        padding: 0 8px;
        height: 35px;
    }
    
    .btn-acao {
        padding: 8px 10px;
        height: 40px;
        font-size: 12px;
    }
}

@media (max-width: 480px) {
    .system-description {
        display: none;
    }
    
    .version-container {
        display: none;
    }
}
```

---

## 9. Utilitários

### Visibilidade
```css
.hidden {
    display: none !important;
}

.visible {
    display: block !important;
}
```

### Texto
```css
.text-center {
    text-align: center;
}

.text-right {
    text-align: right;
}

.text-left {
    text-align: left;
}

.text-bold {
    font-weight: 700;
}

.text-muted {
    color: #999999;
}
```

### Margens e Paddings
```css
.mt-sm { margin-top: 8px; }
.mt-md { margin-top: 12px; }
.mt-lg { margin-top: 16px; }

.mb-sm { margin-bottom: 8px; }
.mb-md { margin-bottom: 12px; }
.mb-lg { margin-bottom: 16px; }

.p-sm { padding: 8px; }
.p-md { padding: 12px; }
.p-lg { padding: 16px; }
```

---

## 10. Componentes React (TypeScript)

### Button Component
```typescript
interface ButtonProps {
  children: React.ReactNode;
  disabled?: boolean;
  onClick?: () => void;
  className?: string;
}

export const Button: React.FC<ButtonProps> = ({
  children,
  disabled = false,
  onClick,
  className = '',
}) => {
  return (
    <button
      className={`btn-acao ${disabled ? 'btn-cinza' : 'btn-roxo'} ${className}`}
      disabled={disabled}
      onClick={onClick}
    >
      {children}
    </button>
  );
};
```

### Card Component
```typescript
interface CardProps {
  children: React.ReactNode;
  className?: string;
}

export const Card: React.FC<CardProps> = ({ children, className = '' }) => {
  return <div className={`card ${className}`}>{children}</div>;
};
```

### Input Component
```typescript
interface InputProps {
  type?: string;
  placeholder?: string;
  value?: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  disabled?: boolean;
  className?: string;
}

export const Input: React.FC<InputProps> = ({
  type = 'text',
  placeholder,
  value,
  onChange,
  disabled = false,
  className = '',
}) => {
  return (
    <input
      type={type}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
      disabled={disabled}
      className={`input-field ${className}`}
    />
  );
};
```

---

## 11. Integração com Tailwind CSS

Se usar Tailwind, configure as cores customizadas:

```javascript
// tailwind.config.js
module.exports = {
  theme: {
    extend: {
      colors: {
        primary: {
          start: '#6a11cb',
          end: '#2575fc',
        },
        success: '#2ecc71',
        info: '#3498db',
        warning: '#f1c40f',
        danger: '#e74c3c',
      },
      fontFamily: {
        sans: ['Segoe UI', 'Tahoma', 'Geneva', 'Verdana', 'sans-serif'],
      },
    },
  },
};
```

---

## 12. Exemplo de Uso

```html
<div class="container">
  <header class="app-header">
    <div class="header-left">
      <div class="logo-container">
        <i class="logo-icon fa-solid fa-stamp"></i>
        <div class="logo-text">
          <span class="system-name">SISTEMA CUSTAS</span>
          <span class="system-description">Cálculo de Emolumentos</span>
        </div>
      </div>
    </div>
  </header>

  <main class="main-content">
    <div class="card">
      <h2 class="text-bold mb-md">Cálculo de Custas</h2>
      <div class="flex gap-md mb-md">
        <input type="text" class="input-field" placeholder="Tipo de ato" />
        <button class="btn-acao btn-roxo">Calcular</button>
      </div>
    </div>
  </main>
</div>
```

---

## 13. Checklist de Implementação

- [ ] Usar cores do Design System
- [ ] Aplicar tipografia consistente
- [ ] Seguir padrões de espaçamento
- [ ] Implementar animações definidas
- [ ] Usar componentes reutilizáveis
- [ ] Garantir responsividade
- [ ] Testar acessibilidade
- [ ] Validar contraste de cores
