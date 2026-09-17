CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,
                          nome VARCHAR(100) NOT NULL,
                          email VARCHAR(150) NOT NULL UNIQUE,
                          senha VARCHAR(255) NOT NULL,
                          cep VARCHAR(8),
                          cidade VARCHAR(100),
                          uf VARCHAR(2),
                          role VARCHAR(20) NOT NULL
);

CREATE TABLE servicos (
                          id BIGSERIAL PRIMARY KEY,
                          prestador_id BIGINT NOT NULL,
                          titulo VARCHAR(150) NOT NULL,
                          descricao VARCHAR(500) NOT NULL,
                          categoria VARCHAR(100) NOT NULL,
                          preco NUMERIC(10, 2) NOT NULL,
                          situacao VARCHAR(20) NOT NULL,

                          CONSTRAINT fk_servico_prestador
                              FOREIGN KEY (prestador_id)
                                  REFERENCES usuarios(id)
);

CREATE TABLE contratacoes (
                              id BIGSERIAL PRIMARY KEY,
                              servico_id BIGINT NOT NULL,
                              contratante_id BIGINT NOT NULL,
                              situacao VARCHAR(20) NOT NULL,

                              CONSTRAINT fk_contratacao_servico
                                  FOREIGN KEY (servico_id)
                                      REFERENCES servicos(id),

                              CONSTRAINT fk_contratacao_contratante
                                  FOREIGN KEY (contratante_id)
                                      REFERENCES usuarios(id)
);