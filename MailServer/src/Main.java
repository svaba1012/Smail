import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5001);
            System.out.println("Server started on port 5000");
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");
                Thread mailServiceThread = new Thread(new MailServiceThread(socket));
                mailServiceThread.start();
            }
        } catch (IOException e) {
            System.out.println("Error on port 5000" + e.getMessage());
        }
    }
}