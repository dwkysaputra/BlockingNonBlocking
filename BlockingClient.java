import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BlockingClient {

    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int serverPort = 12345;

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to the server.");

            Scanner scanner = new Scanner(System.in);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                System.out.print("Enter a message to send to the server: ");
                String message = scanner.nextLine();
                writer.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
