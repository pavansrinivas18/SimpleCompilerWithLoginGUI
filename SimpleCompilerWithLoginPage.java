import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class SimpleCompilerWithLoginPage {

    public static void main(String[] args) {
        // Launch the login page first
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}

// LoginPage class
class LoginPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        // Frame properties
        setTitle("Login - Simple Compiler");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JLabel statusLabel = new JLabel(" ");

        // Panel for form layout
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(statusLabel);

        // Add panel to the frame
        add(panel, BorderLayout.CENTER);

        // Action listener for the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Hardcoded credentials for simplicity
                if (username.equals("admin") && password.equals("1234")) {
                    statusLabel.setText("Login successful!");
                    dispose(); // Close login window
                    new CompilerWindow(); // Open compiler window
                } else {
                    statusLabel.setText("Invalid credentials. Try again.");
                }
            }
        });

        // Display the login page
        setVisible(true);
    }
}

// CompilerWindow class
class CompilerWindow extends JFrame {

    private JTextArea inputArea;
    private JTextArea outputArea;

    public CompilerWindow() {
        // Frame properties
        setTitle("Simple Compiler");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create input and output areas
        inputArea = new JTextArea(10, 40);
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);

        // Buttons for "Run" and "Clear"
        JButton runButton = new JButton("Run");
        JButton clearButton = new JButton("Clear");

        // Action listener for "Run" button
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputArea.getText();
                if (SimpleCompiler.isSyntaxValid(input)) {
                    try {
                        int result = SimpleCompiler.evaluateExpression(input);
                        outputArea.setText("Result: " + result);
                    } catch (Exception ex) {
                        outputArea.setText("Error: " + ex.getMessage());
                    }
                } else {
                    outputArea.setText("Syntax Error in Expression.");
                }
            }
        });

        // Action listener for "Clear" button
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputArea.setText("");
                outputArea.setText("");
            }
        });

        // Layout for compiler window
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(inputArea), BorderLayout.NORTH);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(runButton);
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add panel to frame
        add(panel);
        setVisible(true);
    }
}

// SimpleCompiler class (handles basic arithmetic operations)
class SimpleCompiler {

    // Syntax validation (basic check for numbers, operators, and parentheses)
    public static boolean isSyntaxValid(String input) {
        int balance = 0;
        boolean expectingNumber = true;

        String[] tokens = input.split("\\s+");
        for (String token : tokens) {
            if (token.matches("\\d+")) { // Numbers
                if (!expectingNumber) return false;
                expectingNumber = false;
            } else if (token.matches("[+\\-*/]")) { // Operators
                if (expectingNumber) return false;
                expectingNumber = true;
            } else if (token.equals("(")) { // Open parenthesis
                balance++;
            } else if (token.equals(")")) { // Close parenthesis
                balance--;
                if (balance < 0) return false;
            } else {
                return false; // Invalid token
            }
        }

        return balance == 0 && !expectingNumber;
    }

    // Evaluate arithmetic expressions
    public static int evaluateExpression(String input) throws Exception {
        Stack<Integer> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        String[] tokens = input.split("\\s+");

        for (String token : tokens) {
            if (token.matches("\\d+")) { // Numbers
                values.push(Integer.parseInt(token));
            } else if (token.equals("(")) { // Open parenthesis
                operators.push('(');
            } else if (token.equals(")")) { // Close parenthesis
                while (operators.peek() != '(') {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop(); // Remove '('
            } else if (token.matches("[+\\-*/]")) { // Operators
                while (!operators.isEmpty() && hasPrecedence(token.charAt(0), operators.peek())) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(token.charAt(0));
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private static boolean hasPrecedence(char current, char top) {
        if (top == '(' || top == ')') return false;
        if ((current == '*' || current == '/') && (top == '+' || top == '-')) return false;
        return true;
    }

    private static int applyOperator(char operator, int b, int a) throws Exception {
        switch (operator) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new Exception("Division by zero");
                return a / b;
            default: throw new Exception("Invalid operator: " + operator);
        }
    }
}