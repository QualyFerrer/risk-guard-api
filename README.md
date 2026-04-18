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

**RiskGuard** é uma API REST que analisa transações financeiras em tempo real utilizando um **motor de pontuação de risco**.

Cada transação recebe um score de **0 a 100**, sendo classificada como:

* ✅ APPROVED
* ⚠️ FLAGGED
* 🚫 BLOCKED

Projeto desenvolvido com foco em demonstrar:

* Arquitetura REST bem estruturada
* Segurança com JWT
* Integração com banco relacional
* Boas práticas de backend

---

## ⚙️ Motor de fraude

O sistema utiliza regras independentes para calcular o risco:

| Regra           | Condição                    | Score |
| --------------- | --------------------------- | ----- |
| Desvio da média | Valor > 3× média do usuário | +40   |
| Alta frequência | 5+ transações em 1 minuto   | +80   |
| Valor elevado   | Acima de R$ 10.000          | +60   |

### Resultado final

| Score    | Status     |
| -------- | ---------- |
| 0 – 29   | ✅ APPROVED |
| 30 – 59  | ⚠️ FLAGGED |
| 60 – 100 | 🚫 BLOCKED |

---

## 🚀 Rodando o projeto

### 🐳 Com Docker (recomendado)

```bash
git clone https://github.com/QualyFerrer/risk-guard-api.git
cd risk-guard-api

docker compose up --build
```

### 🌐 Acesso

* API: http://localhost:8081
* Swagger: http://localhost:8081/swagger-ui.html

---

### 💻 Rodando localmente (sem Docker)

```bash
docker compose up postgres

./mvnw spring-boot:run
```

### 🌐 Acesso local

* API: http://localhost:8080
* Swagger: http://localhost:8080/swagger-ui.html

---

## 📡 Endpoints

### 🔐 Autenticação

| Método | Endpoint          | Descrição         |
| ------ | ----------------- | ----------------- |
| POST   | `/api/auth/login` | Retorna token JWT |

---

### 👤 Usuários

| Método | Endpoint          | Auth |
| ------ | ----------------- | ---- |
| POST   | `/api/users`      | ❌    |
| GET    | `/api/users/{id}` | ✅    |
| GET    | `/api/users`      | ✅    |

---

### 💳 Transações

| Método | Endpoint                                 | Auth |
| ------ | ---------------------------------------- | ---- |
| POST   | `/api/transactions/user/{userId}`        | ✅    |
| GET    | `/api/transactions/user/{userId}`        | ✅    |
| GET    | `/api/transactions/alerts/user/{userId}` | ✅    |

---

## 🔐 Autenticação JWT

### 1. Criar usuário

```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"fullName":"João Silva","email":"joao@email.com","password":"123456","initialBalance":5000}'
```

---

### 2. Login

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@email.com","password":"123456"}'
```

---

### 3. Usar token

```bash
curl -X POST http://localhost:8081/api/transactions/user/1 \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{"amount":500,"description":"Pagamento fornecedor"}'
```

---

## 🧪 Testes

```bash
./mvnw test
```

---

## 🏗️ Arquitetura

* Controller → Entrada da requisição
* Service → Regras de negócio (motor de fraude)
* Repository → Acesso ao banco
* Security → JWT + filtros

---

## 🛠️ Tecnologias

* Java 21
* Spring Boot 3.3.5
* Spring Security + JWT
* Spring Data JPA + Hibernate
* PostgreSQL
* Springdoc OpenAPI (Swagger)
* Docker + Docker Compose

---

## 💡 Diferenciais

* Motor de fraude com regras desacopladas
* API stateless com JWT
* Pronta para escalar (containerizada)
* Documentação automática com Swagger

---

## 👨‍💻 Autor

**César Ferrer**
GitHub: https://github.com/QualyFerrer

---
