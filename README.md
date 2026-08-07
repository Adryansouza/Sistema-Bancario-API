# Sistema Bancario FullStack

API REST de um sistema bancario desenvolvida em Java com Spring Boot, MySQL e Flyway. O projeto simula operacoes essenciais de uma conta bancaria, incluindo cadastro de clientes, criacao automatica de conta, login, deposito, saque, historico de transacoes e cadastro de chaves PIX.

> Projeto em evolucao, criado com foco em consolidar fundamentos de backend, arquitetura em camadas, persistencia com SQL e integracao com banco relacional antes da migracao para Spring Data JPA.

## Objetivo

O objetivo deste projeto e construir uma API bancaria do zero, entendendo de forma pratica como uma aplicacao backend organiza regras de negocio, acesso ao banco, validacoes e endpoints HTTP.

Atualmente o sistema permite:

- Cadastrar clientes pessoa fisica e pessoa juridica.
- Criar automaticamente uma conta bancaria para cada cliente cadastrado.
- Buscar clientes por id.
- Atualizar dados cadastrais de clientes PF e PJ.
- Realizar login por documento e senha.
- Realizar deposito em conta.
- Realizar saque com validacao de saldo.
- Registrar transacoes de deposito e saque no banco.
- Cadastrar chaves PIX por conta bancaria.
- Relacionar clientes, contas, transacoes e chaves PIX por chaves estrangeiras.

## Tecnologias

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- MySQL
- Flyway
- JDBC manual
- Maven
- Lombok
- dotenv-java

## Arquitetura

O projeto segue uma separacao simples em camadas:

```text
Controller -> Service -> Repository -> Banco de Dados
```

### Controller

Responsavel por receber as requisicoes HTTP e encaminhar os dados para a camada de servico.

Exemplos:

- `ClienteController`
- `ContaBancariaController`
- `LoginController`
- `ChavePixController`

### Service

Responsavel pelas regras de negocio e validacoes.

Exemplos:

- Validar dados obrigatorios de cadastro.
- Validar CPF, CNPJ, telefone, UF e senha.
- Validar valor minimo de deposito e saque.
- Validar saldo suficiente antes de sacar.
- Validar tipo e valor de chave PIX.

### Repository

Responsavel pela comunicacao direta com o MySQL usando JDBC manual.

O projeto ainda nao usa Spring Data JPA nos repositories. A persistencia foi implementada com `Connection`, `PreparedStatement` e `ResultSet` para reforcar o entendimento de SQL, conexao com banco e mapeamento manual dos dados.

### Migrations

O banco e versionado com Flyway:

- `V1__create_table_banco.sql`: cria as tabelas principais de clientes, contas e transacoes.
- `V2__create_table_chaves_pix.sql`: cria a tabela de chaves PIX.

## Modelo Relacional

Relacionamento principal:

```text
cliente 1 -> 1 conta
conta   1 -> N transacoes
conta   1 -> N chaves_pix
```

Fluxo dos identificadores:

```text
cliente.id
   -> contas.cliente_id

contas.id
   -> transacoes.conta_id
   -> chaves_pix.conta_id
```

Em termos de negocio:

- Um cliente possui uma conta bancaria.
- Uma conta pode ter varias transacoes.
- Uma conta pode ter varias chaves PIX.
- Uma chave PIX sempre pertence a uma conta existente.

## Endpoints

Base local:

```text
http://localhost:8080
```

### Clientes

#### Cadastrar pessoa fisica

```http
POST /clientes/cadastro/pf
```

```json
{
  "nome": "Adryan Silva",
  "cpf": "12345678901",
  "telefone": "11999998888",
  "endereco": "Rua das Flores, 123",
  "senha": "12345678",
  "uf": "SP"
}
```

#### Cadastrar pessoa juridica

```http
POST /clientes/cadastro/pj
```

```json
{
  "nome": "Empresa Exemplo LTDA",
  "cnpj": "11222333000181",
  "telefone": "11988887777",
  "endereco": "Avenida Java, 456",
  "senha": "87654321",
  "uf": "MG"
}
```

#### Buscar cliente por id

```http
GET /clientes/{id}
```

#### Atualizar pessoa fisica

```http
PATCH /clientes/pf/{id}
```

```json
{
  "nome": "Adryan Silva Atualizado",
  "telefone": "11977776666",
  "endereco": "Rua Atualizada, 999",
  "senha": "12345678",
  "uf": "SP"
}
```

