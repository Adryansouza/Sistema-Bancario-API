package com.adryan.projetobanco.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.adryan.projetobanco.dto.TransferenciaPixRequest;
import com.adryan.projetobanco.dto.TransferenciaPixResponse;
import com.adryan.projetobanco.model.TipoChavepix;
import com.adryan.projetobanco.model.ChavePix;
import com.adryan.projetobanco.model.Cliente;
import com.adryan.projetobanco.model.ContaBancaria;
import com.adryan.projetobanco.model.StatusTransacao;
import com.adryan.projetobanco.model.TipoTransacao;
import com.adryan.projetobanco.model.Transacao;
import com.adryan.projetobanco.persistence.ConnectionUtil;
import com.adryan.projetobanco.repository.ChavePixRepository;
import com.adryan.projetobanco.repository.ClienteRepository;
import com.adryan.projetobanco.repository.ContasRepository;
import com.adryan.projetobanco.repository.TransacaoRepository;
import com.adryan.projetobanco.strategy.ValidadorChavePixFactory;

@Service
public class TransferenciaPixService {

    private static final BigDecimal VALOR_MINIMO_TRANSFERENCIA = new BigDecimal("1.00");
    private final ContasRepository contasRepository;
    private final ChavePixRepository chavePixRepository;
    private final ClienteRepository clienteRepository;
    private final TransacaoRepository transacaoRepository;
    private final ValidadorChavePixFactory validadorFactory;
    private final PasswordEncoder passwordEncoder;

    public TransferenciaPixService(ContasRepository contasRepository, ChavePixRepository chavePixRepository,
            ClienteRepository clienteRepository, TransacaoRepository transacaoRepository,
            ValidadorChavePixFactory validadorFactory, PasswordEncoder passwordEncoder) {
        this.contasRepository = contasRepository;
        this.chavePixRepository = chavePixRepository;
        this.clienteRepository = clienteRepository;
        this.transacaoRepository = transacaoRepository;
        this.validadorFactory = validadorFactory;
        this.passwordEncoder = passwordEncoder;
    }

