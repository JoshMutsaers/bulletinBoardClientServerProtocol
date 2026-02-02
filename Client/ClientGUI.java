import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {

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

    public ClientGUI() {
        setTitle("Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildConnectionPanel();
        buildCenterPanel();
        buildInputPanel();

        add(connectionPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setSize(500, 400);
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

    private void buildCenterPanel() {
        centerPanel = new JPanel(new BorderLayout());
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageScrollPane = new JScrollPane(messageArea);
        centerPanel.add(messageScrollPane, BorderLayout.CENTER);
    }

    private void buildInputPanel() {
        inputPanel = new JPanel(new BorderLayout(5, 0));
        inputField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> onSendClicked());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
    }

    // --- Stubs: add your logic here ---
    protected void onConnectClicked() { }
    protected void onDisconnectClicked() { }
    protected void onSendClicked() { }

    // --- Getters: use these to read/write UI ---
    public JTextField getIpField() { return ipField; }
    public JTextField getPortField() { return portField; }
    public JButton getConnectButton() { return connectButton; }
    public JButton getDisconnectButton() { return disconnectButton; }
    public JTextArea getMessageArea() { return messageArea; }
    public JTextField getInputField() { return inputField; }
    public JButton getSendButton() { return sendButton; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientGUI gui = new ClientGUI();
            gui.setVisible(true);
        });
    }
}
