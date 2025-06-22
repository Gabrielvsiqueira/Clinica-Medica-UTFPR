CREATE DATABASE IF NOT EXISTS clinica;
USE clinica;

CREATE TABLE Paciente (
                          paciente_id INT AUTO_INCREMENT PRIMARY KEY,
                          nome VARCHAR(100),
                          foto VARCHAR(255), -- ALTERADO de BLOB para VARCHAR para caminho/URL da foto
                          data_nascimento DATETIME,
                          sexo VARCHAR(50),
                          endereco VARCHAR(200),
                          telefone VARCHAR(20), -- ALTERADO de INT para VARCHAR
                          pagamento VARCHAR(50)
);

CREATE TABLE Especialidade (
                               cod_especialidade INT AUTO_INCREMENT PRIMARY KEY,
                               especialidade VARCHAR(100) UNIQUE -- Adicionado UNIQUE para nome da especialidade
);

CREATE TABLE Medico (
                        medico_id INT AUTO_INCREMENT PRIMARY KEY,
                        crm INT UNIQUE, -- Adicionado UNIQUE para CRM
                        nome VARCHAR(400),
                        endereco VARCHAR(200),
                        telefone VARCHAR(20), -- ALTERADO de INT para VARCHAR
                        fk_especialidade INT,
                        FOREIGN KEY(fk_especialidade) REFERENCES Especialidade(cod_especialidade)
);

CREATE TABLE Agendar_Consulta (
                                  id_consulta INT AUTO_INCREMENT PRIMARY KEY,
                                  fk_paciente INT NOT NULL,
                                  fk_medico INT NOT NULL,
                                  horario_consulta DATETIME NOT NULL,
                                  status VARCHAR(30),
                                  FOREIGN KEY (fk_medico) REFERENCES Medico(medico_id),
                                  FOREIGN KEY(fk_paciente) REFERENCES Paciente(paciente_id),
                                  CONSTRAINT uk_medico_horario UNIQUE (fk_medico, horario_consulta) -- Renomeado para clareza
);

CREATE TABLE Exames (
                        cod_exame INT AUTO_INCREMENT PRIMARY KEY,
                        nome VARCHAR(100) UNIQUE, -- Adicionado UNIQUE para nome do exame
                        valor DECIMAL(10,2), -- ALTERADO de DOUBLE para DECIMAL
                        orientacoes VARCHAR(800)
);

CREATE TABLE Controle_Exames (
                                 agendamento_id INT AUTO_INCREMENT PRIMARY KEY,
                                 fk_exames INT,
                                 fk_paciente INT,
                                 fk_medico INT, -- Este é o médico requisitante
                                 data_realizacao DATETIME NOT NULL,
                                 valor_exame DECIMAL(10,2),
                                 status_exame VARCHAR(30),
                                 FOREIGN KEY (fk_exames) REFERENCES Exames(cod_exame),
                                 FOREIGN KEY (fk_paciente) REFERENCES Paciente(paciente_id),
                                 FOREIGN KEY (fk_medico) REFERENCES Medico(medico_id),
                                 CONSTRAINT uk_paciente_exame_horario UNIQUE (fk_paciente, data_realizacao) -- Renomeado para clareza
);