    public TransferenciaPixResponse transferenciaPix(TransferenciaPixRequest request) {
        
        validarTransferenciaPix(request);
        TipoChavepix tipo = converterTipo(request.getTipoChavePix());
        String chaveNormalizada = validadorFactory.obter(tipo).validarENormalizar(request.getChavePixDestino());

        try (Connection connection = ConnectionUtil.conectar()) {
            connection.setAutoCommit(false);
            try {
                ContaBancaria origem = contasRepository.buscarContaPorContaID(connection, request.getContaId(), true);
                validarConta(origem, "origem");
                validarSenha(origem.getClienteId(), request.getSenha());

                ChavePix chave = chavePixRepository.buscarPorValorETipo(connection, chaveNormalizada, tipo);
                if (chave == null) {
                    throw new IllegalArgumentException("Chave PIX de destino nao encontrada.");
                }
                if (origem.getId().equals(chave.getContaId())) {
                    throw new IllegalArgumentException("A conta de destino deve ser diferente da conta de origem.");
                }

                ContaBancaria destino = contasRepository.buscarContaPorContaID(connection, chave.getContaId(), true);
                validarConta(destino, "destino");
                if (origem.getSaldo().compareTo(request.getValor()) < 0) {
                    throw new IllegalArgumentException("Saldo insuficiente.");
                }

                BigDecimal saldoOrigem = origem.getSaldo().subtract(request.getValor());
                BigDecimal saldoDestino = destino.getSaldo().add(request.getValor());
                contasRepository.atualizarSaldo(connection, origem.getId(), saldoOrigem);
                contasRepository.atualizarSaldo(connection, destino.getId(), saldoDestino);

                Cliente remetente = clienteRepository.buscarClientePorId(origem.getClienteId());
                Cliente destinatario = clienteRepository.buscarClientePorId(destino.getClienteId());
                String idTransacao = UUID.randomUUID().toString();

                Transacao pixEnviado = criarRegistroPix(
                        origem.getId(), idTransacao, TipoTransacao.PIX_ENVIADO,
                        request.getValor(), chaveNormalizada, remetente, destinatario);
                Transacao pixRecebido = criarRegistroPix(
                        destino.getId(), idTransacao, TipoTransacao.PIX_RECEBIDO,
                        request.getValor(), chaveNormalizada, remetente, destinatario);

                transacaoRepository.registrarTransacao(connection, pixEnviado);
                transacaoRepository.registrarTransacao(connection, pixRecebido);
                connection.commit();
                return new TransferenciaPixResponse("Transferencia PIX realizada com sucesso.", saldoOrigem);
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao realizar transferencia PIX.", e);
        }
    }

    private void validarTransferenciaPix(TransferenciaPixRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Os campos nao podem estar vazios.");
        }

        if (request.getContaId() == null || request.getContaId() <= 0) {
            throw new IllegalArgumentException("A conta de origem e obrigatoria.");
        }

        if (request.getChavePixDestino() == null || request.getChavePixDestino().isBlank()) {
            throw new IllegalArgumentException("A chave Pix destino e obrigatoria.");
        }

        if (request.getTipoChavePix() == null || request.getTipoChavePix().isBlank()) {
            throw new IllegalArgumentException("O tipo da chave Pix e obrigatorio.");
        }

        try {
            TipoChavepix.valueOf(request.getTipoChavePix().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de chave Pix invalido. Use CPF, EMAIL ou TELEFONE.");
        }

        if (request.getValor() == null) {
            throw new IllegalArgumentException("O valor da transferencia e obrigatorio.");
        }

        if (request.getValor().compareTo(VALOR_MINIMO_TRANSFERENCIA) < 0) {
            throw new IllegalArgumentException("A transferencia minima e de R$ 1,00.");
        }

        if (request.getSenha() == null || request.getSenha().isBlank()) {
            throw new IllegalArgumentException("A senha e obrigatoria.");
        }
    }

    private TipoChavepix converterTipo(String tipo) {
        return TipoChavepix.valueOf(tipo.trim().toUpperCase());
    }

    private void validarConta(ContaBancaria conta, String papel) {
        if (conta == null) {
            throw new IllegalArgumentException("Conta de " + papel + " nao encontrada.");
        }
        if (!"ATIVA".equalsIgnoreCase(conta.getStatus())) {
            throw new IllegalArgumentException("Conta de " + papel + " nao esta ativa.");
        }
    }

    private void validarSenha(Long clienteId, String senha) throws SQLException {
        Cliente cliente = clienteRepository.buscarClientePorId(clienteId);
        String senhaArmazenada = cliente == null ? null : cliente.getSenha();
        boolean senhaValida = senhaArmazenada != null && (senhaArmazenada.startsWith("$2")
                ? passwordEncoder.matches(senha, senhaArmazenada)
                : senha.equals(senhaArmazenada));
        if (!senhaValida) {
            throw new IllegalArgumentException("Senha incorreta.");
        }
        if (!senhaArmazenada.startsWith("$2")) {
            clienteRepository.atualizarSenha(cliente.getDocumento(), passwordEncoder.encode(senha));
        }
    }

    private Transacao criarRegistroPix(
            Long contaId,
            String idTransacao,
            TipoTransacao tipo,
            BigDecimal valor,
            String chavePixDestino,
            Cliente remetente,
            Cliente destinatario) {
        String descricao = tipo == TipoTransacao.PIX_ENVIADO
                ? "PIX enviado para " + destinatario.getNome()
                : "PIX recebido de " + remetente.getNome();

        return Transacao.builder()
                .contaId(contaId)
                .idTransacao(idTransacao)
                .tipo(tipo)
                .status(StatusTransacao.CONCLUIDA)
                .valor(valor)
                .descricao(descricao)
                .nomeDestinatario(destinatario.getNome())
                .documentoDestinatario(destinatario.getDocumento())
                .chavePixDestino(chavePixDestino)
                .nomeRemetente(remetente.getNome())
                .documentoRemetente(remetente.getDocumento())
                .build();
    }
}
