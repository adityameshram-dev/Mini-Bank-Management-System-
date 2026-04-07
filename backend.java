import java.sql.*;
import java.util.Random;

public class backend {

    private Connection con;

    public backend(Connection con) {
        this.con = con;
    }

    public long login(String username, String pass) {
        long accNo = 0;
        try {
            String sql = "SELECT account_number FROM users WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                accNo = rs.getLong(1);
            } else {
                accNo = -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accNo;
    }

    public boolean deposit(long accNo, double amount, String username, String pass) {
        boolean flag = false;
        try {
            if (amount > 0) {

                String sql = "UPDATE users SET balance = balance + ? WHERE account_number = ? AND username = ? AND password = ?";
                PreparedStatement ps = con.prepareStatement(sql);

                ps.setDouble(1, amount);
                ps.setLong(2, accNo);
                ps.setString(3, username);
                ps.setString(4, pass);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    flag = true;
                    logTransaction(0, accNo, amount, "Deposited");
                } else {
                    flag = false;
                }

            } else {
                System.out.println("Amount should not be zero ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean withdraw(long accNo, double amount, String username, String pass) {
        boolean flag = false;
        try {
            double bal = getBalance(accNo, pass);

            if (bal >= amount && amount > 0) {

                String sql = "UPDATE users SET balance = balance - ? WHERE account_number = ? AND username = ? AND password = ?";

                PreparedStatement ps = con.prepareStatement(sql);

                ps.setDouble(1, amount);
                ps.setLong(2, accNo);
                ps.setString(3, username);
                ps.setString(4, pass);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    flag = true;
                    logTransaction(accNo, 0, amount, "Withdrawl");
                } else {
                    flag = false;
                }
            } else {
                System.out.println("Insufficient Balance");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean transfer(long accNoRecever, long accNoSender, double amount, String username, String pass) {
        boolean flag = false;
        try {
            con.setAutoCommit(false);

            String sqldeduct = "UPDATE users SET balance = balance - ? WHERE account_number = ? AND username = ? AND password = ?";
            String sqladd = "UPDATE users SET balance = balance + ? WHERE account_number = ?";

            double balsender = getBalance(accNoSender, pass);

            if (balsender >= amount && amount > 0) {

                PreparedStatement psdeduct = con.prepareStatement(sqldeduct);
                psdeduct.setDouble(1, amount);
                psdeduct.setLong(2, accNoSender);
                psdeduct.setString(3, username);
                psdeduct.setString(4, pass);
                int rows1 = psdeduct.executeUpdate();

                PreparedStatement psadd = con.prepareStatement(sqladd);
                psadd.setDouble(1, amount);
                psadd.setLong(2, accNoRecever);
                int rows2 = psadd.executeUpdate();

                if (rows1 > 0 && rows2 > 0) {
                    con.commit();
                    flag = true;
                    logTransaction(accNoSender, accNoRecever, amount, "Transferred");
                    logTransaction(accNoRecever, accNoSender, amount, "Received");
                } else {
                    con.rollback();
                    flag = false;
                }
            }
        } catch (Exception e) {
            try {
                if (con != null)
                    con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return flag;
    }

    public long create_account(String fullname, String address, long phone, String username, String password) {

        Long accountnumber = generateaccountno();
        try {

            String sql = "INSERT INTO users (full_name, address, phone, username, password, account_number) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, fullname);
            ps.setString(2, address);
            ps.setLong(3, phone);
            ps.setString(4, username);
            ps.setString(5, password);

            ps.setLong(6, accountnumber);

            int rows = ps.executeUpdate();

            if (rows > 0) {

            } else {
                accountnumber = (long) -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountnumber;
    }

    private void logTransaction(long senderAcc, long receiverAcc, double amount, String type) {
        try {
            String sql = "INSERT INTO transactions (sender_account, receiver_account, amount, type) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, senderAcc);
            ps.setLong(2, receiverAcc);
            ps.setDouble(3, amount);
            ps.setString(4, type);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get transaction history for an account
    public ResultSet getTransactionHistory(long accNo) {
        try {
            String sql = "SELECT txn_id, sender_account, receiver_account, amount, type, txn_date " +
                    "FROM transactions " +
                    "WHERE sender_account = ? OR receiver_account = ? " +
                    "ORDER BY txn_date DESC " +
                    "LIMIT 50"; // Last 50 transactions
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, accNo);
            ps.setLong(2, accNo);
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public double getBalance(long accNO,String pass) {
        double balance = -1;
        try {

            String sql = "SELECT balance FROM users WHERE account_number = ? and password = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, accNO);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                balance = rs.getDouble("balance");
            } else {
                throw new SQLException("Account not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return balance;
    }

    public double checkbalance(long accNo, String pass) {
        double bal = -1;
        try {
            bal = getBalance(accNo, pass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bal;
    }

    public long generateaccountno() {
        Random random = new Random();
        long randomLong = random.nextLong(1000, 5000 + 1);
        return randomLong;
    }
}
