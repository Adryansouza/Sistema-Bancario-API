ALTER TABLE transacoes
    ADD COLUMN id_transacao VARCHAR(36) NULL AFTER id,
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'CONCLUIDA' AFTER tipo,
    ADD COLUMN nome_destinatario VARCHAR(150) NULL,
    ADD COLUMN documento_destinatario VARCHAR(20) NULL,
    ADD COLUMN chave_pix_destino VARCHAR(255) NULL,
    ADD COLUMN nome_remetente VARCHAR(150) NULL,
    ADD COLUMN documento_remetente VARCHAR(20) NULL;

UPDATE transacoes
SET id_transacao = UUID()
WHERE id_transacao IS NULL;

ALTER TABLE transacoes
    MODIFY COLUMN id_transacao VARCHAR(36) NOT NULL;

CREATE INDEX idx_transacoes_conta_data
    ON transacoes (conta_id, data_transacao);