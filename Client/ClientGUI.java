import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {

    private ClientConnection connection;
    private JPanel connectionPanel;
    private JTextField ipField;
    private JTextField portField;
    private JButton connectButton;
    private JButton disconnectButton;

    private JPanel centerPanel;
    private JTextArea messageArea;
    private JScrollPane messageScrollPane;

    private JPanel inputPanel;
    private JTextField inputField;
    private JButton sendButton;

    private JPanel buttonsPanel;
    private JButton getPinsButton;
    private JButton clearButton;
    private JButton shakeButton;
    private JButton pinUnpinButton;

    private JPanel postPanel;
    private JTextField postXField;
    private JTextField postYField;
    private JComboBox<String> postColorCombo;
    private JTextField postMessageField;
    private JButton postButton;

    public ClientGUI() {
        setTitle("Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildConnectionPanel();
        buildButtonsPanel();
        buildCenterPanel();
        buildPostPanel();
        buildInputPanel();

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(connectionPanel, BorderLayout.NORTH);
        northPanel.add(buttonsPanel, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(postPanel, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        connection = new ClientConnection(
            line -> SwingUtilities.invokeLater(() -> {
                messageArea.append(line + "\n");
            }),
            status -> SwingUtilities.invokeLater(() -> {
                messageArea.append(status + "\n");
                if ("connected".equals(status)) {
                    setControlsEnabled(true);
                } else if ("disconnected".equals(status) || status.startsWith("error")) {
                    setControlsEnabled(false);
                }
            })
        );

        setControlsEnabled(false);

        setSize(650, 450);
        setLocationRelativeTo(null);
    }

    private void buildConnectionPanel() {
        connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionPanel.add(new JLabel("IP:"));
        ipField = new JTextField(12);
        connectionPanel.add(ipField);
        connectionPanel.add(new JLabel("Port:"));
        portField = new JTextField(5);
        connectionPanel.add(portField);
        connectButton = new JButton("Connect");
        connectButton.addActionListener(e -> onConnectClicked());
        connectionPanel.add(connectButton);
        disconnectButton = new JButton("Disconnect");
        disconnectButton.addActionListener(e -> onDisconnectClicked());
        connectionPanel.add(disconnectButton);
    }

    private void buildButtonsPanel() {
        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        getPinsButton = new JButton("GET PINS");
        getPinsButton.addActionListener(e -> connection.sendLine("GET PINS"));
        buttonsPanel.add(getPinsButton);
        clearButton = new JButton("CLEAR");
        clearButton.addActionListener(e -> connection.sendLine("CLEAR"));
        buttonsPanel.add(clearButton);
        shakeButton = new JButton("SHAKE");
        shakeButton.addActionListener(e -> connection.sendLine("SHAKE"));
        buttonsPanel.add(shakeButton);
        pinUnpinButton = new JButton("PIN / UNPIN");
        pinUnpinButton.addActionListener(e -> showPinUnpinDialog());
        buttonsPanel.add(pinUnpinButton);
    }

    private void showPinUnpinDialog() {
        JTextField xField = new JTextField(5);
        JTextField yField = new JTextField(5);
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("x:"));
        panel.add(xField);
        panel.add(new JLabel("y:"));
        panel.add(yField);
        String[] options = { "PIN", "UNPIN", "Cancel" };
        int choice = JOptionPane.showOptionDialog(this, panel, "PIN / UNPIN",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[2]);
        if (choice == 2) return; // Cancel
        String xs = xField.getText().trim();
        String ys = yField.getText().trim();
        int x, y;
        try {
            x = Integer.parseInt(xs);
            y = Integer.parseInt(ys);
            if (x < 0 || y < 0) throw new NumberFormatException("must be >= 0");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "x and y must be integers >= 0", "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (choice == 0) connection.sendLine("PIN " + x + " " + y);
        else if (choice == 1) connection.sendLine("UNPIN " + x + " " + y);
    }

    private void buildCenterPanel() {
        centerPanel = new JPanel(new BorderLayout());
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageScrollPane = new JScrollPane(messageArea);
        centerPanel.add(messageScrollPane, BorderLayout.CENTER);
    }

    private void buildPostPanel() {
        postPanel = new JPanel();
        postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.Y_AXIS));
        postPanel.setBorder(BorderFactory.createTitledBorder("POST"));
        postPanel.add(new JLabel("x:"));
        postXField = new JTextField(6);
        postPanel.add(postXField);
        postPanel.add(new JLabel("y:"));
        postYField = new JTextField(6);
        postPanel.add(postYField);
        postPanel.add(new JLabel("color:"));
        // TODO: replace with server-provided colors from handshake
        String[] colors = { "red", "blue", "green", "yellow", "black", "white" };
        postColorCombo = new JComboBox<>(colors);
        postPanel.add(postColorCombo);
        postPanel.add(new JLabel("message:"));
        postMessageField = new JTextField(10);
        postPanel.add(postMessageField);
        postButton = new JButton("POST");
        postButton.addActionListener(e -> onPostClicked());
        postPanel.add(postButton);
    }

    private void onPostClicked() {
        String xs = postXField.getText().trim();
        String ys = postYField.getText().trim();
        String message = postMessageField.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Message cannot be empty", "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int x, y;
        try {
            x = Integer.parseInt(xs);
            y = Integer.parseInt(ys);
            if (x < 0 || y < 0) throw new NumberFormatException("must be >= 0");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "x and y must be integers >= 0", "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String color = (String) postColorCombo.getSelectedItem();
        connection.sendLine("POST " + x + " " + y + " " + color + " " + message);
    }

    private void buildInputPanel() {
        inputPanel = new JPanel(new BorderLayout(5, 0));
        inputField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> onSendClicked());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
    }

    protected void onConnectClicked() {
        String ip = ipField.getText().trim();
        String portStr = portField.getText().trim();
        messageArea.append("connecting…\n");
        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            messageArea.append("Bad port (not a number)\n");
            return;
        }
        try {
            connection.connect(ip, port);
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> messageArea.append("connect failed: " + e.getMessage() + "\n"));
        }
    }

    protected void onDisconnectClicked() {
        connection.disconnect();
    }

    protected void onSendClicked() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;
        connection.sendLine(text);
        inputField.setText("");
    }

    // --- Getters: use these to read/write UI ---
    public JTextField getIpField() { return ipField; }
    public JTextField getPortField() { return portField; }
    public JButton getConnectButton() { return connectButton; }
    public JButton getDisconnectButton() { return disconnectButton; }
    public JTextArea getMessageArea() { return messageArea; }
    public JTextField getInputField() { return inputField; }
    public JButton getSendButton() { return sendButton; }

    private void setControlsEnabled(boolean connected) {
        getPinsButton.setEnabled(connected);
        clearButton.setEnabled(connected);
        shakeButton.setEnabled(connected);
        pinUnpinButton.setEnabled(connected);
        postButton.setEnabled(connected);
        sendButton.setEnabled(connected);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientGUI gui = new ClientGUI();
            gui.setVisible(true);
        });
    }
}
