<div align="center">

# Sistema Bancario FullStack

API REST bancaria desenvolvida com Java, Spring Boot, MySQL, Flyway e JDBC manual.

![Java](https://img.shields.io/badge/Java-21-red?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge&logo=mysql)
![Flyway](https://img.shields.io/badge/Flyway-Migrations-orange?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-Build-purple?style=for-the-badge&logo=apachemaven)

</div>

## Sumario

1. [Sobre o Projeto](#sobre-o-projeto)
2. [Funcionalidades](#funcionalidades)
3. [Tecnologias](#tecnologias)
4. [Arquitetura](#arquitetura)
5. [Modelo Relacional](#modelo-relacional)
6. [Endpoints](#endpoints)
7. [Regras de Negocio](#regras-de-negocio)
8. [Como Rodar](#como-rodar)
9. [Estrutura de Pastas](#estrutura-de-pastas)
10. [Aprendizados Demonstrados](#aprendizados-demonstrados)
11. [Proximos Passos](#proximos-passos)

## Sobre o Projeto

O **Sistema Bancario FullStack** e uma API REST que simula operacoes essenciais de uma conta bancaria. O projeto foi construido com foco em fundamentos de backend: arquitetura em camadas, modelagem relacional, SQL, validacoes de regra de negocio e persistencia com MySQL.

Apesar de o projeto possuir dependencias de JPA no ambiente, a persistencia atual foi implementada com **JDBC manual** usando `Connection`, `PreparedStatement` e `ResultSet`. Essa escolha foi feita para reforcar o entendimento do funcionamento interno da comunicacao entre Java e banco de dados antes de evoluir para Spring Data JPA.

## Funcionalidades

- Cadastro de clientes pessoa fisica.
- Cadastro de clientes pessoa juridica.
- Criacao automatica de conta bancaria ao cadastrar cliente.
- Busca de cliente por id.
- Atualizacao cadastral de clientes PF e PJ.
- Login por documento e senha.
- Deposito em conta bancaria.
- Saque com validacao de saldo.
- Registro de transacoes de deposito e saque.
- Cadastro de chaves PIX por conta bancaria.
- Relacionamento entre cliente, conta, transacoes e chaves PIX.

## Tecnologias

| Tecnologia | Uso no projeto |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot 4.1.0 | Inicializacao e estrutura da API |
| Spring Web MVC | Criacao dos endpoints REST |
| MySQL | Banco de dados relacional |
| Flyway | Versionamento das migrations |
| JDBC manual | Persistencia e consultas SQL |
| Maven | Gerenciamento de dependencias e build |
| Lombok | Reducao de boilerplate em DTOs/models |
| dotenv-java | Leitura de variaveis de ambiente |

## Arquitetura

O projeto segue uma arquitetura em camadas:

```text
Controller -> Service -> Repository -> Banco de Dados
```

### Controller

Recebe as requisicoes HTTP e encaminha os dados para a camada de servico.

Principais controllers:

- `ClienteController`
- `ContaBancariaController`
- `LoginController`
- `ChavePixController`

### Service

Contem as regras de negocio e validacoes.

Exemplos:

- Validacao de nome, telefone, endereco, UF e senha.
- Validacao de CPF e CNPJ.
- Validacao de deposito minimo.
- Validacao de saque minimo.
- Validacao de saldo suficiente.
- Validacao de tipo e valor de chave PIX.

### Repository

Responsavel pela comunicacao direta com o banco de dados usando JDBC manual.

Os repositories executam operacoes como:

- Inserir cliente.
- Buscar cliente por id ou documento.
- Criar conta bancaria.
- Atualizar saldo.
- Registrar transacao.
- Cadastrar chave PIX.

## Modelo Relacional

O banco e versionado com Flyway e possui as tabelas:

- `cliente`
- `contas`
- `transacoes`
- `chaves_pix`

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

Exemplo pratico:

```text
Cliente 1 possui a Conta 1.
Conta 1 possui transacoes.
Conta 1 possui chaves PIX.
```

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

Nos endpoints de deposito e saque, o parametro `{id}` representa o **id do cliente**. O service busca a conta vinculada a esse cliente.

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

## Regras de Negocio

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

### Variaveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
DB_URL=jdbc:mysql://localhost:3306/nome_do_banco
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
```

O projeto tambem aceita uma URL no formato `mysql://`, convertendo internamente para `jdbc:mysql://`.

### Executar

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

Ao iniciar a aplicacao, o Flyway executa as migrations automaticamente e prepara as tabelas.

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
- Integracao com MySQL local ou em ambiente cloud.

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

Funcionalidades testadas manualmente:

- Cadastro PF
- Cadastro PJ
- Atualizacao cadastral
- Login
- Deposito
- Saque
- Cadastro de chave PIX
- Persistencia em MySQL com relacionamento entre tabelas
