# Pendências do backend para completar o front-end

## Transações e extrato

- A listagem básica já existe em `GET /conta/{contaId}/transacoes`.
- Adicionar filtros de período, categoria, tipo e texto no backend para grandes volumes.
- Adicionar exportação de extrato em PDF/CSV.
- Incluir dados estruturados da contraparte nas transferências, em vez de depender apenas da descrição.

## Agendamentos

- Criar, listar, alterar e cancelar transferências agendadas.
- Sugestão: `POST/GET /agendamentos`, `PATCH/DELETE /agendamentos/{id}`.

## Chaves PIX

- A listagem por conta já existe em `GET /cadastroChave/{contaId}`.
- `DELETE /chaves-pix/{id}` para excluir uma chave.
- Endpoint para consultar/validar uma chave e mostrar o destinatário antes da confirmação.
- Avaliar suporte a CNPJ e chave aleatória, pois hoje o backend aceita somente CPF, e-mail e telefone.

## Recebimento e QR Code

- Endpoint para gerar payload PIX Copia e Cola e QR Code.
- Endpoint para gerar dados de cobrança/recebimento, opcionalmente com valor e descrição.

## Conta e perfil

- Endpoint específico de consulta da conta/saldo para atualização periódica sem recarregar todo o cliente.
- Corrigir a atualização de UF de pessoa jurídica em `CadastroService.atualizarCadastroPj`.
- Avaliar endpoints para encerrar/bloquear conta e recuperação de senha.

## Segurança

- Autenticação com token e autorização por usuário; atualmente ids de cliente/conta vêm do front-end.
- Não confiar em `contaId` informado pelo navegador para transferência PIX.
- Rate limit e expiração/bloqueio após tentativas inválidas de senha.

## Busca e notificações

- Busca global, caso o botão de pesquisa deva pesquisar transações/beneficiários.
- Endpoint de notificações e indicador de itens não lidos, caso essa seção seja adicionada.
