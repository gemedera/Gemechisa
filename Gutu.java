// Name: Gemechisa Tolesa
// ID : ugr/34496/16
// Section : 3
// Group : 5




import java.io.*;
import java.net.*;

public class Gutu {
    private Socket socket;
    private BufferedWriter out;

    public Gutu(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            System.out.println("Gutu is connected.");

            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Start a thread to listen for incoming messages
            new Thread(new IncomingMessageHandler(socket)).start();

            // Allow sending messages
            sendMessageLoop();
        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
        }
    }

    private void sendMessageLoop() {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        String message;
        try {
            while ((message = userInput.readLine()) != null) {
                out.write(message);
                out.newLine();
                out.flush();
                if (message.equalsIgnoreCase("Bye")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        try {
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Gutu("localhost", 1234);
    }
}