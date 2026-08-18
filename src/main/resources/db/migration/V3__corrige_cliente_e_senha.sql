SET @adicionar_uf = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE cliente ADD COLUMN uf CHAR(2) NULL',
        'SELECT 1'
    )
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'cliente'
      AND column_name = 'uf'
);

PREPARE adicionar_uf_stmt FROM @adicionar_uf;
EXECUTE adicionar_uf_stmt;
DEALLOCATE PREPARE adicionar_uf_stmt;

ALTER TABLE cliente MODIFY COLUMN senha VARCHAR(60) NOT NULL;
