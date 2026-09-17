# CampusGigs

API REST desenvolvida em Java com Spring Boot para uma plataforma de contratação de serviços entre usuários.

O sistema permite cadastro e autenticação de usuários, publicação de serviços, contratação de serviços e gerenciamento do fluxo das contratações, utilizando autenticação JWT, controle de acesso por roles, PostgreSQL, Flyway, Docker e integração externa para consulta de CEP com HttpExchange.

## Tecnologias

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT
- BCrypt
- PostgreSQL 16
- Flyway
- Docker Compose
- HttpExchange
- ViaCEP
- Maven

## Funcionalidades

### Usuários

- Cadastro de usuários
- Senhas armazenadas com BCrypt
- Login com geração de token JWT
- Roles `USER` e `ADMIN`
- Consulta automática de cidade e UF a partir do CEP
- Tratamento de CEP inexistente

### Serviços

- Publicação de serviços
- Listagem de serviços
- Busca de serviço por ID
- Atualização de serviços
- Controle de propriedade do serviço
- Administradores podem gerenciar serviços de outros usuários
- Situações:
    - `ATIVO`
    - `PAUSADO`
    - `ENCERRADO`
- Serviços encerrados não podem ser reativados

### Contratações

- Solicitação de contratação de um serviço
- Usuário não pode contratar o próprio serviço
- Apenas serviços ativos podem ser contratados
- Controle de acesso entre contratante e prestador

Fluxos possíveis:

```text
SOLICITADA -> ACEITA -> CONCLUIDA
SOLICITADA -> RECUSADA
SOLICITADA -> CANCELADA
```

Contratações concluídas, recusadas ou canceladas são consideradas estados finais.

## Banco de dados

O projeto utiliza PostgreSQL 16 executado com Docker Compose.

O schema do banco é versionado pelo Flyway.

O Hibernate está configurado com:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Dessa forma, o Hibernate valida as entidades em relação ao banco, enquanto a criação e evolução do schema ficam sob responsabilidade das migrations do Flyway.

## Como executar

### Pré-requisitos

É necessário possuir:

- Java 21
- Docker
- Docker Compose

### 1. Clonar o repositório

```bash
git clone https://github.com/FelipeAugusto99/CampusGigs.git
cd CampusGigs
```

### 2. Subir o PostgreSQL

Na raiz do projeto:

```bash
docker compose up -d
```

Para verificar o container:

```bash
docker ps
```

O container esperado é:

```text
campusgigs-postgres
```

### 3. Executar a aplicação

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Em Linux/macOS:

```bash
./mvnw spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

Ao iniciar a aplicação, o Flyway executará automaticamente as migrations pendentes.

## Configuração do PostgreSQL

Configuração utilizada pelo Docker Compose:

```text
Banco: campusgigs
Usuário: campusgigs
Senha: campusgigs
Porta: 5432
```

A aplicação utiliza:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/campusgigs
spring.datasource.username=campusgigs
spring.datasource.password=campusgigs
```

## Autenticação

O projeto utiliza JWT.

Primeiro, o usuário realiza cadastro ou login. Após um login válido, a API retorna um token.

Exemplo:

```http
POST /auth/login
Content-Type: application/json
```

```json
{
  "email": "felipe@campusgigs.com",
  "senha": "123456"
}
```

Resposta:

```json
{
  "id": 1,
  "nome": "Felipe Augusto",
  "email": "felipe@campusgigs.com",
  "role": "USER",
  "token": "TOKEN_JWT",
  "mensagem": "Login realizado com sucesso"
}
```

Para acessar endpoints protegidos, o token deve ser enviado no header:

```text
Authorization: Bearer TOKEN_JWT
```

## Exemplo de chamada autenticada

Exemplo de publicação de serviço:

```http
POST /servicos
Authorization: Bearer TOKEN_JWT
Content-Type: application/json
```

```json
{
  "titulo": "Desenvolvimento de site",
  "descricao": "Criacao de site responsivo para pequenos negocios",
  "categoria": "Tecnologia",
  "preco": 500.00
}
```

## Principais endpoints

### Autenticação

```text
POST /auth/cadastro
POST /auth/login
```

### Serviços