#### Atualizar pessoa juridica

```http
PATCH /clientes/pj/{id}
```

```json
{
  "nome": "Empresa Exemplo Atualizada",
  "telefone": "11966665555",
  "endereco": "Avenida Atualizada, 1000",
  "senha": "87654321"
}
```

### Login

```http
POST /login/pf
```

```json
{
  "documento": "12345678901",
  "senha": "12345678"
}
```

### Conta Bancaria

> Nos endpoints de deposito e saque, o parametro `{id}` representa o id do cliente. O service busca a conta vinculada a esse cliente.

#### Deposito

```http
POST /conta/deposito/{id}
```

```json
{
  "valor": 100.00
}
```

#### Saque

```http
POST /conta/saque/{id}
```

```json
{
  "valor": 25.50
}
```

### Chave PIX

#### Cadastrar chave PIX

```http
POST /cadastroChave
```

```json
{
  "conta_id": 1,
  "tipo_chave": "EMAIL",
  "valor_chave": "adryan@email.com"
}
```

Tipos aceitos:

```text
CPF
EMAIL
TELEFONE
```

Exemplo com CPF:

```json
{
  "conta_id": 1,
  "tipo_chave": "CPF",
  "valor_chave": "12345678901"
}
```

Exemplo com telefone:

```json
{
  "conta_id": 1,
  "tipo_chave": "TELEFONE",
  "valor_chave": "11999998888"
}
```

## Regras de Negocio Implementadas

- Cliente PF deve possuir nome, CPF, telefone, endereco, senha e UF validos.
- Cliente PJ deve possuir nome, CNPJ, telefone, endereco e senha validos.
- Senha deve conter exatamente 8 numeros.
- Deposito minimo: R$ 1,00.
- Saque minimo: R$ 1,00.
- Saque exige saldo suficiente.
- Conta bancaria precisa estar ativa para movimentacoes.
- Chave PIX precisa estar vinculada a uma conta existente.
- Tipo de chave PIX deve ser `CPF`, `EMAIL` ou `TELEFONE`.
- Valor da chave PIX e unico no banco.

## Como Rodar

### Pre-requisitos

- Java 21
- MySQL
- Maven ou Maven Wrapper
- Variaveis de ambiente configuradas

### Configuracao do banco

Crie um arquivo `.env` na raiz do projeto:

```env
DB_URL=jdbc:mysql://localhost:3306/nome_do_banco
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
```

O projeto tambem aceita uma URL no formato `mysql://`, convertendo internamente para `jdbc:mysql://`.

### Executar

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

Ao iniciar, o Flyway executa as migrations automaticamente e prepara as tabelas do banco.

## Estrutura de Pastas

```text
src/main/java/com/adryan/projetobanco
  controller   -> endpoints REST
  dto          -> objetos de entrada e saida da API
  model        -> modelos de dominio
  persistence  -> configuracao e conexao com banco
  repository   -> acesso ao banco com JDBC manual
  service      -> regras de negocio

src/main/resources
  db/migration -> scripts Flyway
```

## Aprendizados Demonstrados

Este projeto demonstra conhecimento pratico em:

- Criacao de APIs REST com Spring Boot.
- Organizacao em camadas.
- Modelagem de banco relacional.
- Uso de chaves primarias e estrangeiras.
- Versionamento de schema com Flyway.
- Manipulacao de dados com JDBC manual.
- Validacao de regras de negocio na camada de service.
- Separacao entre DTO, model, repository e controller.
- Integracao com MySQL em ambiente local ou cloud.

## Proximos Passos

- Criar endpoint de extrato.
- Implementar busca de chave PIX por valor.
- Implementar transferencia PIX entre contas.
- Registrar transacoes de PIX para conta origem e destino.
- Melhorar respostas de erro com status HTTP adequados.
- Criar DTOs de response para evitar retorno de dados sensiveis, como senha.
- Adicionar testes automatizados.
- Evoluir a persistencia para Spring Data JPA em uma etapa futura.

## Status

Funcionalidades atuais testadas manualmente:

- Cadastro PF
- Cadastro PJ
- Atualizacao cadastral
- Login
- Deposito
- Saque
- Cadastro de chave PIX
- Persistencia em MySQL com relacionamento entre tabelas
