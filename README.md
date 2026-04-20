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

RiskGuard é uma API REST que analisa transações financeiras em tempo real utilizando um motor de pontuação de risco baseado em regras.

Cada transação recebe um score de 0 a 100 e é classificada como `APPROVED`, `FLAGGED` ou `BLOCKED`, de acordo com o nível de risco identificado.

O objetivo do projeto é simular cenários reais de detecção de fraude, aplicando boas práticas de desenvolvimento backend com foco em segurança, organização e clareza.

---

## ⚙️ Como o motor de fraude funciona

| Regra | Condição | Score |
|---|---|---|
| Desvio da média | Valor > 3× a média histórica do usuário | +40 |
| Ataque de repetição | 5+ transações no último minuto | +40 |
| Valor crítico | Transação acima de R$ 10.000 | +60 |

### Classificação final

| Score final | Status |
|---|---|
| 0 – 29 | APPROVED |
| 30 – 59 | FLAGGED |
| 60 – 100 | BLOCKED |

---

## 🚀 Rodando o projeto

### Pré-requisito: Docker instalado

```bash
git clone https://github.com/QualyFerrer/risk-guard-api.git
cd risk-guard-api

docker compose up --build
