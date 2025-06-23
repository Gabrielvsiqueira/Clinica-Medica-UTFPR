package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entities.Exame;

public class ExameDAO {

    public void cadastrarExame(Exame exame) {
        String sql = "INSERT INTO Exames (nome, valor, orientacoes) VALUES (?, ?, ?)";
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, exame.getNome());
            stmt.setBigDecimal(2, exame.getValor());
            stmt.setString(3, exame.getOrientacoes());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        exame.setId(rs.getInt(1));
                    }
                }
                System.out.println("Exame inserido com sucesso! ID: " + exame.getId());
            } else {
                System.out.println("Nenhuma linha afetada ao inserir exame.");
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao inserir exame: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Exame buscarExameId(Integer id) {
        String sql = "SELECT cod_exame, nome, valor, orientacoes FROM Exames WHERE cod_exame = ?";
        Exame exame = null;
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    exame = new Exame();
                    exame.setId(rs.getInt("cod_exame"));
                    exame.setNome(rs.getString("nome"));
                    exame.setValor(rs.getBigDecimal("valor"));
                    exame.setOrientacoes(rs.getString("orientacoes"));
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar exame por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return exame;
    }

    public List<Exame> buscarTodosExames() {
        String sql = "SELECT cod_exame, nome, valor, orientacoes FROM Exames ORDER BY nome";
        List<Exame> exames = new ArrayList<>();
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Exame exame = new Exame();
                exame.setId(rs.getInt("cod_exame"));
                exame.setNome(rs.getString("nome"));
                exame.setValor(rs.getBigDecimal("valor"));
                exame.setOrientacoes(rs.getString("orientacoes"));
                exames.add(exame);
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao buscar todos os exames: " + e.getMessage());
            e.printStackTrace();
        }
        return exames;
    }

    public void atualizarExame(Exame exame) {
        String sql = "UPDATE Exames SET nome = ?, valor = ?, orientacoes = ? WHERE cod_exame = ?";
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, exame.getNome());
            stmt.setBigDecimal(2, exame.getValor());
            stmt.setString(3, exame.getOrientacoes());
            stmt.setInt(4, exame.getId());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Exame atualizado com sucesso! ID: " + exame.getId());
            } else {
                System.out.println("Nenhum exame encontrado com o ID: " + exame.getId());
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao atualizar exame: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletarExame(Integer id) {
        String sql = "DELETE FROM Exames WHERE cod_exame = ?";
        try (Connection conn = BancoDados.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Exame deletado com sucesso! ID: " + id);
            } else {
                System.out.println("Nenhum exame encontrado com o ID: " + id);
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao deletar exame: " + e.getMessage());
            e.printStackTrace();
        }
    }
}