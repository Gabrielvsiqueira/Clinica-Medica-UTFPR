package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class BancoDados {
    public static Connection closeConnection;

    public static Connection conectar() throws SQLException, IOException {
        Connection connection = null;
        Properties props = carregarPropriedades();
        String url = props.getProperty("dburl");
        String user = props.getProperty("user");
        String password = props.getProperty("password");
        connection = DriverManager.getConnection(url, user, password);
        System.out.println("Conexão com o banco de dados estabelecida.");
        return connection;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    public static void closeStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar statement: " + e.getMessage());
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar ResultSet: " + e.getMessage());
            }
        }
    }

    private static Properties carregarPropriedades() throws IOException {
        Properties props = new Properties();
        try (FileInputStream propriedadesBanco = new FileInputStream("database.properties")) {
            props.load(propriedadesBanco);
        }
        return props;
    }
    
}