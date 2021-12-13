package ru.cloud.cloudcommander.server;

import java.sql.*;


public class JDBCAuth {
    private static  Connection connection;
    private String login;
    private String password;
    private static Statement stmt;
    private boolean isAuthenticated;
    private PreparedStatement preparedStatement;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public JDBCAuth()  {
        try {
            connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            breaking();
        }
    }

    public JDBCAuth(String login, String password) {
        try {
            this.login = login;
            this.password = password;
            connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            breaking();
        }
    }

    private void breaking() {
        try {
            if (stmt != null) stmt.close();
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/CloudBase.db");
        stmt = connection.createStatement();
        System.out.println(tryAuth());
    }

    public boolean tryAuth() {
        String result = null;
        try {
            if (!connection.isClosed()) {
                preparedStatement = connection.prepareStatement("SELECT Name FROM users WHERE Name= ? and Password=?;");
                preparedStatement.setString(1, login);
                preparedStatement.setString(2, password);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    result = rs.getString("Name");
                }
                System.out.println(result);

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        new JDBCAuth("test", "test");
    }

}
