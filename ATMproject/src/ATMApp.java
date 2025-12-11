import java.sql.*;
import java.util.Scanner;

public class ATMApp {

    static final String URL = "jdbc:mysql://localhost:3306/atm_db";
    static final String USER = "root";  // update if needed
    static final String PASS = "";      // update if needed

    static Scanner sc = new Scanner(System.in);
    static Connection conn;
    static String loggedUser;

    public static void main(String[] args) {

        try {
            conn = DriverManager.getConnection(URL, USER, PASS);

            if (login()) {
                menu();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // ------------------ LOGIN ------------------
    public static boolean login() throws Exception {
        System.out.print("Enter username: ");
        String user = sc.nextLine();

        System.out.print("Enter password: ");
        String pass = sc.nextLine();

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, user);
        ps.setString(2, pass);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            loggedUser = user;
            System.out.println("\nLogin successful. Welcome, " + loggedUser + " !");
            return true;
        } else {
            System.out.println("Invalid credentials.");
            return false;
        }
    }

    // ------------------ MENU ------------------
    public static void menu() throws Exception {

        while (true) {
            System.out.println("\n===== ATM MENU =====");
            System.out.println("1. Set PIN");
            System.out.println("2. Change PIN");
            System.out.println("3. Deposit");
            System.out.println("4. Withdraw");
            System.out.println("5. Check Balance");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1: setPIN(); break;
                case 2: changePIN(); break;
                case 3: deposit(); break;
                case 4: withdraw(); break;
                case 5: checkBalance(); break;
                case 6: System.out.println("Thank you!"); return;
                default: System.out.println("Invalid option!");
            }
        }
    }

    // ------------------ SET PIN ------------------
    public static void setPIN() throws Exception {

        // Check if already has a PIN
        String check = "SELECT pin FROM users WHERE username = ?";
        PreparedStatement ps1 = conn.prepareStatement(check);
        ps1.setString(1, loggedUser);

        ResultSet rs = ps1.executeQuery();
        if (rs.next() && rs.getString("pin") != null) {
            System.out.println("PIN already set. Use Change PIN.");
            return;
        }

        System.out.print("Enter new PIN: ");
        String pin = sc.next();

        String sql = "UPDATE users SET pin = ? WHERE username = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, pin);
        ps.setString(2, loggedUser);

        ps.executeUpdate();
        System.out.println("PIN set successfully.");
    }

    // ------------------ CHANGE PIN ------------------
    public static void changePIN() throws Exception {

        System.out.print("Enter old PIN: ");
        String oldPin = sc.next();

        String sql = "SELECT pin FROM users WHERE username = ?";
        PreparedStatement ps1 = conn.prepareStatement(sql);
        ps1.setString(1, loggedUser);

        ResultSet rs = ps1.executeQuery();

        if (rs.next() && rs.getString("pin") != null && rs.getString("pin").equals(oldPin)) {

            System.out.print("Enter new PIN: ");
            String newPin = sc.next();

            String update = "UPDATE users SET pin = ? WHERE username = ?";
            PreparedStatement ps2 = conn.prepareStatement(update);
            ps2.setString(1, newPin);
            ps2.setString(2, loggedUser);

            ps2.executeUpdate();
            System.out.println("PIN changed successfully.");
        } else {
            System.out.println("Incorrect old PIN.");
        }
    }

    // ------------------ DEPOSIT ------------------
    public static void deposit() throws Exception {
        System.out.print("Enter amount to deposit: ");
        double amt = sc.nextDouble();

        String sql = "UPDATE users SET balance = balance + ? WHERE username = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setDouble(1, amt);
        ps.setString(2, loggedUser);

        ps.executeUpdate();
        System.out.println("Amount deposited successfully.");
    }

    // ------------------ WITHDRAW ------------------
    public static void withdraw() throws Exception {

        System.out.print("Enter amount to withdraw: ");
        double amt = sc.nextDouble();

        String sql = "SELECT balance FROM users WHERE username = ?";
        PreparedStatement ps1 = conn.prepareStatement(sql);
        ps1.setString(1, loggedUser);

        ResultSet rs = ps1.executeQuery();

        if (rs.next()) {
            double bal = rs.getDouble("balance");

            if (bal >= amt) {
                String update = "UPDATE users SET balance = balance - ? WHERE username = ?";
                PreparedStatement ps2 = conn.prepareStatement(update);
                ps2.setDouble(1, amt);
                ps2.setString(2, loggedUser);

                ps2.executeUpdate();
                System.out.println("Withdraw successful.");
            } else {
                System.out.println("Insufficient balance.");
            }
        }
    }

    // ------------------ BALANCE CHECK ------------------
    public static void checkBalance() throws Exception {

        String sql = "SELECT balance FROM users WHERE username = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, loggedUser);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("Current Balance: " + rs.getDouble("balance"));
        }
    }
}