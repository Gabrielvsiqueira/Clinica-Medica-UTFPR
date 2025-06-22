package dao;

import entities.Paciente;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    public void inserir(Paciente paciente) {
        String sql = "INSERT INTO Paciente (nome, foto, data_nascimento, sexo, endereco, telefone, pagamento) VALUES (?, ?, ?, ?, ?, ?, ?)"; // Ajustado
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getFoto()); // Assumindo String para caminho/URL
            stmt.setDate(3, Date.valueOf(paciente.getDataNascimento()));
            stmt.setString(4, paciente.getSexo());
            stmt.setString(5, paciente.getEndereco());
            stmt.setString(6, paciente.getTelefone()); // Assumindo String para telefone
            stmt.setString(7, paciente.getFormaPagamento()); // Mapeia formaPagamento para 'pagamento'
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        paciente.setId(rs.getInt(1)); // paciente_id é o ID
                    }
                }
                System.out.println("Paciente inserido com sucesso! ID: " + paciente.getId());
            } else {
                System.out.println("Nenhuma linha afetada ao inserir paciente.");
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao inserir paciente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Paciente buscarPorId(Integer id) {
        String sql = "SELECT paciente_id, nome, foto, data_nascimento, sexo, endereco, telefone, pagamento FROM Paciente WHERE paciente_id = ?"; // Ajustado
        Paciente paciente = null;
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    paciente = new Paciente();
                    paciente.setId(rs.getInt("paciente_id"));
                    paciente.setNome(rs.getString("nome"));
                    paciente.setFoto(rs.getString("foto"));
                    paciente.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                    paciente.setSexo(rs.getString("sexo"));
                    paciente.setEndereco(rs.getString("endereco"));
                    paciente.setTelefone(rs.getString("telefone")); // Assumindo String
                    paciente.setFormaPagamento(rs.getString("pagamento")); // Mapeia 'pagamento' para formaPagamento
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar paciente por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return paciente;
    }

    public List<Paciente> buscarTodos() {
        String sql = "SELECT paciente_id, nome, foto, data_nascimento, sexo, endereco, telefone, pagamento FROM Paciente ORDER BY nome"; // Ajustado
        List<Paciente> pacientes = new ArrayList<>();
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(rs.getInt("paciente_id"));
                paciente.setNome(rs.getString("nome"));
                paciente.setFoto(rs.getString("foto"));
                paciente.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                paciente.setSexo(rs.getString("sexo"));
                paciente.setEndereco(rs.getString("endereco"));
                paciente.setTelefone(rs.getString("telefone"));
                paciente.setFormaPagamento(rs.getString("pagamento"));
                pacientes.add(paciente);
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar todos os pacientes: " + e.getMessage());
            e.printStackTrace();
        }
        return pacientes;
    }

    public void atualizar(Paciente paciente) {
        String sql = "UPDATE Paciente SET nome = ?, foto = ?, data_nascimento = ?, sexo = ?, endereco = ?, telefone = ?, pagamento = ? WHERE paciente_id = ?"; // Ajustado
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getFoto());
            stmt.setDate(3, Date.valueOf(paciente.getDataNascimento()));
            stmt.setString(4, paciente.getSexo());
            stmt.setString(5, paciente.getEndereco());
            stmt.setString(6, paciente.getTelefone());
            stmt.setString(7, paciente.getFormaPagamento());
            stmt.setInt(8, paciente.getId());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Paciente atualizado com sucesso! ID: " + paciente.getId());
            } else {
                System.out.println("Nenhum paciente encontrado com o ID: " + paciente.getId());
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao atualizar paciente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletar(Integer id) {
        String sql = "DELETE FROM Paciente WHERE paciente_id = ?"; // Ajustado
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Paciente deletado com sucesso! ID: " + id);
            } else {
                System.out.println("Nenhum paciente encontrado com o ID: " + id);
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao deletar paciente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}