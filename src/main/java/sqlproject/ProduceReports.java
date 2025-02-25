package sqlproject;

import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class ProduceReports {
     //This line declares a constant variable DB_URL that holds the URL for the MySQL database you're connecting to.
    // It specifies the database location, port, and database name.


    private static final String DB_URL = "jdbc:mysql://localhost:3306/project2";



    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            //These lines allow the user to input a customer ID. It uses a Scanner object to read input from the user.
            System.out.print("Enter customer ID: ");
            long customerId = scanner.nextLong();

           
             //These lines load database configuration properties from a file named "config.properties."
            // It includes the database username and password.
             Properties props = new Properties();
            try (FileInputStream input = new FileInputStream("config.properties")) {
                props.load(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String DB_USER = props.getProperty("DB_USER");
            String DB_PASSWORD = props.getProperty("DB_PASSWORD");

             //This code establishes a connection to the MySQL database using,
            // the URL, username (DB_USER), and password (DB_PASSWORD) obtained from the properties file.
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                 //It calls a method named produceReports to generate reports based on the customer ID and the database connection.
                produceReports(connection, customerId);
                 //These lines catch and handle exceptions that might occur during the database connection or report generation.
                // If there's an error, it prints the error details.
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
//This method is responsible for producing various reports based on the customer ID and the database connection.
    private static void produceReports(Connection connection, long customerId) throws SQLException {

        // Create a SQL statement
        Statement statement = connection.createStatement();
// Report 1: Address data for the given customer ID
        String query1 = "SELECT Address FROM Customers WHERE CustomerIdentification = '" + customerId + "'";
        ResultSet resultSet1 = statement.executeQuery(query1);
        while (resultSet1.next()) {
            System.out.println("Address: " + resultSet1.getString("Address"));
        }
// Report 2: Total balance for all accounts of the given customer ID
        String query2 = "SELECT SUM(CurrentBalance) AS TotalBalance FROM BankAccounts WHERE CustomerIdentification_FK = '" + customerId + "'";
        ResultSet resultSet2 = statement.executeQuery(query2);
        while (resultSet2.next()) {
            System.out.println("Total Balance: $" + resultSet2.getBigDecimal("TotalBalance"));
        }
// Report 3: Overview of transactions for checking accounts
        String query3 = "SELECT B.AccountNumber, B.AccountType, T.TransactionDate, T.Amount, T.TransactionType " +
                "FROM Transactions T " +
                "INNER JOIN BankAccounts B ON T.CustomerIdentification_FK = B.CustomerIdentification_FK " +
                "WHERE T.CustomerIdentification_FK = '" + customerId + "' AND B.AccountType = 'CHEQUE'";
        ResultSet resultSet3 = statement.executeQuery(query3);
        while (resultSet3.next()) {
            System.out.println("Account Number: " + resultSet3.getString("AccountNumber"));
            System.out.println("Account Type: " + resultSet3.getString("AccountType"));
            System.out.println("Transaction Date: " + resultSet3.getDate("TransactionDate"));
            System.out.println("Amount: $" + resultSet3.getBigDecimal("Amount"));
            System.out.println("Transaction Type: " + resultSet3.getString("TransactionType"));
            System.out.println();
        }
// Close the resources
        resultSet1.close();
        resultSet2.close();
        resultSet3.close();
        statement.close();
        connection.close();
    }
}




