-- Flyway migration: Create tokens table
-- Version: 1
-- Description: Cria tabela para armazenar tokens de autenticação do Mercado Livre

CREATE TABLE IF NOT EXISTS tokens (
    user_id VARCHAR(20) PRIMARY KEY,
    access_token VARCHAR(500) NOT NULL,
    refresh_token VARCHAR(500) NOT NULL,
    expires_in BIGINT,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices para melhor performance
CREATE INDEX idx_expires_at ON tokens(expires_at);

