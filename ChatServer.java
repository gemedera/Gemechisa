// Name: Gemechisa Tolesa
// ID : ugr/34496/16
// Section : 3
// Group : 5



import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {
    private static List<ChatClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Server is listening on port 1234...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());
                ChatClientHandler clientHandler = new ChatClientHandler(socket);
                clientHandlers.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
        }
    }

    private static class ChatClientHandler extends Thread {
        private Socket socket;
        private BufferedWriter out;

        public ChatClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                this.out = bufferedWriter;

                String msgFromClient;
                while ((msgFromClient = bufferedReader.readLine()) != null) {
                    System.out.println("Client: " + msgFromClient);
                    sendMessageToOtherClients(msgFromClient);
                    if (msgFromClient.equalsIgnoreCase("Bye")) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                removeClientHandler();
                closeSocket();
            }
        }

        private void sendMessageToOtherClients(String message) {
            synchronized (clientHandlers) {
                for (ChatClientHandler client : clientHandlers) {
                    if (client != this) { // Don't send the message to the sender
                        try {
                            client.out.write(message);
                            client.out.newLine();
                            client.out.flush();
                        } catch (IOException e) {
                            System.err.println("Error sending message to a client: " + e.getMessage());
                        }
                    }
                }
            }
        }

        private void removeClientHandler() {
            synchronized (clientHandlers) {
                clientHandlers.remove(this);
            }
        }

        private void closeSocket() {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}