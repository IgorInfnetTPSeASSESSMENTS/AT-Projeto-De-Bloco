![CI](https://github.com/IgorInfnetTPSeASSESSMENTS/AT-Projeto-De-Bloco/actions/workflows/ci.yml/badge.svg)
![CD](https://github.com/IgorInfnetTPSeASSESSMENTS/AT-Projeto-De-Bloco/actions/workflows/cd.yml/badge.svg)
![Security](https://github.com/IgorInfnetTPSeASSESSMENTS/AT-Projeto-De-Bloco/actions/workflows/security.yml/badge.svg)
![CodeQL](https://github.com/IgorInfnetTPSeASSESSMENTS/AT-Projeto-De-Bloco/actions/workflows/codeql.yml/badge.svg)

# ADOPET

Sistema web CRUD para gestão de abrigos, pets e solicitações de adoção, desenvolvido em Java 21 com Spring Boot, Spring MVC e Thymeleaf.

Esta entrega final consolida:

- CRUD completo
- Clean Code, modularidade e CQS
- imutabilidade e uso de Value Objects
- testes automatizados em múltiplos níveis
- Selenium WebDriver com Page Object Model
- testes parametrizados, property-based e fuzz testing
- CI/CD com GitHub Actions
- SAST, análise de dependências e verificação estática de código
- deploy automatizado para múltiplos ambientes na AWS
- validação pós-deploy
- monitoramento, logs e alertas

## Objetivo Acadêmico

O projeto foi estruturado para atender à etapa final da disciplina, demonstrando um sistema maduro, integrado a uma esteira de build, testes, segurança e deploy.

Além do CRUD funcional, a solução evidencia:

- separação entre leitura e escrita com Command/Query Separation
- regras de negócio centralizadas no domínio
- tratamento de falhas com fail early e fail gracefully
- automação de qualidade no pipeline
- validação pós-publicação em ambiente implantado

## Stack

- Java 21
- Maven
- Spring Boot 4
- Spring MVC
- Thymeleaf
- Jakarta Bean Validation
- JUnit 5
- jqwik
- Mockito
- Selenium WebDriver
- JaCoCo
- Checkstyle
- OWASP Dependency Check
- GitHub CodeQL
- Terraform
- AWS ECS Fargate, ALB, CloudWatch e SNS
- Locust

## Arquitetura

O sistema foi organizado em camadas com responsabilidades explícitas.

```text
src/main/java
└── adopet
    ├── application
    │   ├── adoption
    │   └── shelterandpet
    ├── domain
    │   ├── adoption
    │   └── shelterandpet
    ├── exception
    ├── gateway
    ├── infrastructure
    │   └── memory
    └── web
        ├── config
        ├── controller
        ├── dto
        └── exception
```

### Domain

Contém entidades e Value Objects imutáveis. O projeto usa `record` para representar objetos de domínio e reforçar invariantes.

Exemplos:

- `Shelter`
- `Pet`
- `AdoptionRequest`
- `PhoneNumber`
- `Email`
- `ApplicantDocument`
- `ApplicantName`
- `ReasonText`
- `AgeYears`
- `WeightKg`

### Application

Contém casos de uso separados em comandos e consultas.

Exemplos:

- comandos: `RegisterShelterCommandHandler`, `UpdatePetCommandHandler`, `CreateAdoptionRequestCommandHandler`
- queries: `ListSheltersQuery`, `ListShelterPetsQuery`, `GetAdoptionRequestDetailsQuery`

Essa separação reduz acoplamento entre leitura e escrita e deixa o fluxo de negócio mais previsível.

### Gateway

As interfaces de gateway abstraem persistência e serviços externos.

Exemplos:

- `ShelterGateway`
- `PetGateway`
- `AdoptionRequestGateway`
- `EligibilityAnalysisGateway`
- `NotificationGateway`

### Infrastructure

Contém implementações concretas usadas neste projeto.

Exemplos:

- `InMemoryShelterGateway`
- `InMemoryPetGateway`
- `InMemoryAdoptionRequestGateway`
- `ProgrammableEligibilityAnalysisGateway`
- `ProgrammableNotificationGateway`

A persistência é em memória, o que simplifica os testes e a execução acadêmica. No deploy atual, a task ECS roda com `desired_count = 1`, o que mantém o comportamento coerente com esse modelo.

### Web

Contém a interface MVC:

- controllers
- DTOs de formulário
- templates Thymeleaf
- tratamento global de exceções

## Funcionalidades

### Abrigos

- cadastrar
- listar
- editar
- excluir

### Pets

- cadastrar pet dentro de um abrigo
- listar pets do abrigo
- editar
- excluir
- importar CSV
- exportar CSV

### Solicitações de adoção

- criar
- listar por pet
- visualizar detalhes
- editar
- aprovar
- rejeitar
- cancelar
- excluir
- executar nova análise de elegibilidade
- simular cenários de falha de análise e notificação

## Princípios de Qualidade Aplicados

### Clean Code e modularidade

O código foi dividido por contexto de negócio e por camada arquitetural, evitando mistura de responsabilidades entre domínio, aplicação, infraestrutura e web.

### Imutabilidade

As classes centrais de domínio são modeladas com `record` e retornam novas instâncias em transições relevantes, como acontece em `AdoptionRequest`.

### Separação entre leitura e escrita

Leituras são concentradas em queries e escritas em command handlers, reduzindo efeitos colaterais implícitos e deixando os testes mais simples.

### Coesão e baixo acoplamento

Regras ficam no domínio, orquestração na camada application e detalhes técnicos atrás de gateways.

## Regras de Negócio Relevantes

### Abrigos

- nome não pode ser vazio
- telefone não pode ser vazio
- email deve ser válido
- não é permitido cadastrar abrigo duplicado por nome

### Pets

- nome, raça e cor são obrigatórios
- idade e peso devem estar em faixa válida
- o abrigo precisa existir

### Solicitações de adoção

- pet e abrigo devem ser válidos
- dados do solicitante são obrigatórios
- não é permitido criar solicitação ativa duplicada para o mesmo pet e documento
- somente solicitações pendentes ou em análise podem ser editadas, aprovadas ou rejeitadas
- análise automática recalcula o status

## Fail Early e Fail Gracefully

### Fail Early

As entradas são validadas o mais cedo possível:

- Bean Validation nos formulários web
- validações nos command handlers
- invariantes e Value Objects no domínio

### Fail Gracefully

Quando há falhas em serviços externos simulados:

- a solicitação ainda pode ser registrada com análise `UNAVAILABLE`
- a falha de notificação não derruba a operação principal
- exceções de negócio retornam mensagens amigáveis na interface

## Estratégia de Testes

O projeto possui testes em múltiplos níveis.

### 1. Testes de domínio

Validam invariantes, Value Objects e transições de status.

### 2. Testes da camada application

Validam command handlers, queries, regras de transição e fallback em falhas.

### 3. Testes de infraestrutura em memória

Validam persistência, filtros, atualização e remoção.

### 4. Testes web

Validam controllers, formulários, mensagens de erro e tratamento de exceções.

### 5. Testes Selenium

Cobrem a interface web com:

- Selenium WebDriver
- Page Object Model
- fluxos end-to-end
- validação de mensagens
- simulação de timeout e erro externo

### 6. Testes parametrizados e property-based

O projeto usa:

- `@ParameterizedTest`
- `@CsvSource`
- `@ValueSource`
- `jqwik @Property`

### 7. Fuzz testing

Há testes que enviam entradas aleatórias e maliciosas para validar robustez contra entradas inválidas e inesperadas.

## Page Object Model

Os testes Selenium foram organizados com POM, encapsulando a navegação e as interações em classes próprias.

Exemplos:

- `HomePage`
- `SheltersListPage`
- `ShelterCreatePage`
- `PetsListPage`
- `PetCreatePage`
- `AdoptionRequestsListPage`
- `AdoptionRequestCreatePage`
- `AdoptionRequestDetailsPage`

## CI/CD com GitHub Actions

O projeto utiliza workflows reutilizáveis e separados por responsabilidade.

### `ci.yml`

Executa o pipeline principal de integração contínua:

- checkout
- Java 21
- `mvn clean verify jacoco:report -Dheadless=true -DskipITs=true`
- upload de relatórios
- resumo da execução

Esse workflow roda:

- Checkstyle
- testes unitários e web
- cobertura JaCoCo

Os testes `*IT.java` não rodam aqui, porque o pós-deploy fica reservado ao workflow específico após publicação.

### `security.yml`

Executa segurança de dependências com OWASP Dependency Check:

- purge de cache corrompido
- análise com NVD API Key
- geração de relatório HTML e JSON
- upload de artifacts

### `codeql.yml`

Executa SAST com GitHub CodeQL sobre o código Java.

### `cd.yml`

Executa o fluxo de entrega contínua:

- build, testes, cobertura e Checkstyle
- build e push da imagem Docker por ambiente
- provisionamento Terraform por ambiente
- deploy no ECS
- health check
- smoke test pós-deploy
- load test no ambiente de teste

## Deploy Multiambiente

Os ambientes tratados no pipeline são:

- `dev`
- `test`
- `prod`

Regras atuais:

- `develop` publica em `dev`
- `test` publica em `test`
- `main`, `release` e `workflow_dispatch` publicam em `prod`

Cada ambiente utiliza seu próprio segredo `DOCKER_IMAGE`, permitindo publicar em repositórios ECR distintos. Exemplo recomendado:

- `dev` -> `<account>.dkr.ecr.sa-east-1.amazonaws.com/adopet-dev`
- `test` -> `<account>.dkr.ecr.sa-east-1.amazonaws.com/adopet-test`
- `prod` -> `<account>.dkr.ecr.sa-east-1.amazonaws.com/adopet-prod`

## Infraestrutura AWS

O diretório `infra/` provisiona:

- backend Terraform em S3 com lock no DynamoDB
- ECS Cluster
- ECS Service em Fargate
- ALB
- Security Groups
- CloudWatch Log Group
- CloudWatch Alarms
- SNS para alertas

## Deploy ECS

O deploy:

- obtém a task definition atual
- gera nova revisão com a imagem publicada
- atualiza o serviço
- aguarda estabilização
- executa health check no ALB
- realiza rollback se o health check falhar

## Pós-Deploy

O workflow `post_deploy_selenium.yml` roda apenas depois do deploy e do health check.

Ele:

- instala Google Chrome
- instala Chromedriver
- injeta `APP_BASE_URL`
- executa:

```bash
mvn verify \
  -Dapp.baseUrl="${APP_BASE_URL}" \
  -Dheadless=true \
  -Dit.test=PostDeploySmokeIT,PostDeployFunctionalIT
```

### `PostDeploySmokeIT`

Valida:

- carregamento da home
- navegação inicial

### `PostDeployFunctionalIT`

Valida o sistema publicado com um fluxo funcional completo:

- cria abrigo
- cria pet
- cria solicitação
- aprova solicitação

## Testes de Carga

O workflow `load_test_locust.yml` executa Locust no ambiente `test`, depois do smoke test.

Atualmente:

- 20 usuários
- ramp-up de 5 usuários por segundo
- duração de 1 minuto

## Segurança

O projeto possui três frentes principais de segurança no pipeline.

### 1. SAST

- GitHub CodeQL

### 2. Segurança de dependências

- OWASP Dependency Check

### 3. Gestão de credenciais

- uso de `secrets` no GitHub Actions
- autenticação AWS via OIDC
- ausência de credenciais fixas no repositório

Segredos esperados pelo pipeline:

- `NVD_API_KEY` no nível do repositório
- `AWS_GITHUB_ROLE_ARN` por environment
- `DOCKER_IMAGE` por environment
- `AWS_VPC_ID` por environment
- `AWS_PUBLIC_SUBNET_IDS` por environment
- `ALERT_EMAIL` por environment

### Segregação por environment

Os workflows reutilizáveis recebem `environment: ${{ inputs.environment_name }}`. Com isso, `dev`, `test` e `prod` podem ter configuração realmente isolada no GitHub Actions, incluindo:

- role AWS distinta por ambiente
- repositório ECR distinto por ambiente
- VPC e subnets distintas por ambiente
- email de alerta distinto por ambiente

Para a imagem Docker, a recomendação é criar um repositório ECR para cada ambiente e cadastrar o valor correspondente em `DOCKER_IMAGE` dentro do environment respectivo.

## Monitoramento, Logs e Alertas

O projeto possui:

- logs da aplicação enviados ao CloudWatch Logs
- alarmes para 5xx no ALB
- alarmes para hosts não saudáveis
- alarmes para CPU e memória do ECS
- notificações via SNS
- step summaries nos workflows
- upload de artifacts de relatórios
- badges no README

## Como Executar Localmente

### Pré-requisitos

- Java 21
- Maven 3.8+

### Subir a aplicação

```bash
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### Rodar validação local principal

```bash
mvn clean verify jacoco:report -Dheadless=true -DskipITs=true
```

Esse comando executa:

- Checkstyle
- testes unitários
- testes web
- testes Selenium locais que não dependem de `*IT`
- cobertura JaCoCo

### Rodar apenas os testes unitários/web

```bash
mvn test -Dheadless=true
```

### Rodar os testes pós-deploy manualmente

```bash
mvn verify \
  -Dapp.baseUrl=http://HOST_PUBLICADO \
  -Dheadless=true \
  -Dit.test=PostDeploySmokeIT,PostDeployFunctionalIT
```

## Como Interpretar os Resultados

### Testes

- relatórios Surefire: `target/surefire-reports`
- relatórios Failsafe: `target/failsafe-reports`

### Cobertura

- relatório JaCoCo: `target/site/jacoco/index.html`

### Segurança

- relatório OWASP HTML: `target/dependency-check-report.html`
- relatório OWASP JSON: `target/dependency-check-report.json`

### Load test

Artifacts do workflow:

- `locust-report.html`
- `locust-report_stats.csv`
- `locust-report_failures.csv`
- `locust-report_exceptions.csv`

## Estrutura Relevante do Repositório

```text
.
├── .github/workflows
│   ├── ci.yml
│   ├── cd.yml
│   ├── security.yml
│   ├── codeql.yml
│   ├── infra_terraform.yml
│   ├── deploy_ecs.yml
│   ├── post_deploy_selenium.yml
│   └── load_test_locust.yml
├── infra
├── src/main/java
├── src/main/resources/templates
├── src/test/java
├── Dockerfile
├── locustfile.py
└── pom.xml
```

## Conclusão

Esta entrega final reúne um sistema CRUD completo com foco em qualidade de código, testes automatizados, segurança e operação contínua.

O repositório demonstra:

- arquitetura modular
- domínio orientado a regras
- robustez diante de falhas
- validação de interface e backend
- automação de pipeline ponta a ponta
- deploy multiambiente com validação pós-publicação
- observabilidade básica com logs, alarmes e relatórios

Para o contexto da disciplina, a solução atende ao objetivo de apresentar um sistema consolidado, automatizado e preparado para avaliação por rubrica técnica.
