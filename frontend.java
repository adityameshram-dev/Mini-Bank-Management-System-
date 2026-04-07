import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class frontend extends JFrame {
    private Connection con;
    private backend db;

    // Logged-in user info
    private String loggedInUsername = null;
    private long loggedInAccNo = 0;

    private JTabbedPane mainTabbedPane;

    private JTable transactionTable;
    private DefaultTableModel tableModel;

    public frontend() {
        con = DBConnection.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed!");
            System.exit(0);
        }
        db = new backend(con);

        setTitle("Mini Bank");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        showInitialPage();
    }

    // ─── Helper: Button ───────────────────────────────────────────────────────
    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return b;
    }

    // ─── Helper: Label ────────────────────────────────────────────────────────
    private JLabel lbl(String text, int size, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size));
        return l;
    }

    // ─── Helper: Styled TextField ─────────────────────────────────────────────
    private JTextField field() {
        JTextField f = new JTextField(15);
        return f;
    }

    // INITIAL PAGE

    private void showInitialPage() {
        getContentPane().removeAll();
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(52, 152, 219));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(52, 152, 219));
        box.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel title = lbl("Mini Bank", 36, true);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = lbl("GP Sakoli", 16, false);
        sub.setForeground(Color.WHITE);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        JButton btnLogin = btn("  Login  ", new Color(46, 204, 113));
        JButton btnCreate = btn("  Create Account  ", new Color(241, 196, 15));
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnCreate.setAlignmentX(CENTER_ALIGNMENT);

        btnLogin.addActionListener(e -> showLoginPage());
        btnCreate.addActionListener(e -> showCreateAccountPage());

        box.add(title);
        box.add(Box.createVerticalStrut(10));
        box.add(sub);
        box.add(Box.createVerticalStrut(30));
        box.add(btnLogin);
        box.add(Box.createVerticalStrut(15));
        box.add(btnCreate);

        p.add(box);
        getContentPane().add(p);
        revalidate();
        repaint();
    }

    // LOGIN PAGE

    private void showLoginPage() {
        getContentPane().removeAll();
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(236, 240, 241));

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 15));
        form.setBackground(new Color(236, 240, 241));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtUser = field();
        JPasswordField txtPass = new JPasswordField(15);

        form.add(lbl("Username:", 14, false));
        form.add(txtUser);
        form.add(lbl("Password:", 14, false));
        form.add(txtPass);

        JButton btnLogin = btn("Login", new Color(46, 204, 113));
        JButton btnBack = btn("Back", new Color(149, 165, 166));

        btnLogin.addActionListener(e -> {
            try {
                String username = txtUser.getText().trim();
                String password = new String(txtPass.getPassword());
                long accNo = db.login(username, password);

                if (accNo > 0) {
                    loggedInUsername = username;
                    // loggedInPassword = password;
                    loggedInAccNo = accNo;
                    showDashboard();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid username !");
            }
        });
        btnBack.addActionListener(e -> showInitialPage());

        form.add(btnLogin);
        form.add(btnBack);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(new Color(236, 240, 241));
        wrap.setBorder(BorderFactory.createTitledBorder("  Login  "));
        wrap.add(form);

        p.add(wrap);
        getContentPane().add(p);
        revalidate();
        repaint();
    }

    // CREATE ACCOUNT PAGE

    private void showCreateAccountPage() {
        getContentPane().removeAll();
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(236, 240, 241));

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 12));
        form.setBackground(new Color(236, 240, 241));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtName = field();
        JTextField txtAddress = field();
        JTextField txtPhone = field();
        JTextField txtUser = field();
        JPasswordField txtPass = new JPasswordField(15);

        form.add(lbl("Full Name:", 14, false));
        form.add(txtName);
        form.add(lbl("Address:", 14, false));
        form.add(txtAddress);
        form.add(lbl("Phone:", 14, false));
        form.add(txtPhone);
        form.add(lbl("Username:", 14, false));
        form.add(txtUser);
        form.add(lbl("Password:", 14, false));
        form.add(txtPass);

        JButton btnCreate = btn("Create Account", new Color(52, 152, 219));
        JButton btnBack = btn("Back", new Color(149, 165, 166));

        btnCreate.addActionListener(e -> {
            try {
                String fullname = txtName.getText().trim();
                String address = txtAddress.getText().trim();
                long phone = Long.parseLong(txtPhone.getText().trim());
                String username = txtUser.getText().trim();
                String password = new String(txtPass.getPassword());

                if (fullname.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields!");
                    return;
                }

                long accNo = db.create_account(fullname, address, phone, username, password);

                if (accNo != -1) {
                    JOptionPane.showMessageDialog(this,
                            "Account created successfully!\n" +
                                    "Your Account Number: " + accNo + "\n" +
                                    "Please note it down and login.",
                            "Account Created", JOptionPane.INFORMATION_MESSAGE);
                    showLoginPage();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create account. Try again!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid phone number!");
            }
        });
        btnBack.addActionListener(e -> showInitialPage());

        form.add(btnCreate);
        form.add(btnBack);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(new Color(236, 240, 241));
        wrap.setBorder(BorderFactory.createTitledBorder("  Create Account  "));
        wrap.add(form);

        p.add(wrap);
        getContentPane().add(p);
        revalidate();
        repaint();
    }

    // DASHBOARD

    private void showDashboard() {
        getContentPane().removeAll();
        JPanel main = new JPanel(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(new Color(52, 73, 94));
        JLabel title = lbl("Mini Bank  |  Welcome, " + loggedInUsername, 15, true);
        title.setForeground(Color.WHITE);
        JButton btnLogout = btn("Logout", new Color(231, 76, 60));
        btnLogout.addActionListener(e -> {
            loggedInUsername = null;
            showInitialPage();
        });
        header.add(title);
        header.add(btnLogout);
        main.add(header, BorderLayout.NORTH);

        // Tabs
        mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainTabbedPane.addTab("Deposit", createDepositPanel());
        mainTabbedPane.addTab("Withdraw", createWithdrawPanel());
        mainTabbedPane.addTab("Transfer", createTransferPanel());
        mainTabbedPane.addTab("Check Balance", createBalancePanel());
        mainTabbedPane.addTab("Transaction History", createTransactionHistoryPanel());
        main.add(mainTabbedPane, BorderLayout.CENTER);

        getContentPane().add(main);
        revalidate();
        repaint();
        refreshTransactionHistory();
    }

    // DEPOSIT TAB

    private JPanel createDepositPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 15));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JTextField txtAmount = field();
        JPasswordField password = new JPasswordField();

        form.add(lbl("Account Number:", 14, false));
        form.add(lbl(String.valueOf(loggedInAccNo), 14, true));
        form.add(lbl("Amount (₹):", 14, false));
        form.add(txtAmount);
        form.add(lbl("Password", 14, false));
        form.add(password);

        JButton btnDeposit = btn("Deposit", new Color(46, 204, 113));
        btnDeposit.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(txtAmount.getText().trim());
                String pass = new String(password.getPassword());
                if (pass.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(p,
                            "Password cannot be empty!",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                boolean ok = db.deposit(loggedInAccNo, amount, loggedInUsername, pass);
                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "₹" + String.format("%.2f", amount) + " deposited successfully!\n",
                            "Deposit Successful", JOptionPane.INFORMATION_MESSAGE);
                    txtAmount.setText("");
                    password.setText("");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Deposit failed! Check amount or password.",
                            "Deposit Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid amount!");
            }
        });

        form.add(btnDeposit);
        form.add(new JLabel());

        p.add(form);
        return p;
    }

    // WITHDRAW TAB

    private JPanel createWithdrawPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 15));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JTextField txtAmount = field();
        JPasswordField password = new JPasswordField();

        form.add(lbl("Account Number:", 14, false));
        form.add(lbl(String.valueOf(loggedInAccNo), 14, true));
        form.add(lbl("Amount (₹):", 14, false));
        form.add(txtAmount);
        form.add(lbl("Password", 14, false));
        form.add(password);

        JButton btnWithdraw = btn("Withdraw", new Color(231, 76, 60));
        btnWithdraw.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(txtAmount.getText().trim());
                String pass = new String(password.getPassword());
                if (pass.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(p,
                            "Password cannot be empty!",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                boolean ok = db.withdraw(loggedInAccNo, amount, loggedInUsername, pass);
                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "₹" + String.format("%.2f", amount) + " withdrawn successfully!", "Withdraw Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                    txtAmount.setText("");
                    password.setText("");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Withdrawal failed!\nInsufficient balance or invalid details.",
                            "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid amount!");
            }
        });

        form.add(btnWithdraw);
        form.add(new JLabel());

        p.add(form);
        return p;
    }

    // TRANSFER TAB

    private JPanel createTransferPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 15));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JTextField txtReceiverAccNo = field();
        JTextField txtAmount = field();
        JPasswordField password = new JPasswordField();

        form.add(lbl("Your Account No.:", 14, false));
        form.add(lbl(String.valueOf(loggedInAccNo), 14, true));
        form.add(lbl("Receiver Account No.:", 14, false));
        form.add(txtReceiverAccNo);
        form.add(lbl("Amount (₹):", 14, false));
        form.add(txtAmount);
        form.add(lbl("Password", 14, false));
        form.add(password);

        JButton btnTransfer = btn("Transfer", new Color(52, 152, 219));
        btnTransfer.addActionListener(e -> {
            try {
                long receiverAccNo = Long.parseLong(txtReceiverAccNo.getText().trim());
                double amount = Double.parseDouble(txtAmount.getText().trim());
                String pass = new String(password.getPassword());
                if (pass.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(p,
                            "Password cannot be empty!",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (receiverAccNo == loggedInAccNo) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot transfer to your own account!",
                            "Invalid Transfer",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Transfer ₹" + String.format("%.2f", amount) +
                                " to Account " + receiverAccNo + "?",
                        "Confirm Transfer", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean ok = db.transfer(receiverAccNo, loggedInAccNo, amount,
                            loggedInUsername, pass);
                    if (ok) {
                        JOptionPane.showMessageDialog(this,
                                "₹" + String.format("%.2f", amount) + " transferred successfully!",
                                "Transfer Successful", JOptionPane.INFORMATION_MESSAGE);

                        txtReceiverAccNo.setText("");
                        txtAmount.setText("");
                        password.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Transfer failed!\nInsufficient balance or invalid receiver account.",
                                "Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid account number and amount!");
            }
        });

        form.add(btnTransfer);
        form.add(new JLabel());

        p.add(form);
        return p;
    }

    private JPanel createTransactionHistoryPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table columns
        String[] cols = { "Sr No.", "Type", "Sender", "Receiver", "Amount (₹)", "Date" };

        // Create table model (non-editable)
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        // Create table with styling
        transactionTable = new JTable(tableModel);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        transactionTable.setRowHeight(28);
        transactionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        transactionTable.getTableHeader().setBackground(new Color(52, 73, 94));
        transactionTable.getTableHeader().setForeground(Color.WHITE);

        // Add table to scroll pane
        p.add(new JScrollPane(transactionTable), BorderLayout.CENTER);

        // Refresh button at bottom
        JButton btnRefresh = btn("Refresh", new Color(52, 152, 219));
        btnRefresh.addActionListener(e -> refreshTransactionHistory());
        JPanel south = new JPanel();
        south.setBackground(Color.WHITE);
        south.add(btnRefresh);
        p.add(south, BorderLayout.SOUTH);

        return p;
    }

    private void refreshTransactionHistory() {
        // Clear existing rows
        tableModel.setRowCount(0);

        try {
            ResultSet rs = db.getTransactionHistory(loggedInAccNo);

            if (rs == null) {
                return;
            }

            int srNo = 1;
            while (rs.next()) {
                String type = rs.getString("type");
                String sender, receiver;

                // Set sender and receiver based on transaction type
                if (type.equals("DEPOSIT")) {
                    sender = "BANK";
                    receiver = String.valueOf(rs.getLong("receiver_account"));
                } else if (type.equals("WITHDRAWAL")) {
                    sender = String.valueOf(rs.getLong("sender_account"));
                    receiver = "BANK";
                } else if (type.equals("TRANSFER")) {
                    sender = String.valueOf(rs.getLong("sender_account"));
                    receiver = String.valueOf(rs.getLong("receiver_account"));
                } else {
                    sender = String.valueOf(rs.getLong("sender_account"));
                    receiver = String.valueOf(rs.getLong("receiver_account"));
                }

                // Format amount
                double amount = rs.getDouble("amount");
                String formattedAmount = String.format("₹ %.2f", amount);

                // Format date (remove milliseconds)
                Timestamp timestamp = rs.getTimestamp("txn_date");
                String dateStr = "";
                if (timestamp != null) {
                    java.util.Date date = new java.util.Date(timestamp.getTime());
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                    dateStr = sdf.format(date);
                } else {
                    dateStr = "N/A";
                }

                // Add row to table
                tableModel.addRow(new Object[] {
                        srNo++,
                        type,
                        sender,
                        receiver,
                        formattedAmount,
                        dateStr
                });
            }

            // If no transactions found, show message
            if (srNo == 1) {
                tableModel.addRow(new Object[] { "-", "No transactions", "-", "-", "-", "-" });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading transaction history!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // CHECK BALANCE TAB

    private JPanel createBalancePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(236, 240, 241));

        JPanel box = new JPanel(new GridLayout(5, 2, 15, 15));
        box.setBackground(new Color(236, 240, 241));
        box.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JLabel lblAccNo = lbl(String.valueOf(loggedInAccNo), 18, true);
        JLabel lblName = lbl(loggedInUsername, 18, true);
        JLabel lblBal = lbl(" ? ", 18, true);
        JPasswordField password = new JPasswordField();

        lblAccNo.setForeground(new Color(41, 128, 185));
        lblName.setForeground(new Color(39, 174, 96));
        lblBal.setForeground(new Color(41, 128, 185));

        box.add(lbl("Account Number:", 15, false));
        box.add(lblAccNo);
        box.add(lbl("Username:", 15, false));
        box.add(lblName);
        box.add(lbl("Balance:", 15,false));
        box.add(lblBal);
        box.add(lbl("Password", 14, false));
        box.add(password);

        JButton btnRefresh = btn("Check Balance", new Color(52, 152, 219));
        btnRefresh.addActionListener(e -> {
            String pass = new String(password.getPassword());
            if (pass.trim().isEmpty()) {
                JOptionPane.showMessageDialog(p,
                        "Password cannot be empty!",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            double bal = db.checkbalance(loggedInAccNo, pass);
            if (bal == -1) {
                JOptionPane.showMessageDialog(p,
                        "Invalid password!",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);
                password.setText("");
                lblBal.setText("?");
            } else {
                lblBal.setText("₹ " + String.format("%.2f", bal));
            }
        });

        box.add(btnRefresh);
        box.add(new JLabel());

        p.add(box);
        return p;
    }

    // MAIN
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new frontend().setVisible(true));
    }
}