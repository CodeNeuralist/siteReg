package org.example;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class Main {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(5000), 0);
        server.createContext("/register", new RegisterHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 5000");
    }

    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Получение данных из тела запроса
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                // Обработка данных
                System.out.println("Received registration data: " + requestBody);
                // Добавление пользователя в базу данных SQLite
                try {
                    addUserToDatabase(requestBody);
                    // Отправка ответа клиенту
                    String response = "User registered successfully";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Ошибка при добавлении пользователя в базу данных
                    String response = "Error adding user to database";
                    exchange.sendResponseHeaders(500, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            } else {
                // Метод запроса отличается от POST
                String response = "Only POST method is supported";
                exchange.sendResponseHeaders(405, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private void addUserToDatabase(String userData) throws SQLException {
            // Подключение к базе данных SQLite
            String url = "jdbc:sqlite:users.db";
            try (Connection conn = DriverManager.getConnection(url)) {
                // Создание таблицы, если она не существует
                String sqlCreateTable = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlCreateTable)) {
                    pstmt.executeUpdate();
                }
                // Добавление пользователя в базу данных
                String sqlInsert = "INSERT INTO users (data) VALUES (?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                    pstmt.setString(1, userData);
                    pstmt.executeUpdate();
                }
            }
        }
    }
}
