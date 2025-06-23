package dao;

import entities.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoExameDAO {

    public void cadastrarAgendamento(AgendamentoExame agendamento) {
        String sql = "INSERT INTO Controle_Exames (fk_exames, fk_paciente, fk_medico, data_realizacao, valor_exame, status_exame) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherStatementAgendamento(stmt, agendamento);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0 && stmt.getGeneratedKeys().next()) {
                agendamento.setId(stmt.getGeneratedKeys().getInt(1));
            }
        } catch (SQLException | IOException e) {
            tratarErro(e, "inserir agendamento de exame");
        }
    }

    public AgendamentoExame buscarAgendamentoId(Integer id) {
        String sql = "SELECT ... WHERE ae.agendamento_id = ?"; // mesma query grande anterior
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? mapearAgendamento(rs) : null;

        } catch (SQLException | IOException e) {
            tratarErro(e, "buscar agendamento por ID");
            return null;
        }
    }

    public List<AgendamentoExame> buscarPorExameEData(Integer exameId, LocalDateTime data) {
        String sql = "SELECT ... WHERE ae.fk_exames = ? AND DATE(ae.data_realizacao) = ?";
        List<AgendamentoExame> agendamentos = new ArrayList<>();
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, exameId);
            stmt.setDate(2, Date.valueOf(data.toLocalDate()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) agendamentos.add(mapearAgendamento(rs));

        } catch (SQLException | IOException e) {
            tratarErro(e, "buscar por exame e data");
        }
        return agendamentos;
    }

    public void atualizarAgendamento(AgendamentoExame agendamento) {
        String sql = "UPDATE Controle_Exames SET fk_exames = ?, fk_paciente = ?, fk_medico = ?, data_realizacao = ?, valor_exame = ?, status_exame = ? WHERE agendamento_id = ?";
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preencherStatementAgendamento(stmt, agendamento);
            stmt.setInt(7, agendamento.getId());
            stmt.executeUpdate();

        } catch (SQLException | IOException e) {
            tratarErro(e, "atualizar agendamento");
        }
    }

    public void deletarAgendamento(Integer id) {
        String sql = "DELETE FROM Controle_Exames WHERE agendamento_id = ?";
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException | IOException e) {
            tratarErro(e, "deletar agendamento");
        }
    }

    public boolean existeSobreposicao(Integer pacienteId, LocalDateTime inicio, LocalDateTime fim, Integer agendamentoIdExcluir) {
        String sql = "SELECT COUNT(*) FROM Controle_Exames WHERE fk_paciente = ? AND data_realizacao BETWEEN ? AND ? AND status_exame != 'Cancelado'" +
                (agendamentoIdExcluir != null ? " AND agendamento_id != ?" : "");

        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pacienteId);
            stmt.setTimestamp(2, Timestamp.valueOf(inicio));
            stmt.setTimestamp(3, Timestamp.valueOf(fim));
            if (agendamentoIdExcluir != null) stmt.setInt(4, agendamentoIdExcluir);

            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException | IOException e) {
            tratarErro(e, "verificar sobreposição de agendamento");
            return false;
        }
    }

    public boolean existeSobreposicao(Integer pacienteId, LocalDateTime data) {
        return existeSobreposicao(pacienteId, data, data.plusMinutes(60), null);
    }

    public List<AgendamentoExame> buscarHistoricoPaciente(Integer pacienteId) {
        String sql = "SELECT ... WHERE ae.fk_paciente = ? ORDER BY ae.data_realizacao DESC";
        List<AgendamentoExame> agendamentos = new ArrayList<>();
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pacienteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) agendamentos.add(mapearAgendamento(rs));

        } catch (SQLException | IOException e) {
            tratarErro(e, "buscar histórico paciente");
        }
        return agendamentos;
    }

    private void preencherStatementAgendamento(PreparedStatement stmt, AgendamentoExame ag) throws SQLException {
        stmt.setInt(1, ag.getExame().getId());
        stmt.setInt(2, ag.getPaciente().getId());
        if (ag.getMedicoRequisitante() != null) stmt.setInt(3, ag.getMedicoRequisitante().getId());
        else stmt.setNull(3, Types.INTEGER);
        stmt.setTimestamp(4, Timestamp.valueOf(ag.getDataRealizacao()));
        stmt.setBigDecimal(5, ag.getValorPago());
        stmt.setString(6, ag.getStatus());
    }

    private AgendamentoExame mapearAgendamento(ResultSet rs) throws SQLException {
        AgendamentoExame ag = new AgendamentoExame();
        ag.setId(rs.getInt("agendamento_id"));
        ag.setDataRealizacao(rs.getTimestamp("data_realizacao").toLocalDateTime());
        ag.setValorPago(rs.getBigDecimal("valor_exame"));
        ag.setStatus(rs.getString("status_exame"));
        ag.setExame(new Exame(rs.getInt("exame_id"), rs.getString("exame_nome"), rs.getBigDecimal("exame_valor"), rs.getString("orientacoes")));
        ag.setPaciente(new Paciente(rs.getInt("paciente_id"), rs.getString("paciente_nome"), rs.getString("foto"), rs.getDate("data_nascimento").toLocalDate(), rs.getString("sexo"), rs.getString("endereco"), rs.getString("telefone"), rs.getString("pagamento")));
        if (rs.getObject("medico_req_id") != null) {
            Especialidade esp = new Especialidade(rs.getInt("medico_req_especialidade_id"), rs.getString("medico_req_especialidade_nome"));
            ag.setMedicoRequisitante(new Medico(rs.getInt("medico_req_id"), rs.getInt("medico_req_crm"), rs.getString("medico_req_nome"), rs.getString("medico_req_endereco"), rs.getString("medico_req_telefone"), esp));
        }
        return ag;
    }

    private void tratarErro(Exception e, String operacao) {
        System.err.println("Erro ao " + operacao + ": " + e.getMessage());
        e.printStackTrace();
    }
}
