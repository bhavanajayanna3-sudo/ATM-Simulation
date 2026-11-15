
import java.sql.*;
import java.util.Scanner;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;

public class ATMSimulation {

    static Scanner scanner = new Scanner(System.in);

    static String accountNumber;

    static String databaseURL = System.getenv("DB_URL");
    static String databaseUser = System.getenv("DB_USER");
    static String databasePassword = System.getenv("DB_PASSWORD");

    public static void main(String[] args) throws IOException {

        int port = Integer.parseInt(
            System.getenv().getOrDefault("PORT", "8080")
        );

        HttpServer server = HttpServer.create(
            new InetSocketAddress(port), 0
        );

        // Test connection
        server.createContext("/test", exchange -> {

            String message = "Java Backend Connected Successfully!";

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
            );

            byte[] response = message.getBytes();

            exchange.sendResponseHeaders(200, response.length);

            OutputStream output = exchange.getResponseBody();
            output.write(response);
            output.close();
        });

        // Login
        server.createContext("/login", exchange -> {

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
            );

            if (exchange.getRequestMethod().equals("POST")) {

                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(exchange.getRequestBody())
                );

                String data = reader.readLine();
                String[] details = data.split("&");

                String account = URLDecoder.decode(
                    details[0].split("=")[1], "UTF-8"
                );

                String pin = URLDecoder.decode(
                    details[1].split("=")[1], "UTF-8"
                );

                String message = "Invalid Account Number or PIN!";

                try {
                    Connection connection = connect();

                    String query =
                        "SELECT customer_name FROM customers WHERE account_number=? AND pin_hash=?";

                    PreparedStatement statement =
                        connection.prepareStatement(query);

                    statement.setString(1, account);
                    statement.setString(2, pin);

                    ResultSet result = statement.executeQuery();

                    if (result.next()) {
                        message = "Login Successful! Welcome "
                            + result.getString("customer_name");
                    }

                    result.close();
                    statement.close();
                    connection.close();

                } catch (Exception error) {

                    error.printStackTrace();
                    message = "Database Error!";
                }

                byte[] response = message.getBytes();

                exchange.sendResponseHeaders(200, response.length);

                OutputStream output = exchange.getResponseBody();
                output.write(response);
                output.close();
            }
        });

        // Check Balance
        server.createContext("/balance", exchange -> {

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
            );

            String account = exchange.getRequestURI()
                .getQuery().split("=")[1];

            String message = "";

            try {
                Connection connection = connect();

                String query =
                    "SELECT balance FROM customers WHERE account_number=?";

                PreparedStatement statement =
                    connection.prepareStatement(query);

                statement.setString(1, account);

                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    message = "Your Balance: Rs. "
                        + result.getDouble("balance");
                }

                result.close();
                statement.close();
                connection.close();

            } catch (Exception error) {
                message = "Unable to check balance!";
            }

            byte[] response = message.getBytes();

            exchange.sendResponseHeaders(200, response.length);

            OutputStream output = exchange.getResponseBody();
            output.write(response);
            output.close();
        });

        // Deposit
        server.createContext("/deposit", exchange -> {

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
            );

            String message = "Deposit Failed!";

            try {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(exchange.getRequestBody())
                );

                String data = reader.readLine();
                String[] details = data.split("&");

                String account = URLDecoder.decode(
                    details[0].split("=")[1], "UTF-8"
                );

                double amount = Double.parseDouble(
                    details[1].split("=")[1]
                );

                if (amount <= 0) {
                    message = "Enter a valid amount!";
                } else {

                    Connection connection = connect();

                    String query =
                        "UPDATE customers SET balance = balance + ? WHERE account_number=?";

                    PreparedStatement statement =
                        connection.prepareStatement(query);

                    statement.setDouble(1, amount);
                    statement.setString(2, account);

                    int rows = statement.executeUpdate();

                    if (rows > 0) {

                        String transactionQuery =
                            "INSERT INTO transactions (account_number, transaction_type, amount) VALUES (?, ?, ?)";

                        PreparedStatement transaction =
                            connection.prepareStatement(transactionQuery);

                        transaction.setString(1, account);
                        transaction.setString(2, "Deposit");
                        transaction.setDouble(3, amount);

                        transaction.executeUpdate();

                        message = "Money Deposited Successfully!";

                        transaction.close();
                    }

                    statement.close();
                    connection.close();
                }

            } } catch (Exception error) {
    error.printStackTrace();
    message = "Deposit Error: " + error.getMessage();
}

            byte[] response = message.getBytes();

            exchange.sendResponseHeaders(200, response.length);

            OutputStream output = exchange.getResponseBody();
            output.write(response);
            output.close();
        });

        // Withdraw
        server.createContext("/withdraw", exchange -> {

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
            );

            String message = "Withdrawal Failed!";

            try {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(exchange.getRequestBody())
                );

                String data = reader.readLine();
                String[] details = data.split("&");

                String account = URLDecoder.decode(
                    details[0].split("=")[1], "UTF-8"
                );

                double amount = Double.parseDouble(
                    details[1].split("=")[1]
                );

                if (amount <= 0) {
                    message = "Enter a valid amount!";
                } else {

                    Connection connection = connect();

                    String query =
                        "UPDATE customers SET balance = balance - ? WHERE account_number = ? AND balance >= ?";

                    PreparedStatement statement =
                        connection.prepareStatement(query);

                    statement.setDouble(1, amount);
                    statement.setString(2, account);
                    statement.setDouble(3, amount);

                    int rows = statement.executeUpdate();

                    if (rows > 0) {

                        String transactionQuery =
                            "INSERT INTO transactions (account_number, transaction_type, amount) VALUES (?, ?, ?)";

                        PreparedStatement transaction =
                            connection.prepareStatement(transactionQuery);

                        transaction.setString(1, account);
                        transaction.setString(2, "Withdrawal");
                        transaction.setDouble(3, amount);

                        transaction.executeUpdate();

                        message =
                            "Withdrawal Successful! Please collect your cash.";

                        transaction.close();

                    } else {
                        message = "Insufficient Balance!";
                    }

                    statement.close();
                    connection.close();
                }

            } } catch (Exception error) {
    error.printStackTrace();
    message = "Database Error: " + error.getMessage();
}

            byte[] response = message.getBytes();

            exchange.sendResponseHeaders(200, response.length);

            OutputStream output = exchange.getResponseBody();
            output.write(response);
            output.close();
        });

        // Mini Statement
