// Name: Gemechisa Tolesa
// ID : ugr/34496/16
// Section : 3
// Group : 5


import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Client1GUI {

    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;

    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    public Client1GUI(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            System.out.println("Gemechisa is connected.");

            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            createGUI();
            startListening();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection failed");
        }
    }

    private void createGUI() {
        frame = new JFrame("Client 1 - Gemechisa");
        chatArea = new JTextArea();
        chatArea.setEditable(false);

        messageField = new JTextField();
        sendButton = new JButton("Send");

        sendButton.addActionListener(e -> sendMessage());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void sendMessage() {
        try {
            String message = "Gemechisa: " + messageField.getText();
            out.write(message);
            out.newLine();
            out.flush();

            chatArea.append(message + "\n");
            messageField.setText("");

        } catch (IOException e) {
            chatArea.append("Message not sent\n");
        }
    }

    private void startListening() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    chatArea.append(msg + "\n");
                }
            } catch (IOException e) {
                chatArea.append("Connection closed\n");
            }
        }).start();
    }

    public static void main(String[] args) {
        new Client1GUI("localhost", 1234);
    }
}