```text
POST  /servicos
GET   /servicos
GET   /servicos/{id}
PUT   /servicos/{id}
PATCH /servicos/{id}/situacao
```

### Contratações

```text
POST  /contratacoes
GET   /contratacoes
GET   /contratacoes/{id}
PATCH /contratacoes/{id}/situacao
```

## Integração externa de CEP

Durante o cadastro, o CEP informado pelo usuário é consultado em um serviço externo utilizando o cliente HTTP declarativo do Spring com `@HttpExchange`.

A integração utiliza o ViaCEP.

Exemplo:

```text
CEP: 01001000
```

Resultado obtido:

```text
Cidade: São Paulo
UF: SP
```

Essas informações são preenchidas automaticamente e persistidas no usuário.

Caso o CEP não exista, a operação é rejeitada e o usuário não é cadastrado.

Caso o serviço externo esteja indisponível, a API trata a falha sem persistir um cadastro incompleto.

## Segurança e autorização

Os endpoints protegidos exigem JWT válido.

O sistema diferencia os papéis:

```text
USER
ADMIN
```

Um usuário comum não pode modificar recursos pertencentes a outro usuário.

Administradores possuem permissões administrativas previstas pelas regras da aplicação.

Nas contratações, contratante e prestador possuem permissões diferentes para alterar a situação.

Exemplos:

```text
Contratante tentando aceitar uma solicitação:
403 Forbidden

Prestador tentando cancelar uma solicitação:
403 Forbidden
```

## Tratamento de erros

A aplicação possui tratamento centralizado de exceções.

Principais respostas utilizadas:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
409 Conflict
503 Service Unavailable
```

Exemplos:

- CEP inexistente: `400 Bad Request`
- Token ausente ou inválido: `401 Unauthorized`
- Operação sem permissão: `403 Forbidden`
- Transição inválida de situação: `409 Conflict`
- Falha na consulta externa de CEP: `503 Service Unavailable`

## Testes manuais realizados

Foram testados manualmente:

- Cadastro de usuário
- Login com senha correta
- Login com senha incorreta
- Acesso a endpoint protegido sem JWT
- Acesso a endpoint protegido com JWT
- Publicação de serviço
- Atualização do próprio serviço
- Bloqueio da alteração de serviço de outro usuário
- Operação administrativa
- Alteração das situações de serviço
- Bloqueio da reativação de serviço encerrado
- Contratação de serviço de outro usuário
- Bloqueio da contratação do próprio serviço
- Aceite de contratação pelo prestador
- Bloqueio do aceite pelo contratante
- Recusa de contratação
- Cancelamento pelo contratante
- Bloqueio do cancelamento pelo prestador
- Conclusão da contratação
- Bloqueio de alteração após estado final
- Consulta de CEP válido
- Rejeição de CEP inexistente

## Checkpoints

O desenvolvimento foi organizado em checkpoints incrementais:

### CP1 — Infraestrutura e persistência

Configuração do PostgreSQL com Docker Compose e versionamento do schema com Flyway.

**Decisão:** utilizar PostgreSQL em container para tornar o ambiente de banco reproduzível.

### CP2 — Cadastro e autenticação

Implementação de cadastro, login e armazenamento seguro de senhas com BCrypt.

**Decisão:** nunca armazenar senhas em texto puro.

### CP3 — JWT

Implementação da geração e validação de tokens JWT para autenticação stateless.

**Decisão:** utilizar o e-mail do usuário como subject do token.

### CP4 — Autorização

Implementação de roles e regras de autorização sobre os recursos da aplicação.

**Decisão:** validar também a propriedade dos recursos, impedindo que usuários comuns alterem recursos pertencentes a outros usuários.

### CP5 — Integração externa e revisão final

Integração de CEP utilizando `HttpExchange`, preenchimento automático de cidade e UF e tratamento de falhas da integração.

**Decisão:** em caso de CEP inexistente ou falha na integração externa, o cadastro não é persistido de forma incompleta.

## Estrutura principal

```text
src/main/java/br/com/fiap/campusgigs
├── client
├── config
├── controller
├── dto
├── exception
├── model
├── repository
├── security
└── service
```

## Autor

Felipe Augusto Lopes Ferreira