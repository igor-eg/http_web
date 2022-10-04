import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    final int numberOfThreads = 64;
    ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);

    public void listenToThePort(int port) {
        System.out.println("Server started");
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                System.out.println(socket);
                ClientHandler clientHandler = new ClientHandler(socket);
                service.submit(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}