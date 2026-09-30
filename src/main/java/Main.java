import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");

        try {
            ServerSocket serverSocket = new ServerSocket(6379);
            serverSocket.setReuseAddress(true);

            Socket clientSocket = serverSocket.accept();

            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            while (true) {
                int firstByte = inputStream.read();

                if (firstByte == -1) {
                    break;
                }

                // Read the rest of the RESP command.
                // For this stage we only need to consume the command
                // and send PONG for each command received.

                if (firstByte == '*') {
                    readLine(inputStream); // number of array elements

                    int dollar = inputStream.read();

                    if (dollar == '$') {
                        String length = readLine(inputStream);
                        int commandLength = Integer.parseInt(length);

                        byte[] command = new byte[commandLength];
                        readFully(inputStream, command);

                        inputStream.read(); // \r
                        inputStream.read(); // \n

                        outputStream.write("+PONG\r\n".getBytes());
                        outputStream.flush();
                    }
                }
            }

            clientSocket.close();

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static String readLine(InputStream inputStream) throws IOException {
        StringBuilder line = new StringBuilder();

        int current;
        while ((current = inputStream.read()) != -1) {
            if (current == '\r') {
                inputStream.read(); // \n
                break;
            }
            line.append((char) current);
        }

        return line.toString();
    }

    private static void readFully(InputStream inputStream, byte[] buffer)
            throws IOException {

        int offset = 0;

        while (offset < buffer.length) {
            int bytesRead = inputStream.read(buffer, offset, buffer.length - offset);

            if (bytesRead == -1) {
                throw new IOException("Connection closed");
            }

            offset += bytesRead;
        }
    }
}