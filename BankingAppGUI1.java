import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class Bank {
    private String accno;
    private String name;
    private String acc_type;
    private long balance;

    public void openAccount(String accno, String acc_type, String name, long balance) {
        this.accno = accno;
        this.acc_type = acc_type;
        this.name = name;
        this.balance = balance;
    }

    public String showAccount() {
        return "Name: " + name + "\nAccount Type: " + acc_type + "\nAccount Number: " + accno + "\nBalance: " + balance;
    }

    public void deposit(long amt) {
        balance += amt;
    }

    public boolean withdraw(long amt) {
        if (balance >= amt) {
            balance -= amt;
            return true;
        } else {
            return false;
        }
    }

    public boolean search(String ac_no) {
        return accno.equals(ac_no);
    }

    public void modify(String name, String acc_type, long balance) {
        this.name = name;
        this.acc_type = acc_type;
        this.balance = balance;
    }

    public String getAccNo() {
        return accno;
    }

    public String getName() {
        return name;
    }

    public String getAccType() {
        return acc_type;
    }

    public long getBalance() {
        return balance;
    }
}


public class BankingAppGUI1 {
    private static ArrayList<Bank> banks = new ArrayList<>();
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;

    public static void main(String[] args) {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("banking");
        collection = database.getCollection("accounts");

JFrame frame = new JFrame("BANK MANAGING APPLICATION");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 3, 5, 5)); // 2 rows, 3 columns, with 5px horizontal and vertical gap

        JButton openAccountButton = new JButton("Open Account");
        JButton displayAllButton = new JButton("Display All Accounts");
        JButton searchButton = new JButton("Search Account");
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton deleteButton = new JButton("Delete Account");
        JButton modifyButton = new JButton("Modify Account");
        JButton exitButton = new JButton("Exit");

        // Set button colors to purple with white text
        Color buttonColor = new Color(128, 0, 128);
        openAccountButton.setBackground(buttonColor);
        openAccountButton.setForeground(Color.WHITE); // Set text color to white
        displayAllButton.setBackground(buttonColor);
        displayAllButton.setForeground(Color.WHITE);
        searchButton.setBackground(buttonColor);
        searchButton.setForeground(Color.WHITE);
        depositButton.setBackground(buttonColor);
        depositButton.setForeground(Color.WHITE);
        withdrawButton.setBackground(buttonColor);
        withdrawButton.setForeground(Color.WHITE);
        deleteButton.setBackground(buttonColor);
        deleteButton.setForeground(Color.WHITE);
        modifyButton.setBackground(buttonColor);
        modifyButton.setForeground(Color.WHITE);
        exitButton.setBackground(buttonColor);
        exitButton.setForeground(Color.WHITE);

        buttonPanel.add(openAccountButton);
        buttonPanel.add(displayAllButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(exitButton);

        JPanel outputPanel = new JPanel();
        outputPanel.setBackground(new Color(230, 200, 255)); // Light purple background
        outputPanel.setLayout(new BorderLayout());

        JTextArea outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        outputPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(outputPanel, BorderLayout.CENTER);

        frame.add(mainPanel);

        openAccountButton.addActionListener(e -> openAccount(outputArea));
        displayAllButton.addActionListener(e -> displayAllAccounts(outputArea));
        searchButton.addActionListener(e -> searchAccount(outputArea));
        depositButton.addActionListener(e -> deposit(outputArea));
        withdrawButton.addActionListener(e -> withdraw(outputArea));
        deleteButton.addActionListener(e -> deleteAccount(outputArea));
        modifyButton.addActionListener(e -> modifyAccount(outputArea));
        exitButton.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }


    private static void openAccount(JTextArea outputArea) {
        JTextField accnoField = new JTextField();
        JTextField accTypeField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField balanceField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Account No:"));
        panel.add(accnoField);
        panel.add(new JLabel("Account Type:"));
        JComboBox<String> accountTypeDropdown = new JComboBox<>(new String[]{"Savings", "Checking", "Credit"});
        panel.add(accountTypeDropdown);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Balance:"));
        panel.add(balanceField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Open Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String accno = accnoField.getText();
            String accType = accountTypeDropdown.getSelectedItem().toString();
            String name = nameField.getText();
            long balance = Long.parseLong(balanceField.getText());

            Bank bank = new Bank();
            bank.openAccount(accno, accType, name, balance);
            banks.add(bank);
            insertAccountIntoMongoDB(accno, accType, name, balance);
            outputArea.append("Account Created Successfully!\n");
        }
    }

    // Helper method to insert account into MongoDB
    private static void insertAccountIntoMongoDB(String accno, String accType, String name, long balance) {
        Document document = new Document();
        document.append("accno", accno);
        document.append("accType", accType);
        document.append("name", name);
        document.append("balance", balance);
        collection.insertOne(document);
    }

