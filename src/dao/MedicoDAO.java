package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entities.Especialidade;
import entities.Medico;

public class MedicoDAO {

    public void cadastrarMedico(Medico medico) {
        String sql = "INSERT INTO Medico (crm, nome, endereco, telefone, fk_especialidade) VALUES (?, ?, ?, ?, ?)"; // Ajustado
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, medico.getCrm());
            stmt.setString(2, medico.getNomeCompleto());
            stmt.setString(3, medico.getEndereco());
            stmt.setString(4, medico.getTelefone());
            stmt.setInt(5, medico.getEspecialidade().getId());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        medico.setId(rs.getInt(1));
                    }
                }
                System.out.println("Médico inserido com sucesso! ID: " + medico.getId());
            } else {
                System.out.println("Nenhuma linha afetada ao inserir médico.");
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao inserir médico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Medico buscarMedicoPorId(Integer id) {
        String sql = "SELECT m.medico_id, m.crm, m.nome, m.endereco, m.telefone, e.cod_especialidade AS especialidade_id, e.especialidade AS especialidade_nome " + // Ajustado
                "FROM Medico m INNER JOIN Especialidade e ON m.fk_especialidade = e.cod_especialidade WHERE m.medico_id = ?"; // Ajustado
        Medico medico = null;
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    medico = new Medico();
                    medico.setId(rs.getInt("medico_id"));
                    medico.setCrm(rs.getInt("crm"));
                    medico.setNomeCompleto(rs.getString("nome"));
                    medico.setEndereco(rs.getString("endereco"));
                    medico.setTelefone(rs.getString("telefone"));

                    Especialidade especialidade = new Especialidade();
                    especialidade.setId(rs.getInt("especialidade_id"));
                    especialidade.setNome(rs.getString("especialidade_nome"));
                    medico.setEspecialidade(especialidade);
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar médico por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return medico;
    }

    public Medico buscarPorCrm(Integer crm) { // CRM é Integer
        String sql = "SELECT m.medico_id, m.crm, m.nome, m.endereco, m.telefone, e.cod_especialidade AS especialidade_id, e.especialidade AS especialidade_nome " + "FROM Medico m INNER JOIN Especialidade e ON m.fk_especialidade = e.cod_especialidade WHERE m.crm = ?";
        Medico medico = null;
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, crm);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    medico = new Medico();
                    medico.setId(rs.getInt("medico_id"));
                    medico.setCrm(rs.getInt("crm"));
                    medico.setNomeCompleto(rs.getString("nome"));
                    medico.setEndereco(rs.getString("endereco"));
                    medico.setTelefone(rs.getString("telefone"));

                    Especialidade especialidade = new Especialidade();
                    especialidade.setId(rs.getInt("especialidade_id"));
                    especialidade.setNome(rs.getString("especialidade_nome"));
                    medico.setEspecialidade(especialidade);
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar médico por CRM: " + e.getMessage());
            e.printStackTrace();
        }
        return medico;
    }


    public List<Medico> buscarTodos() {
        String sql = "SELECT m.medico_id, m.crm, m.nome, m.endereco, m.telefone, e.cod_especialidade AS especialidade_id, e.especialidade AS especialidade_nome " + "FROM Medico m INNER JOIN Especialidade e ON m.fk_especialidade = e.cod_especialidade ORDER BY m.nome";
        List<Medico> medicos = new ArrayList<>();
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Medico medico = new Medico();
                medico.setId(rs.getInt("medico_id"));
                medico.setCrm(rs.getInt("crm"));
                medico.setNomeCompleto(rs.getString("nome"));
                medico.setEndereco(rs.getString("endereco"));
                medico.setTelefone(rs.getString("telefone"));

                Especialidade especialidade = new Especialidade();
                especialidade.setId(rs.getInt("especialidade_id"));
                especialidade.setNome(rs.getString("especialidade_nome"));
                medico.setEspecialidade(especialidade);
                medicos.add(medico);
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar todos os médicos: " + e.getMessage());
            e.printStackTrace();
        }
        return medicos;
    }

    public void atualizarMedico(Medico medico) {
        String sql = "UPDATE Medico SET crm = ?, nome = ?, endereco = ?, telefone = ?, fk_especialidade = ? WHERE medico_id = ?";
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, medico.getCrm());
            stmt.setString(2, medico.getNomeCompleto());
            stmt.setString(3, medico.getEndereco());
            stmt.setString(4, medico.getTelefone());
            stmt.setInt(5, medico.getEspecialidade().getId());
            stmt.setInt(6, medico.getId());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Médico atualizado com sucesso! ID: " + medico.getId());
            } else {
                System.out.println("Nenhum médico encontrado com o ID: " + medico.getId());
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao atualizar médico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletarMedico(Integer id) {
        String sql = "DELETE FROM Medico WHERE medico_id = ?";
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

        } catch (SQLException | IOException e) {
            System.err.println("Erro ao deletar médico: " + e.getMessage());
            e.printStackTrace();
        }
    }
}