server.createContext("/statement", exchange -> {

    String query = exchange.getRequestURI().getQuery();
    String account = query.split("=")[1];

    String message = "";

    try {
        Connection connection = connect();

        String sql = "SELECT transaction_type, amount, transaction_date FROM transactions WHERE account_number=? ORDER BY transaction_date DESC LIMIT 5";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, account);

        ResultSet result = statement.executeQuery();

        while (result.next()) {
            message += result.getString("transaction_type")
                + " - Rs. "
                + result.getDouble("amount")
                + " - "
                + result.getTimestamp("transaction_date")
                + "\n";
        }

        if (message.equals("")) {
            message = "No transactions found!";
        }

        result.close();
        statement.close();
        connection.close();

    } catch (Exception error) {
        error.printStackTrace();
        message = "Database Error: " + error.getMessage();
    }

    exchange.getResponseHeaders().set(
        "Access-Control-Allow-Origin", "*"
    );

    byte[] response = message.getBytes();

    exchange.sendResponseHeaders(200, response.length);

    OutputStream output = exchange.getResponseBody();
    output.write(response);
    output.close();
});

server.start();

System.out.println("ATM Server Started!");
System.out.println("Server running at port " + port);
}
    // Database Connection
    static Connection connect() throws SQLException {

        return DriverManager.getConnection(
            databaseURL,
            databaseUser,
            databasePassword
        );
    }
}