private static void displayAllAccounts(JTextArea outputArea) {
        if (banks.isEmpty()) {
            outputArea.append("No accounts available.\n");
            return;
        }

        for (Bank bank : banks) {
            outputArea.append(bank.showAccount() + "\n\n");
        }
    }

    private static void searchAccount(JTextArea outputArea) {
        String accno = JOptionPane.showInputDialog("Enter Account No to Search:");
        if (accno == null || accno.isEmpty()) {
            return;
        }

        boolean found = false;
        for (Bank bank : banks) {
            if (bank.search(accno)) {
                outputArea.append(bank.showAccount() + "\n");
                found = true;
                break;
            }
        }

        if (!found) {
            outputArea.append("Account not found.\n");
        }
    }

    private static void deposit(JTextArea outputArea) {
        String accno = JOptionPane.showInputDialog("Enter Account No:");
        if (accno == null || accno.isEmpty()) {
            return;
        }

        Bank bank = getBankByAccNo(accno);
        if (bank == null) {
            outputArea.append("Account not found.\n");
            return;
        }

        String amountStr = JOptionPane.showInputDialog("Enter amount to deposit:");
        if (amountStr == null || amountStr.isEmpty()) {
            return;
        }

        long amount = Long.parseLong(amountStr);
        bank.deposit(amount);
        outputArea.append("Amount Deposited Successfully!\n");
    }

    private static void withdraw(JTextArea outputArea) {
        String accno = JOptionPane.showInputDialog("Enter Account No:");
        if (accno == null || accno.isEmpty()) {
            return;
        }

        Bank bank = getBankByAccNo(accno);
        if (bank == null) {
            outputArea.append("Account not found.\n");
            return;
        }

        String amountStr = JOptionPane.showInputDialog("Enter amount to withdraw:");
        if (amountStr == null || amountStr.isEmpty()) {
            return;
        }

        long amount = Long.parseLong(amountStr);
        if (bank.withdraw(amount)) {
            outputArea.append("Amount Withdrawn Successfully!\n");
        } else {
            outputArea.append("Insufficient Balance!\n");
        }
    }

    private static void deleteAccount(JTextArea outputArea) {
        String accno = JOptionPane.showInputDialog("Enter Account No to Delete:");
        if (accno == null || accno.isEmpty()) {
            return;
        }

        Bank bank = getBankByAccNo(accno);
        if (bank == null) {
            outputArea.append("Account not found.\n");
            return;
        }

        int confirmDelete = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this account?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirmDelete == JOptionPane.YES_OPTION) {
            banks.remove(bank);
            outputArea.append("Account Deleted Successfully!\n");
        }
    }

    private static void modifyAccount(JTextArea outputArea) {
        String accno = JOptionPane.showInputDialog("Enter Account No to Modify:");
        if (accno == null || accno.isEmpty()) {
            return;
        }

        Bank bank = getBankByAccNo(accno);
        if (bank == null) {
            outputArea.append("Account not found.\n");
            return;
        }

        JTextField nameField = new JTextField(bank.getName());
        JTextField accTypeField = new JTextField(bank.getAccType());
        JTextField balanceField = new JTextField(String.valueOf(bank.getBalance()));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Account Type:"));
        panel.add(accTypeField);
        panel.add(new JLabel("Balance:"));
        panel.add(balanceField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Modify Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String accType = accTypeField.getText();
            long balance = Long.parseLong(balanceField.getText());

            bank.modify(name, accType, balance);
            outputArea.append("Account Modified Successfully!\n");
        }
    }

    private static Bank getBankByAccNo(String accno) {
        for (Bank bank : banks) {
            if (bank.search(accno)) {
                return bank;
            }
        }
        return null;
    }
}

