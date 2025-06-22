package dao;

import entities.Consulta;
import entities.Especialidade;
import entities.Medico;
import entities.Paciente;

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

public class ConsultaDAO {

    public void inserir(Consulta consulta) {
        String sql = "INSERT INTO Agendar_Consulta (fk_paciente, fk_medico, horario_consulta, status) VALUES (?, ?, ?, ?)"; // Ajustado
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, consulta.getPaciente().getId()); // Mapeia para fk_paciente
            stmt.setInt(2, consulta.getMedico().getId()); // Mapeia para fk_medico
            stmt.setTimestamp(3, Timestamp.valueOf(consulta.getDataHora())); // Mapeia para horario_consulta
            stmt.setString(4, consulta.getStatus());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        consulta.setId(rs.getInt(1)); // id_consulta é o ID
                    }
                }
                System.out.println("Consulta inserida com sucesso! ID: " + consulta.getId());
            } else {
                System.out.println("Nenhuma linha afetada ao inserir consulta.");
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao inserir consulta: " + e.getMessage());
            e.printStackTrace();
            // Para lidar com a constraint UNIQUE no DB
            if (e instanceof SQLException && ((SQLException) e).getSQLState().startsWith("23")) { // 23 é o SQLState para violação de integridade
                System.err.println("Possível sobreposição de consulta detectada para o médico neste horário.");
            }
        }
    }

    public Consulta buscarPorId(Integer id) {
        String sql = "SELECT c.id_consulta, c.horario_consulta, c.status, " + // Ajustado
                "p.paciente_id AS paciente_id, p.nome AS paciente_nome, p.foto, p.data_nascimento, p.sexo, p.endereco, p.telefone, p.pagamento, " + // Ajustado Paciente
                "m.medico_id AS medico_id, m.crm, m.nome AS medico_nome, m.endereco AS medico_endereco, m.telefone AS medico_telefone, " + // Ajustado Medico
                "e.cod_especialidade AS especialidade_id, e.especialidade AS especialidade_nome " + // Ajustado Especialidade
                "FROM Agendar_Consulta c " + // Ajustado nome da tabela
                "INNER JOIN Paciente p ON c.fk_paciente = p.paciente_id " + // Ajustado
                "INNER JOIN Medico m ON c.fk_medico = m.medico_id " + // Ajustado
                "INNER JOIN Especialidade e ON m.fk_especialidade = e.cod_especialidade " + // Ajustado
                "WHERE c.id_consulta = ?";
        Consulta consulta = null;
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    consulta = new Consulta();
                    consulta.setId(rs.getInt("id_consulta"));
                    consulta.setDataHora(rs.getTimestamp("horario_consulta").toLocalDateTime()); // Mapeia horario_consulta para dataHora
                    consulta.setStatus(rs.getString("status"));

                    Paciente paciente = new Paciente(
                            rs.getInt("paciente_id"),
                            rs.getString("paciente_nome"),
                            rs.getString("foto"),
                            rs.getDate("data_nascimento").toLocalDate(),
                            rs.getString("sexo"),
                            rs.getString("endereco"),
                            rs.getString("telefone"),
                            rs.getString("pagamento") // Mapeia 'pagamento' para formaPagamento
                    );
                    consulta.setPaciente(paciente);

                    Especialidade especialidade = new Especialidade(
                            rs.getInt("especialidade_id"),
                            rs.getString("especialidade_nome")
                    );
                    Medico medico = new Medico(
                            rs.getInt("medico_id"),
                            rs.getInt("crm"), // CRM é Integer
                            rs.getString("medico_nome"), // Mapeia 'nome' para nomeCompleto
                            rs.getString("medico_endereco"),
                            rs.getString("medico_telefone"),
                            especialidade
                    );
                    consulta.setMedico(medico);
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar consulta por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return consulta;
    }

    public List<Consulta> buscarPorMedicoEData(Integer medicoId, LocalDateTime data) {
        String sql = "SELECT c.id_consulta, c.horario_consulta, c.status, " + // Ajustado
                "p.paciente_id AS paciente_id, p.nome AS paciente_nome, p.foto, p.data_nascimento, p.sexo, p.endereco, p.telefone, p.pagamento, " + // Ajustado Paciente
                "m.medico_id AS medico_id, m.crm, m.nome AS medico_nome, m.endereco AS medico_endereco, m.telefone AS medico_telefone, " + // Ajustado Medico
                "e.cod_especialidade AS especialidade_id, e.especialidade AS especialidade_nome " + // Ajustado Especialidade
                "FROM Agendar_Consulta c " + // Ajustado nome da tabela
                "INNER JOIN Paciente p ON c.fk_paciente = p.paciente_id " + // Ajustado
                "INNER JOIN Medico m ON c.fk_medico = m.medico_id " + // Ajustado
                "INNER JOIN Especialidade e ON m.fk_especialidade = e.cod_especialidade " + // Ajustado
                "WHERE c.fk_medico = ? AND DATE(c.horario_consulta) = ? ORDER BY c.horario_consulta"; // Ajustado
        List<Consulta> consultas = new ArrayList<>();
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, medicoId);
            stmt.setDate(2, java.sql.Date.valueOf(data.toLocalDate()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Consulta consulta = new Consulta();
                    consulta.setId(rs.getInt("id_consulta"));
                    consulta.setDataHora(rs.getTimestamp("horario_consulta").toLocalDateTime());
                    consulta.setStatus(rs.getString("status"));

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
                    consulta.setPaciente(paciente);

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
                    consulta.setMedico(medico);
                    consultas.add(consulta);
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar consultas por médico e data: " + e.getMessage());
            e.printStackTrace();
        }
        return consultas;
    }

    public void atualizar(Consulta consulta) {
        String sql = "UPDATE Agendar_Consulta SET fk_paciente = ?, fk_medico = ?, horario_consulta = ?, status = ? WHERE id_consulta = ?"; // Ajustado
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, consulta.getPaciente().getId());
            stmt.setInt(2, consulta.getMedico().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(consulta.getDataHora()));
            stmt.setString(4, consulta.getStatus());
            stmt.setInt(5, consulta.getId());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Consulta atualizada com sucesso! ID: " + consulta.getId());
            } else {
                System.out.println("Nenhuma consulta encontrada com o ID: " + consulta.getId());
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao atualizar consulta: " + e.getMessage());
            e.printStackTrace();
            if (e instanceof SQLException && ((SQLException) e).getSQLState().startsWith("23")) {
                System.err.println("Possível sobreposição de consulta detectada para o médico neste horário.");
            }
        }
    }

    public void deletar(Integer id) {
        String sql = "DELETE FROM Agendar_Consulta WHERE id_consulta = ?"; // Ajustado
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Consulta deletada com sucesso! ID: " + id);
            } else {
                System.out.println("Nenhuma consulta encontrada com o ID: " + id);
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao deletar consulta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para verificar sobreposição de consultas para um médico (ajustado para o nome da coluna do DB)
    // Este método é essencial para a lógica no Service.
    public boolean existeSobreposicao(Integer medicoId, LocalDateTime inicio, LocalDateTime fim, Integer consultaIdExcluir) {
        String sql = "SELECT COUNT(*) FROM Agendar_Consulta " + // Ajustado
                "WHERE fk_medico = ? AND horario_consulta BETWEEN ? AND ? " + // Ajustado
                "AND status != 'Cancelada'";

        if (consultaIdExcluir != null) {
            sql += " AND id_consulta != ?"; // Ajustado
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
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao verificar sobreposição de consulta: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Consulta> buscarHistoricoPaciente(Integer pacienteId) {
        String sql = "SELECT c.id_consulta, c.horario_consulta, c.status, " +
                "p.paciente_id AS paciente_id, p.nome AS paciente_nome, p.foto, p.data_nascimento, p.sexo, p.endereco, p.telefone, p.pagamento, " +
                "m.medico_id AS medico_id, m.crm, m.nome AS medico_nome, m.endereco AS medico_endereco, m.telefone AS medico_telefone, " +
                "e.cod_especialidade AS especialidade_id, e.especialidade AS especialidade_nome " +
                "FROM Agendar_Consulta c " +
                "INNER JOIN Paciente p ON c.fk_paciente = p.paciente_id " +
                "INNER JOIN Medico m ON c.fk_medico = m.medico_id " +
                "INNER JOIN Especialidade e ON m.fk_especialidade = e.cod_especialidade " +
                "WHERE c.fk_paciente = ? ORDER BY c.horario_consulta DESC"; // Ajustado
        List<Consulta> consultas = new ArrayList<>();
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pacienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Consulta consulta = new Consulta();
                    consulta.setId(rs.getInt("id_consulta"));
                    consulta.setDataHora(rs.getTimestamp("horario_consulta").toLocalDateTime());
                    consulta.setStatus(rs.getString("status"));

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
                    consulta.setPaciente(paciente);

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
                    consulta.setMedico(medico);
                    consultas.add(consulta);
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar histórico de consultas do paciente: " + e.getMessage());
            e.printStackTrace();
        }
        return consultas;
    }
}