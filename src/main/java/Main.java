import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(6379);

            Socket clientSocket = serverSocket.accept();

            clientSocket.getOutputStream().write("+PONG\r\n".getBytes());
            clientSocket.getOutputStream().flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}