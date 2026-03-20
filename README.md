# Microservices Sandbox

Projeto de estudo, aplicando conhecimentos em Arquitetura de microserviços e Observabilidade.

## Tecnologias

- Caddy (Proxy Reverso + API Gateway)
- Java + Spring Framework
    - Spring Boot
    - Spring Security
    - Spring Actuator
    - OAuth & JWT
- Kotlin + Quarkus Framework
    - Micrometer
- OpenSSL (RSA Keys Generator)
- Docker & Docker Compose
- PostgreSQL & Redis

## Serviços

### Gateway
Configurações do servidor Caddy, atuando como Proxy Reverso e API Gateway para os demais serviços.

### Auth Service
Microserviço de autenticação em Java, lidando com OAuth + JWT e Criptografia Assimétrica com chaves RSA. Expõe métricas com Spring Actuator + Micrometer.

### Core Service
Microserviço leve em Kotlin + Quarkus. Expõe métricas com a extensão _micrometer-registry-prometheus_.

### Prometheus
Serviço **TSDB** (Time Series Database) para lidar com métricas, realizando **scrape** nos microserviços.

### Grafana
Serviço para visualização de métricas gerenciadas pelo _Prometheus_ tem tempo real.

