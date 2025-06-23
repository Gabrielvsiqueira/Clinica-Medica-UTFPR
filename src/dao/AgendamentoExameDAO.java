package dao;

import entities.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoExameDAO {

    public void inserir(AgendamentoExame agendamento) {
        String sql = "INSERT INTO Controle_Exames (fk_exames, fk_paciente, fk_medico, data_realizacao, valor_exame, status_exame) VALUES (?, ?, ?, ?, ?, ?)"; // Ajustado
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             stmt.setInt(1, agendamento.getExame().getId()); // Mapeia para fk_exames
             stmt.setInt(2, agendamento.getPaciente().getId()); // Mapeia para fk_paciente
            if (agendamento.getMedicoRequisitante() != null) {
                stmt.setInt(3, agendamento.getMedicoRequisitante().getId()); // Mapeia para fk_medico
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setTimestamp(4, Timestamp.valueOf(agendamento.getDataRealizacao()));
            stmt.setBigDecimal(5, agendamento.getValorPago()); // Mapeia para valor_exame
            stmt.setString(6, agendamento.getStatus()); // Mapeia para status_exame
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        agendamento.setId(rs.getInt(1)); // agendamento_id é o ID
                    }
                }
                System.out.println("Agendamento de exame inserido com sucesso! ID: " + agendamento.getId());
            } else {
                System.out.println("Nenhuma linha afetada ao inserir agendamento de exame.");
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao inserir agendamento de exame: " + e.getMessage());
            e.printStackTrace();
            if (e instanceof SQLException && ((SQLException) e).getSQLState().startsWith("23")) { // 23 é o SQLState para violação de integridade
                System.err.println("Possível sobreposição de agendamento de exame para o paciente neste horário.");
            }
        }
    }

    public AgendamentoExame buscarPorId(Integer id) {
        String sql = "SELECT ae.agendamento_id, ae.data_realizacao, ae.valor_exame, ae.status_exame, " + // Ajustado
                "e.cod_exame AS exame_id, e.nome AS exame_nome, e.valor AS exame_valor, e.orientacoes, " + // Ajustado Exames
                "p.paciente_id AS paciente_id, p.nome AS paciente_nome, p.foto, p.data_nascimento, p.sexo, p.endereco, p.telefone, p.pagamento, " + // Ajustado Paciente
                "mr.medico_id AS medico_req_id, mr.crm AS medico_req_crm, mr.nome AS medico_req_nome, mr.endereco AS medico_req_endereco, mr.telefone AS medico_req_telefone, " + // Ajustado Medico
                "esp.cod_especialidade AS medico_req_especialidade_id, esp.especialidade AS medico_req_especialidade_nome " + // Ajustado Especialidade
                "FROM Controle_Exames ae " + // Ajustado nome da tabela
                "INNER JOIN Exames e ON ae.fk_exames = e.cod_exame " + // Ajustado
                "INNER JOIN Paciente p ON ae.fk_paciente = p.paciente_id " + // Ajustado
                "LEFT JOIN Medico mr ON ae.fk_medico = mr.medico_id " + // Ajustado
                "LEFT JOIN Especialidade esp ON mr.fk_especialidade = esp.cod_especialidade " + // Ajustado
                "WHERE ae.agendamento_id = ?";
        AgendamentoExame agendamento = null;
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    agendamento = new AgendamentoExame();
                    agendamento.setId(rs.getInt("agendamento_id"));
                    agendamento.setDataRealizacao(rs.getTimestamp("data_realizacao").toLocalDateTime());
                    agendamento.setValorPago(rs.getBigDecimal("valor_exame")); // Mapeia valor_exame para valorPago
                    agendamento.setStatus(rs.getString("status_exame")); // Mapeia status_exame para status

                    Exame exame = new Exame(
                            rs.getInt("exame_id"),
                            rs.getString("exame_nome"),
                            rs.getBigDecimal("exame_valor"),
                            rs.getString("orientacoes")
                    );
                    agendamento.setExame(exame);

                    Paciente paciente = new Paciente(
                            rs.getInt("paciente_id"),
                            rs.getString("paciente_nome"),
                            rs.getString("foto"),
                            rs.getDate("data_nascimento").toLocalDate(),
                            rs.getString("sexo"),
                            rs.getString("endereco"),
                            rs.getString("telefone"),
                            rs.getString("pagamento")
                    );
                    agendamento.setPaciente(paciente);

                    if (rs.getObject("medico_req_id") != null) {
                        Especialidade especialidadeReq = new Especialidade(
                                rs.getInt("medico_req_especialidade_id"),
                                rs.getString("medico_req_especialidade_nome")
                        );
                        Medico medicoRequisitante = new Medico(
                                rs.getInt("medico_req_id"),
                                rs.getInt("medico_req_crm"),
                                rs.getString("medico_req_nome"),
                                rs.getString("medico_req_endereco"),
                                rs.getString("medico_req_telefone"),
                                especialidadeReq
                        );
                        agendamento.setMedicoRequisitante(medicoRequisitante);
                    }
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar agendamento de exame por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return agendamento;
    }

    public List<AgendamentoExame> buscarPorExameEData(Integer exameId, LocalDateTime data) {
        String sql = "SELECT ae.agendamento_id, ae.data_realizacao, ae.valor_exame, ae.status_exame, " +
                "e.cod_exame AS exame_id, e.nome AS exame_nome, e.valor AS exame_valor, e.orientacoes, " +
                "p.paciente_id AS paciente_id, p.nome AS paciente_nome, p.foto, p.data_nascimento, p.sexo, p.endereco, p.telefone, p.pagamento, " +
                "mr.medico_id AS medico_req_id, mr.crm AS medico_req_crm, mr.nome AS medico_req_nome, mr.endereco AS medico_req_endereco, mr.telefone AS medico_req_telefone, " +
                "esp.cod_especialidade AS medico_req_especialidade_id, esp.especialidade AS medico_req_especialidade_nome " +
                "FROM Controle_Exames ae " +
                "INNER JOIN Exames e ON ae.fk_exames = e.cod_exame " +
                "INNER JOIN Paciente p ON ae.fk_paciente = p.paciente_id " +
                "LEFT JOIN Medico mr ON ae.fk_medico = mr.medico_id " +
                "LEFT JOIN Especialidade esp ON mr.fk_especialidade = esp.cod_especialidade " +
                "WHERE ae.fk_exames = ? AND DATE(ae.data_realizacao) = ? ORDER BY ae.data_realizacao"; // Ajustado
        List<AgendamentoExame> agendamentos = new ArrayList<>();
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, exameId);
            stmt.setDate(2, java.sql.Date.valueOf(data.toLocalDate()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AgendamentoExame agendamento = new AgendamentoExame();
                    agendamento.setId(rs.getInt("agendamento_id"));
                    agendamento.setDataRealizacao(rs.getTimestamp("data_realizacao").toLocalDateTime());
                    agendamento.setValorPago(rs.getBigDecimal("valor_exame"));
                    agendamento.setStatus(rs.getString("status_exame"));

                    Exame exame = new Exame(
                            rs.getInt("exame_id"),
                            rs.getString("exame_nome"),
                            rs.getBigDecimal("exame_valor"),
                            rs.getString("orientacoes")
                    );
                    agendamento.setExame(exame);

                    Paciente paciente = new Paciente(
                            rs.getInt("paciente_id"),
                            rs.getString("paciente_nome"),
                            rs.getString("foto"),
                            rs.getDate("data_nascimento").toLocalDate(),
                            rs.getString("sexo"),
                            rs.getString("endereco"),
                            rs.getString("telefone"),
                            rs.getString("pagamento")
                    );
                    agendamento.setPaciente(paciente);

                    if (rs.getObject("medico_req_id") != null) {
                        Especialidade especialidadeReq = new Especialidade(
                                rs.getInt("medico_req_especialidade_id"),
                                rs.getString("medico_req_especialidade_nome")
                        );
                        Medico medicoRequisitante = new Medico(
                                rs.getInt("medico_req_id"),
                                rs.getInt("medico_req_crm"),
                                rs.getString("medico_req_nome"),
                                rs.getString("medico_req_endereco"),
                                rs.getString("medico_req_telefone"),
                                especialidadeReq
                        );
                        agendamento.setMedicoRequisitante(medicoRequisitante);
                    }
                    agendamentos.add(agendamento);
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar agendamentos de exame por exame e data: " + e.getMessage());
            e.printStackTrace();
        }
        return agendamentos;
    }

    public void atualizar(AgendamentoExame agendamento) {
        String sql = "UPDATE Controle_Exames SET fk_exames = ?, fk_paciente = ?, fk_medico = ?, data_realizacao = ?, valor_exame = ?, status_exame = ? WHERE agendamento_id = ?"; // Ajustado
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setInt(1, agendamento.getExame().getId());
            stmt.setInt(2, agendamento.getPaciente().getId());
            if (agendamento.getMedicoRequisitante() != null) {
                stmt.setInt(3, agendamento.getMedicoRequisitante().getId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setTimestamp(4, Timestamp.valueOf(agendamento.getDataRealizacao()));
            stmt.setBigDecimal(5, agendamento.getValorPago());
            stmt.setString(6, agendamento.getStatus());
            stmt.setInt(7, agendamento.getId());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Agendamento de exame atualizado com sucesso! ID: " + agendamento.getId());
            } else {
                System.out.println("Nenhum agendamento de exame encontrado com o ID: " + agendamento.getId());
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao atualizar agendamento de exame: " + e.getMessage());
            e.printStackTrace();
            if (e instanceof SQLException && ((SQLException) e).getSQLState().startsWith("23")) {
                System.err.println("Possível sobreposição de agendamento de exame para o paciente neste horário.");
            }
        }
    }

    public void deletar(Integer id) {
        String sql = "DELETE FROM Controle_Exames WHERE agendamento_id = ?"; // Ajustado
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Agendamento de exame deletado com sucesso! ID: " + id);
            } else {
                System.out.println("Nenhum agendamento de exame encontrado com o ID: " + id);
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao deletar agendamento de exame: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean existeSobreposicao(Integer pacienteId, LocalDateTime inicio, LocalDateTime fim, Integer agendamentoIdExcluir) {
        String sql = "SELECT COUNT(*) FROM Controle_Exames " +
                "WHERE fk_paciente = ? AND data_realizacao BETWEEN ? AND ? " +
                "AND status_exame != 'Cancelado'";

        if (agendamentoIdExcluir != null) {
            sql += " AND agendamento_id != ?";
        }
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pacienteId);
            stmt.setTimestamp(2, Timestamp.valueOf(inicio));
            stmt.setTimestamp(3, Timestamp.valueOf(fim));
            if (agendamentoIdExcluir != null) {
                stmt.setInt(4, agendamentoIdExcluir);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao verificar sobreposição de agendamento de exame: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean existeSobreposicao(Integer pacienteId, LocalDateTime dataRealizacao) {
        LocalDateTime fimEstimado = dataRealizacao.plusMinutes(60);
        return existeSobreposicao(pacienteId, dataRealizacao, fimEstimado, null);
    }

    public List<AgendamentoExame> buscarHistoricoPaciente(Integer pacienteId) {
        String sql = "SELECT ae.agendamento_id, ae.data_realizacao, ae.valor_exame, ae.status_exame, " +
                "e.cod_exame AS exame_id, e.nome AS exame_nome, e.valor AS exame_valor, e.orientacoes, " +
                "p.paciente_id AS paciente_id, p.nome AS paciente_nome, p.foto, p.data_nascimento, p.sexo, p.endereco, p.telefone, p.pagamento, " +
                "mr.medico_id AS medico_req_id, mr.crm AS medico_req_crm, mr.nome AS medico_req_nome, mr.endereco AS medico_req_endereco, mr.telefone AS medico_req_telefone, " +
                "esp.cod_especialidade AS medico_req_especialidade_id, esp.especialidade AS medico_req_especialidade_nome " +
                "FROM Controle_Exames ae " +
                "INNER JOIN Exames e ON ae.fk_exames = e.cod_exame " +
                "INNER JOIN Paciente p ON ae.fk_paciente = p.paciente_id " +
                "LEFT JOIN Medico mr ON ae.fk_medico = mr.medico_id " +
                "LEFT JOIN Especialidade esp ON mr.fk_especialidade = esp.cod_especialidade " +
                "WHERE ae.fk_paciente = ? ORDER BY ae.data_realizacao DESC";
        List<AgendamentoExame> agendamentos = new ArrayList<>();
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setInt(1, pacienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AgendamentoExame agendamento = new AgendamentoExame();
                    agendamento.setId(rs.getInt("agendamento_id"));
                    agendamento.setDataRealizacao(rs.getTimestamp("data_realizacao").toLocalDateTime());
                    agendamento.setValorPago(rs.getBigDecimal("valor_exame"));
                    agendamento.setStatus(rs.getString("status_exame"));

                    Exame exame = new Exame(
                            rs.getInt("exame_id"),
                            rs.getString("exame_nome"),
                            rs.getBigDecimal("exame_valor"),
                            rs.getString("orientacoes")
                    );
                    agendamento.setExame(exame);

                    Paciente paciente = new Paciente(
                            rs.getInt("paciente_id"),
                            rs.getString("paciente_nome"),
                            rs.getString("foto"),
                            rs.getDate("data_nascimento").toLocalDate(),
                            rs.getString("sexo"),
                            rs.getString("endereco"),
                            rs.getString("telefone"),
                            rs.getString("pagamento")
                    );
                    agendamento.setPaciente(paciente);

                    if (rs.getObject("medico_req_id") != null) {
                        Especialidade especialidadeReq = new Especialidade(
                                rs.getInt("medico_req_especialidade_id"),
                                rs.getString("medico_req_especialidade_nome")
                        );
                        Medico medicoRequisitante = new Medico(
                                rs.getInt("medico_req_id"),
                                rs.getInt("medico_req_crm"),
                                rs.getString("medico_req_nome"),
                                rs.getString("medico_req_endereco"),
                                rs.getString("medico_req_telefone"),
                                especialidadeReq
                        );
                        agendamento.setMedicoRequisitante(medicoRequisitante);
                    }
                    agendamentos.add(agendamento);
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar histórico de agendamentos de exames do paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return agendamentos;
    }
}