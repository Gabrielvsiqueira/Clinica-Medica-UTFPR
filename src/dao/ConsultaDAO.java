package dao;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import entities.Consulta;
import entities.Especialidade;
import entities.Medico;
import entities.Paciente;

public class ConsultaDAO {
    public void cadastrarConsulta(Consulta consulta) {
        String sql = "INSERT INTO Agendar_Consulta (fk_paciente, fk_medico, horario_consulta, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, consulta.getPaciente().getId());
            stmt.setInt(2, consulta.getMedico().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(consulta.getDataHora()));
            stmt.setString(4, consulta.getStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) consulta.setId(rs.getInt(1));
                }
            }

        } catch (SQLException | IOException e) {
            throw new RuntimeException("Erro ao cadastrar consulta", e);
        }
    }

    public Consulta buscarConsultaId(Integer id) {
        String sql = getConsultaBaseSQL() + " WHERE c.id_consulta = ?";
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapearConsulta(rs) : null;
            }

        } catch (SQLException | IOException e) {
            throw new RuntimeException("Erro ao buscar consulta por ID", e);
        }
    }

    public void atualizarConsulta(Consulta consulta) {
        String sql = "UPDATE Agendar_Consulta SET fk_paciente = ?, fk_medico = ?, horario_consulta = ?, status = ? WHERE id_consulta = ?";
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, consulta.getPaciente().getId());
            stmt.setInt(2, consulta.getMedico().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(consulta.getDataHora()));
            stmt.setString(4, consulta.getStatus());
            stmt.setInt(5, consulta.getId());

            stmt.executeUpdate();

        } catch (SQLException | IOException e) {
            throw new RuntimeException("Erro ao atualizar consulta", e);
        }
    }

    public void deletarConsulta(Integer id) {
        String sql = "DELETE FROM Agendar_Consulta WHERE id_consulta = ?";
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException | IOException e) {
            throw new RuntimeException("Erro ao deletar consulta", e);
        }
    }

    public List<Consulta> buscarPorMedicoEData(Integer medicoId, LocalDateTime data) {
        String sql = getConsultaBaseSQL() +
                " WHERE c.fk_medico = ? AND DATE(c.horario_consulta) = ? ORDER BY c.horario_consulta";
        List<Consulta> consultas = new ArrayList<>();

        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, medicoId);
            stmt.setDate(2, java.sql.Date.valueOf(data.toLocalDate()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    consultas.add(mapearConsulta(rs));
                }
            }

        } catch (SQLException | IOException e) {
            throw new RuntimeException("Erro ao buscar consultas por médico e data", e);
        }
        return consultas;
    }

    public List<Consulta> buscarHistoricoPaciente(Integer pacienteId) {
        String sql = getConsultaBaseSQL() +
                " WHERE c.fk_paciente = ? ORDER BY c.horario_consulta DESC";
        List<Consulta> consultas = new ArrayList<>();

        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pacienteId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    consultas.add(mapearConsulta(rs));
                }
            }

        } catch (SQLException | IOException e) {
            throw new RuntimeException("Erro ao buscar histórico de consultas do paciente", e);
        }
        return consultas;
    }

    public boolean existeSobreposicaoConsulta(Integer medicoId, LocalDateTime inicio, LocalDateTime fim, Integer consultaIdExcluir) {
        String sql = "SELECT COUNT(*) FROM Agendar_Consulta " +
                "WHERE fk_medico = ? AND horario_consulta BETWEEN ? AND ? AND status != 'Cancelada'";

        if (consultaIdExcluir != null) {
            sql += " AND id_consulta != ?";
        }

        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, medicoId);
            stmt.setTimestamp(2, Timestamp.valueOf(inicio));
            stmt.setTimestamp(3, Timestamp.valueOf(fim));

            if (consultaIdExcluir != null) {
                stmt.setInt(4, consultaIdExcluir);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException | IOException e) {
            throw new RuntimeException("Erro ao verificar sobreposição de consulta", e);
        }
    }

    private String getConsultaBaseSQL() {
        return "SELECT c.id_consulta, c.horario_consulta, c.status, " +
                "p.paciente_id AS paciente_id, p.nome AS paciente_nome, p.foto, p.data_nascimento, p.sexo, p.endereco, p.telefone, p.pagamento, " +
                "m.medico_id AS medico_id, m.crm, m.nome AS medico_nome, m.endereco AS medico_endereco, m.telefone AS medico_telefone, " +
                "e.cod_especialidade AS especialidade_id, e.especialidade AS especialidade_nome " +
                "FROM Agendar_Consulta c " +
                "INNER JOIN Paciente p ON c.fk_paciente = p.paciente_id " +
                "INNER JOIN Medico m ON c.fk_medico = m.medico_id " +
                "INNER JOIN Especialidade e ON m.fk_especialidade = e.cod_especialidade";
    }

    private Consulta mapearConsulta(ResultSet rs) throws SQLException {
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

        Especialidade especialidade = new Especialidade(
                rs.getInt("especialidade_id"),
                rs.getString("especialidade_nome")
        );

        Medico medico = new Medico(
                rs.getInt("medico_id"),
                rs.getInt("crm"),
                rs.getString("medico_nome"),
                rs.getString("medico_endereco"),
                rs.getString("medico_telefone"),
                especialidade
        );

        return new Consulta(
                rs.getInt("id_consulta"),
                paciente,
                medico,
                rs.getTimestamp("horario_consulta").toLocalDateTime(),
                rs.getString("status")
        );
    }
}
