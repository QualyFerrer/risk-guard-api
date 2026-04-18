<h1 align="center">
  🛡️ RiskGuard API
</h1>

<p align="center">
  API de detecção de fraude em transações financeiras em tempo real
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen?style=flat-square&logo=springboot"/>
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql"/>
  <img src="https://img.shields.io/badge/Docker-ready-2496ED?style=flat-square&logo=docker"/>
  <img src="https://img.shields.io/badge/JWT-Auth-black?style=flat-square&logo=jsonwebtokens"/>
</p>

---

## 📌 Sobre o projeto

RiskGuard é uma API REST que analisa transações financeiras em tempo real usando um **motor de pontuação de risco** com 3 regras independentes. Cada transação recebe um score de 0 a 100 e é classificada como `APPROVED`, `FLAGGED` ou `BLOCKED`.

Desenvolvido como projeto de portfólio para demonstrar habilidades em arquitetura de APIs REST com Spring Boot, segurança com JWT e boas práticas de desenvolvimento backend.

---

## ⚙️ Como o motor de fraude funciona

| Regra | Condição | Score |
|---|---|---|
| Desvio da média | Valor > 3× a média histórica do usuário | +40 |
| Ataque de repetição | 5+ transações no último minuto | +80 |
| Valor crítico | Transação acima de R$ 10.000 | +60 |

| Score final | Status |
|---|---|
| 0 – 29 | ✅ APPROVED |
| 30 – 59 | ⚠️ FLAGGED |
| 60 – 100 | 🚫 BLOCKED |

---

## 🚀 Rodando o projeto

### Pré-requisito: Docker instalado

```bash
# Clone o repositório
git clone https://github.com/QualyFerrer/risk-guard-api.git
cd risk-guard-api

# Sobe banco + aplicação
docker compose up --build
```

A API estará disponível em `http://localhost:8080`
Documentação Swagger: `http://localhost:8080/swagger-ui.html`

### Rodando localmente (sem Docker)

```bash
# Sobe apenas o banco
docker compose up postgres

# Roda a aplicação pela IDE ou por:
./mvnw spring-boot:run
```

---

## 📡 Endpoints

### Autenticação
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/api/auth/login` | Retorna token JWT | ❌ |

### Usuários
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/api/users` | Cadastra novo usuário | ❌ |
| GET | `/api/users/{id}` | Busca usuário por ID | ✅ |
| GET | `/api/users` | Lista todos os usuários | ✅ |

### Transações
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/api/transactions/user/{userId}` | Processa transação com análise de fraude | ✅ |
| GET | `/api/transactions/user/{userId}` | Histórico de transações | ✅ |
| GET | `/api/transactions/alerts/user/{userId}` | Alertas de fraude gerados | ✅ |

---

## 🔐 Autenticação

```bash
# 1. Cria um usuário
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"fullName":"João Silva","email":"joao@email.com","password":"123456","initialBalance":5000}'

# 2. Faz login e obtém o token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@email.com","password":"123456"}'

# 3. Usa o token nas próximas requisições
curl -X POST http://localhost:8080/api/transactions/user/1 \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{"amount":500,"description":"Pagamento fornecedor"}'
```

---

## 🛠️ Tecnologias

- **Java 21** + **Spring Boot 3.3.5**
- **Spring Security** + **JWT (JJWT 0.12.6)**
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL 16**
- **Bean Validation**
- **Springdoc OpenAPI 2.6.0**
- **JUnit 5** + **Mockito**
- **Docker** + **Docker Compose**

---

## 🧪 Testes

```bash
./mvnw test
```

---

## 👨‍💻 Autor

**César Ferrer**
[![GitHub](https://img.shields.io/badge/GitHub-QualyFerrer-181717?style=flat-square&logo=github)](https://github.com/QualyFerrer)