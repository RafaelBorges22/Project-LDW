# Project-LDW — Kazu Tattoo

Sistema web para um estúdio de tatuagem: clientes se cadastram, solicitam orçamentos (com imagem de referência), acompanham o status e conversam com o estúdio por chat em tempo real; o administrador gerencia clientes e responde aos orçamentos.

Projeto Interdisciplinar do 4º semestre — Fatec (disciplina LDW, Laboratório de Desenvolvimento Web).

## Funcionalidades

- **Autenticação** com JWT (JSON Web Token) e perfis `ADMIN` e `CLIENT`
- **Clientes**: cadastro, edição, busca por e-mail/filtro e remoção (admin)
- **Recuperação de senha** por e-mail (token com expiração)
- **Orçamentos**: criação com upload de imagem, parte do corpo e tamanho; admin define valor e custos adicionais; histórico por cliente
- **Chat privado** cliente ↔ estúdio via WebSocket (STOMP), com histórico persistido no MongoDB
- **Documentação da API** via Swagger/OpenAPI

## Stack

| Camada | Tecnologias |
|---|---|
| Backend | Java 17, Spring Boot 3.5 (Web, Data JPA, Data MongoDB, Security, WebSocket, Mail), JJWT, Lombok, springdoc-openapi |
| Frontend | Vue 3, Vue Router, Vite, TypeScript, SCSS, Axios, STOMP.js + SockJS |
| Dados | MySQL 8 (clientes e orçamentos), MongoDB 4.4 (mensagens do chat) |
| Testes | JUnit, Cucumber, REST Assured |
| Infra | Docker Compose, Dockerfile multi-stage |

## Estrutura

```
.
├── compose.yaml                     # MySQL + MongoDB
├── docker/mysql-init/init.sql       # schema inicial e usuários de exemplo
├── pom.xml
└── src/main
    ├── java/ldw/squad/project
    │   ├── Config/        # segurança, JWT, CORS, Swagger, upload
    │   ├── Controller/    # REST: auth, clients, quotes, upload
    │   ├── Dto/ Entities/ Mapper/ Repository/ Service/
    │   └── chat/
    │       ├── history/   # REST do histórico (MongoDB)
    │       └── websocket/ # STOMP / WebSocket
    └── resources
        ├── application.properties
        └── templates/projectLdwFront/   # frontend Vue
```

## Como executar

### Pré-requisitos

- JDK 17
- Node.js `^20.19.0` ou `>=22.12.0`
- Docker e Docker Compose

### 1. Bancos de dados

```bash
docker compose up -d
```

Sobe o MySQL na porta **3307** (banco `ldw_database`, usuário/senha `root`) e o MongoDB na **27017**. O `init.sql` cria a tabela `clients` e usuários de exemplo.

> O backend inclui `spring-boot-docker-compose`, então ao rodar pela aplicação o Compose também pode ser iniciado automaticamente.

### 2. Variáveis de ambiente (e-mail)

A recuperação de senha envia e-mail via SMTP do Gmail. Defina as credenciais (use uma [senha de app](https://support.google.com/accounts/answer/185833)):

```bash
export MAIL_USERNAME=seu-email@gmail.com
export MAIL_PASSWORD=sua-senha-de-app
```

Sem elas a aplicação sobe normalmente, mas o envio de e-mail falha.

### 3. Backend

```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

API em `http://localhost:8081`. Swagger UI em `http://localhost:8081/swagger-ui.html`.

### 4. Frontend

```bash
cd src/main/resources/templates/projectLdwFront
npm install
npm run dev
```

As URLs da API ficam em `src/main/resources/templates/projectLdwFront/.env` (padrão: `http://localhost:8081`).

### Usuários de exemplo

Criados pelo `init.sql`, ambos com a senha `SenhaAdmin1!`:

| Perfil | E-mail |
|---|---|
| ADMIN | `rafaelmascarenhasborges@gmail.com` |
| CLIENT | `contacontaconta0002@gmail.com` |

## API

| Recurso | Endpoints |
|---|---|
| Auth | `POST /auth/login` |
| Clientes | `GET /clients`, `GET /clients/{id}`, `GET /clients/email/{email}`, `GET /clients/filter`, `POST /clients`, `PUT /clients/{id}`, `DELETE /clients/{id}/admin`, `POST /clients/request`, `POST /clients/reset` |
| Orçamentos | `GET /quotes`, `GET /quotes/{id}`, `GET /quotes/{clientId}/history`, `POST /quotes` (multipart), `PUT /quotes/{id}`, `DELETE /quotes/{id}/admin` |
| Upload | `POST /upload`, `GET /upload/download/{fileName}` |
| Chat (histórico) | `GET/POST /mensagemchat`, `GET/PUT/DELETE /mensagemchat/{id}`, `GET /mensagemchat/historico/{user1}/{user2}` |
| Chat (WebSocket) | endpoint `/ws`; envio em `/app/chat.addUser` e `/app/chat.privateMessage`; assinatura em `/topic/users` (usuários online) e `/topic/room/{roomId}` (mensagens) |

Detalhes de payloads no Swagger.

## Testes

```bash
./mvnw test
```

## Equipe

- Rafael Borges — [@RafaelBorges22](https://github.com/RafaelBorges22)
- Lucas Retanero
- Kevyn Cavalcanti
- [@Tavaressan](https://github.com/Tavaressan)
