package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import entities.Especialidade;

public class EspecialidadeDAO {

    public void cadastrarEspecialidade(Especialidade especialidade) {
        String sql = "INSERT INTO Especialidade (especialidade) VALUES (?)"; // Ajustado nome da tabela e coluna
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, especialidade.getNome());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        especialidade.setId(rs.getInt(1)); // cod_especialidade é o ID
                    }
                }
                System.out.println("Especialidade inserida com sucesso! ID: " + especialidade.getId());
            } else {
                System.out.println("Nenhuma linha afetada ao inserir especialidade.");
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao inserir especialidade: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Especialidade buscarEspecialidadeId(Integer id) {
        String sql = "SELECT cod_especialidade, especialidade FROM Especialidade WHERE cod_especialidade = ?"; // Ajustado
        Especialidade especialidade = null;
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    especialidade = new Especialidade();
                    especialidade.setId(rs.getInt("cod_especialidade"));
                    especialidade.setNome(rs.getString("especialidade")); // Mapeia 'especialidade' do DB para 'nome'
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar especialidade por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return especialidade;
    }

    public List<Especialidade> buscarTodos() {
        String sql = "SELECT cod_especialidade, especialidade FROM Especialidade ORDER BY especialidade"; // Ajustado
        List<Especialidade> especialidades = new ArrayList<>();
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Especialidade especialidade = new Especialidade();
                especialidade.setId(rs.getInt("cod_especialidade"));
                especialidade.setNome(rs.getString("especialidade"));
                especialidades.add(especialidade);
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar todas as especialidades: " + e.getMessage());
            e.printStackTrace();
        }
        return especialidades;
    }

    public void atualizarEspecialidade(Especialidade especialidade) {
        String sql = "UPDATE Especialidade SET especialidade = ? WHERE cod_especialidade = ?";
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, especialidade.getNome());
            stmt.setInt(2, especialidade.getId());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Especialidade atualizada com sucesso! ID: " + especialidade.getId());
            } else {
                System.out.println("Nenhuma especialidade encontrada com o ID: " + especialidade.getId());
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao atualizar especialidade: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletarEspecialidade(Integer id) {
        String sql = "DELETE FROM Especialidade WHERE cod_especialidade = ?";
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
             stmt.executeUpdate();
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao deletar especialidade: " + e.getMessage());
            e.printStackTrace();
        }
    }
}