package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class BancoDados {
    public static Connection closeConnection;

    // REMOVA A VARIÁVEL ESTÁTICA 'conn' DAQUI.
    // private static Connection conn = null; // <-- REMOVA ESTA LINHA!

    public static Connection conectar() throws SQLException, IOException {
        Connection connection = null; // Declara uma variável local para a conexão
        Properties props = carregarPropriedades();
        String url = props.getProperty("dburl");
        String user = props.getProperty("user"); // Certifique-se de que 'user' e 'password' estão no seu database.properties
        String password = props.getProperty("password");

        try {
            // Carrega o driver JDBC explicitamente (ainda uma boa prática)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Sempre cria uma NOVA conexão
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexão com o banco de dados estabelecida.");
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC do MySQL não encontrado. Certifique-se de que o conector esteja no classpath.");
            e.printStackTrace();
            throw new SQLException("Driver do banco de dados não encontrado.", e);
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
            // Lança a exceção para que as camadas superiores saibam da falha
            throw new SQLException("Falha na conexão com o banco de dados. Verifique as credenciais ou o servidor.", e);
        }
    }

    // REMOVA OU SIMPLIFIQUE o método desconectar(), pois o try-with-resources cuida disso.
    // Se você realmente precisar de um desconectar global (ex: ao fechar a aplicação),
    // ele precisaria de uma variável estática para fechar. Mas para DAOs, evite-o.
    // Se a conexão não é estática em 'BancoDados', este método não faz mais sentido.
    // Se você tinha um 'conn' estático e o usava em outros lugares além do try-with-resources,
    // essa seria uma preocupação diferente.
    // Por enquanto, podemos simplesmente REMOVER o método desconectar() se ele só gerenciava o 'conn' estático.

    // NOVO método para fechar a conexão, statement, e resultset (boa prática no try-catch normal)
    // Mas com try-with-resources, você não precisará chamá-los explicitamente.
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    // Adapte este método, pois ele pode ser usado em try-catch normais
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
        // Garanta que database.properties esteja no classpath ou especifique o caminho completo
        try (FileInputStream propriedadesBanco = new FileInputStream("database.properties")) {
            props.load(propriedadesBanco);
        } // try-with-resources fecha automaticamente
        return props;
    }
    
}