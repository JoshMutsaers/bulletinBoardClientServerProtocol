import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
      this.setTitle("Client");
      this.setDefaultCloseOperation(3);
      this.setLayout(new BorderLayout());
      this.buildConnectionPanel();
      this.buildButtonsPanel();
      this.buildCenterPanel();
      this.buildPostPanel();
      this.buildInputPanel();

      JPanel var1 = new JPanel(new BorderLayout());
      var1.add(this.connectionPanel, "North");
      var1.add(this.buttonsPanel, "Center");
      this.add(var1, "North");
      this.add(this.centerPanel, "Center");
      this.add(this.postPanel, "East");
      this.add(this.inputPanel, "South");

      this.connection = new ClientConnection((var1x) -> {
         SwingUtilities.invokeLater(() -> {
            this.messageArea.append(var1x + "\n");

          
            if (var1x != null && var1x.startsWith("COLOURS ")) {
               this.updateColoursFromHandshake(var1x);
            }
         });
      }, (var1x) -> {
         SwingUtilities.invokeLater(() -> {
            this.messageArea.append(var1x + "\n");
            if ("connected".equals(var1x)) {
               this.setControlsEnabled(true);
            } else if ("disconnected".equals(var1x) || var1x.startsWith("error")) {
               this.setControlsEnabled(false);
            }
         });
      });

      this.setControlsEnabled(false);
      this.setSize(650, 450);
      this.setLocationRelativeTo((Component)null);
   }

  
   private void updateColoursFromHandshake(String var1) {

      String[] var2 = var1.trim().split("\\s+");
      DefaultComboBoxModel<String> var3 = new DefaultComboBoxModel<>();

      int var4;
      for(var4 = 1; var4 < var2.length; ++var4) {
         if (!var2[var4].isEmpty()) {
            var3.addElement(var2[var4]);
         }
      }

      if (var3.getSize() > 0) {
         this.postColorCombo.setModel(var3);
      }
   }

   private void buildConnectionPanel() {
      this.connectionPanel = new JPanel(new FlowLayout(0));
      this.connectionPanel.add(new JLabel("IP:"));
      this.ipField = new JTextField(12);
      this.connectionPanel.add(this.ipField);
      this.connectionPanel.add(new JLabel("Port:"));
      this.portField = new JTextField(5);
      this.connectionPanel.add(this.portField);
      this.connectButton = new JButton("Connect");
      this.connectButton.addActionListener((var1) -> {
         this.onConnectClicked();
      });
      this.connectionPanel.add(this.connectButton);
      this.disconnectButton = new JButton("Disconnect");
      this.disconnectButton.addActionListener((var1) -> {
         this.onDisconnectClicked();
      });
      this.connectionPanel.add(this.disconnectButton);
   }

   private void buildButtonsPanel() {
      this.buttonsPanel = new JPanel(new FlowLayout(0));
      this.getPinsButton = new JButton("GET PINS");
      this.getPinsButton.addActionListener((var1) -> {
         this.connection.sendLine("GET PINS");
      });
      this.buttonsPanel.add(this.getPinsButton);
      this.clearButton = new JButton("CLEAR");
      this.clearButton.addActionListener((var1) -> {
         this.connection.sendLine("CLEAR");
      });
      this.buttonsPanel.add(this.clearButton);
      this.shakeButton = new JButton("SHAKE");
      this.shakeButton.addActionListener((var1) -> {
         this.connection.sendLine("SHAKE");
      });
      this.buttonsPanel.add(this.shakeButton);
      this.pinUnpinButton = new JButton("PIN / UNPIN");
      this.pinUnpinButton.addActionListener((var1) -> {
         this.showPinUnpinDialog();
      });
      this.buttonsPanel.add(this.pinUnpinButton);
   }

   private void showPinUnpinDialog() {
      JTextField var1 = new JTextField(5);
      JTextField var2 = new JTextField(5);
      JPanel var3 = new JPanel(new FlowLayout());
      var3.add(new JLabel("x:"));
      var3.add(var1);
      var3.add(new JLabel("y:"));
      var3.add(var2);
      String[] var4 = new String[]{"PIN", "UNPIN", "Cancel"};
      int var5 = JOptionPane.showOptionDialog(this, var3, "PIN / UNPIN", -1, -1, (Icon)null, var4, var4[2]);
      if (var5 != 2) {
         String var6 = var1.getText().trim();
         String var7 = var2.getText().trim();

         int var8;
         int var9;
         try {
            var8 = Integer.parseInt(var6);
            var9 = Integer.parseInt(var7);
            if (var8 < 0 || var9 < 0) {
               throw new NumberFormatException("must be >= 0");
            }
         } catch (NumberFormatException var11) {
            JOptionPane.showMessageDialog(this, "x and y must be integers >= 0", "Invalid input", 2);
            return;
         }

         if (var5 == 0) {
            this.connection.sendLine("PIN " + var8 + " " + var9);
         } else if (var5 == 1) {
            this.connection.sendLine("UNPIN " + var8 + " " + var9);
         }
      }
   }

   private void buildCenterPanel() {
      this.centerPanel = new JPanel(new BorderLayout());
      this.messageArea = new JTextArea();
      this.messageArea.setEditable(false);
      this.messageArea.setLineWrap(true);
      this.messageScrollPane = new JScrollPane(this.messageArea);
      this.centerPanel.add(this.messageScrollPane, "Center");
   }

   private void buildPostPanel() {
      this.postPanel = new JPanel();
      this.postPanel.setLayout(new BoxLayout(this.postPanel, 1));
      this.postPanel.setBorder(BorderFactory.createTitledBorder("POST"));
      this.postPanel.add(new JLabel("x:"));
      this.postXField = new JTextField(6);
      this.postPanel.add(this.postXField);
      this.postPanel.add(new JLabel("y:"));
      this.postYField = new JTextField(6);
      this.postPanel.add(this.postYField);
      this.postPanel.add(new JLabel("color:"));

    
      
      String[] var1 = new String[]{"Loading..."};
      this.postColorCombo = new JComboBox<>(var1);

      this.postPanel.add(this.postColorCombo);
      this.postPanel.add(new JLabel("message:"));
      this.postMessageField = new JTextField(10);
      this.postPanel.add(this.postMessageField);
      this.postButton = new JButton("POST");
      this.postButton.addActionListener((var1x) -> {
         this.onPostClicked();
      });
      this.postPanel.add(this.postButton);
   }

   private void onPostClicked() {
      String var1 = this.postXField.getText().trim();
      String var2 = this.postYField.getText().trim();
      String var3 = this.postMessageField.getText().trim();
      if (var3.isEmpty()) {
         JOptionPane.showMessageDialog(this, "Message cannot be empty", "Invalid input", 2);
      } else {
         int var4;
         int var5;
         try {
            var4 = Integer.parseInt(var1);
            var5 = Integer.parseInt(var2);
            if (var4 < 0 || var5 < 0) {
               throw new NumberFormatException("must be >= 0");
            }
         } catch (NumberFormatException var7) {
            JOptionPane.showMessageDialog(this, "x and y must be integers >= 0", "Invalid input", 2);
            return;
         }

      
         String var6 = (String)this.postColorCombo.getSelectedItem();
         if (var6 == null) {
            JOptionPane.showMessageDialog(this, "No colour selected", "Invalid input", 2);
            return;
         }
         var6 = var6.trim();

         this.connection.sendLine("POST " + var4 + " " + var5 + " " + var6 + " " + var3);
      }
   }

   private void buildInputPanel() {
      this.inputPanel = new JPanel(new BorderLayout(5, 0));
      this.inputField = new JTextField();
      this.sendButton = new JButton("Send");
      this.sendButton.addActionListener((var1) -> {
         this.onSendClicked();
      });
      this.inputPanel.add(this.inputField, "Center");
      this.inputPanel.add(this.sendButton, "East");
   }

   protected void onConnectClicked() {
      String var1 = this.ipField.getText().trim();
      String var2 = this.portField.getText().trim();
      this.messageArea.append("connecting…\n");

      int var3;
      try {
         var3 = Integer.parseInt(var2);
      } catch (NumberFormatException var6) {
         this.messageArea.append("Bad port (not a number)\n");
         return;
      }

      try {
         this.connection.connect(var1, var3);
      } catch (Exception var5) {
         SwingUtilities.invokeLater(() -> {
            this.messageArea.append("connect failed: " + var5.getMessage() + "\n");
         });
      }
   }

    
   protected void onDisconnectClicked() {
      new Thread(() -> {
         try {

            this.connection.sendLine("DISCONNECT");
         } catch (Exception var2) {

         }
         this.connection.disconnect();
      }).start();
   }

   protected void onSendClicked() {
      String var1 = this.inputField.getText().trim();
      if (!var1.isEmpty()) {
         this.connection.sendLine(var1);
         this.inputField.setText("");
      }
   }

   public JTextField getIpField() {
      return this.ipField;
   }

   public JTextField getPortField() {
      return this.portField;
   }

   public JButton getConnectButton() {
      return this.connectButton;
   }

   public JButton getDisconnectButton() {
      return this.disconnectButton;
   }

   public JTextArea getMessageArea() {
      return this.messageArea;
   }

   public JTextField getInputField() {
      return this.inputField;
   }

   public JButton getSendButton() {
      return this.sendButton;
   }

   private void setControlsEnabled(boolean var1) {
      this.getPinsButton.setEnabled(var1);
      this.clearButton.setEnabled(var1);
      this.shakeButton.setEnabled(var1);
      this.pinUnpinButton.setEnabled(var1);
      this.postButton.setEnabled(var1);
      this.sendButton.setEnabled(var1);
   }

   public static void main(String[] var0) {
      SwingUtilities.invokeLater(() -> {
         ClientGUI var0x = new ClientGUI();
         var0x.setVisible(true);
      });
   }
}
