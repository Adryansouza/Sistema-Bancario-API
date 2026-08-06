CREATE TABLE cliente
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_cliente VARCHAR(20)  NOT NULL,
    nome         VARCHAR(150) NOT NULL,
    documento    VARCHAR(20)  NOT NULL UNIQUE,
    telefone     VARCHAR(20),
    endereco     VARCHAR(100),
    senha        VARCHAR(8)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE contas (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        cliente_id BIGINT NOT NULL,
                        numero_conta VARCHAR(20) NOT NULL UNIQUE,
                        agencia VARCHAR(10),
                        saldo DECIMAL(15,2) NOT NULL DEFAULT 0.00,
                        status VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
                        data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE TABLE transacoes (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            conta_id BIGINT NOT NULL,
                            tipo VARCHAR(30) NOT NULL,
                            valor DECIMAL(15,2) NOT NULL,
                            descricao VARCHAR(255),
                            data_transacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            FOREIGN KEY (conta_id) REFERENCES contas(id)
);