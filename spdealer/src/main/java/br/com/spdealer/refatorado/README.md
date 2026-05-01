# Refatorado - Código Backend Gerado (Java)

## 📌 Propósito

Código Java gerado automaticamente pelo **FlowForm** com base em eventos e regras de negócio configuradas visualmente.

## 📁 Estrutura

```
br/com/spdealer/refatorado/
├─ controller/
│  ├─ ClienteController.java      ← Endpoints REST
│  └─ FornecedorController.java
├─ service/
│  ├─ ClienteService.java         ← Lógica de negócio
│  └─ FornecedorService.java
└─ validator/
   ├─ ClienteValidator.java       ← Validações customizadas
   └─ FornecedorValidator.java
```

## 🔄 Workflow

### 1️⃣ Configurar Eventos no FlowForm
```
Campo "email_cliente"
└─ AoSair → ValidarEmail()
   └─ Gera: ClienteValidator.validarEmail()
```

### 2️⃣ Código Gerado
```java
// refatorado/validator/ClienteValidator.java
@Component
public class ClienteValidator {
    // Gerado automaticamente
    public boolean validarEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
```

### 3️⃣ Teste e Deploy
```bash
mvn test -Dtest=ClienteValidatorTest
# Aprovado? Mover para pasta original
```

## 📋 Padrões de Código Gerado

### Controllers
```java
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {
    // CRUD básico + endpoints customizados do FlowForm
}
```

### Services
```java
@Service
public class ClienteService {
    // Lógica de negócio dos flow_steps
}
```

### Validators
```java
@Component
public class ClienteValidator {
    // Validações dos flow_events
}
```

---

**Última Atualização**: 08 JAN 2026
