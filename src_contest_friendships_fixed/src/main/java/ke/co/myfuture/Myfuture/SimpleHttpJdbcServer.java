package ke.co.myfuture.Myfuture;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SimpleHttpJdbcServer {

    // Change DB connection details
    private static final String JDBC_URL = "jdbc:mariadb://localhost:3306/myfuture_question_store";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "root";

    public static void main(String[] args) throws Exception {
        // Start simple HTTP server on port 2024
        HttpServer server = HttpServer.create(new InetSocketAddress(2024), 0);
        server.createContext("/notes", new NotesHandler());
        server.setExecutor(null); // default executor
        server.start();
        System.out.println("Server started at http://localhost:2024/notes");
    }

    static class NotesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, content FROM curri_notes WHERE subtopic = 3204")) {

                StringBuilder sb = new StringBuilder();
                sb.append("[");
                while (rs.next()) {
                    sb.append("{")
                            .append("\"id\":").append(rs.getInt("id")).append(",")
                            .append("\"content\":\"").append(rs.getString("content")).append("\"")
                            .append("},");
                }
                if (sb.charAt(sb.length() - 1) == ',') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                sb.append("]");
                response = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                response = "{\"error\":\"" + e.getMessage() + "\"}";
            }

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
