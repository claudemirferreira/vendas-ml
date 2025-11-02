-- Script de inicialização do banco de dados
-- Este arquivo é executado automaticamente quando o container MySQL é criado pela primeira vez

-- Garantir que o banco existe (já criado pelo MYSQL_DATABASE)
USE mercadolivre_db;

-- Definir charset e collation
ALTER DATABASE mercadolivre_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- A tabela tokens será criada automaticamente pelo Hibernate com ddl-auto=update
-- Mas podemos criar aqui também se necessário

