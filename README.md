# NoBank - Distributed Scheduling com Spring Boot

Projeto demo para talk sobre **Distributed Scheduling com Spring Boot**. Simula um cenário onde propostas de cartão de crédito elegíveis precisam ser processadas por um job agendado que consulta APIs externas e vincula cartões às propostas.

## Stack

- Java 21
- Spring Boot 3.5.13
- Spring Cloud OpenFeign
- PostgreSQL 17
- Hibernate 6 (batch inserts, pessimistic locking com `SKIP LOCKED`)
- WireMock (mock das APIs externas)
- Docker Compose

## Como rodar

### 1. Subir os containers (PostgreSQL + WireMock)

```shell
docker compose up -d
```

Na primeira inicialização, o PostgreSQL executa automaticamente os scripts em `bootstrap/`:
- `01-schema.sql` — cria as tabelas `card` e `proposal`
- `02-proposals_generator.sql` — popula 10.000 propostas com status `ELIGIBLE`

### 2. Rodar a aplicação

```shell
./mvnw spring-boot:run
```

O job `AttachCardsToProposalsJob` inicia após 10 segundos e roda a cada 1 minuto, processando propostas em lotes de 50.

### 3. Criar uma proposta manualmente

```shell
curl -X POST http://localhost:8080/api/v1/proposals \
  -H "Content-Type: application/json" \
  -d '{
    "document": "045.371.139-50",
    "email": "joao@email.com",
    "name": "Joao da Silva",
    "address": "Rua das Tabajaras, 123",
    "salary": 5000.00
  }'
```

## Cenarios explorados no Job

1. Naive solution
2. Top50 (paginacao) — evita `OutOfMemoryError`
3. Top50 + loop — transacao longa
4. Top50 + `TransactionTemplate` — controle manual de transacao
5. Cluster + `synchronized` — nao funciona em ambiente distribuido
6. Cluster + `FOR UPDATE` — funciona mas serializa os nos
7. Cluster + `FOR UPDATE SKIP LOCKED` — processamento paralelo entre nos
8. Performance: `initialDelay`, Hibernate batch size, tuning do banco
9. Error handling: timeouts, retries, idempotencia, bulkhead

## Comandos uteis

Resetar os mappings do WireMock:
```shell
curl -X POST http://localhost:9999/__admin/reset
```

Recriar o banco do zero (limpa o volume Docker):
```shell
docker compose down -v && docker compose up -d
```

Rodar os testes:
```shell
./mvnw test
```
