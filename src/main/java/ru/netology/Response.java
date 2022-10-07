package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Response implements Runnable {
    final Socket socket;
    final ConcurrentHashMap<String, ConcurrentHashMap> firstLevel;

    Response(Socket socket, ConcurrentHashMap<String, ConcurrentHashMap> firstLevel) {
        this.socket = socket;
        this.firstLevel = firstLevel;
    }

    @Override
    public void run() {
        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            final var request = createRequest(in);
            System.out.println("Зарегистрирован запрос: " + request.getMethod() + " "
                    + request.getPath() + " " + request.getProtocol());

            if (firstLevel.containsKey(request.getMethod())) {
                System.out.println("Есть вложенная Map с ключом: " + request.getMethod());
                if (firstLevel.get(request.getMethod()).containsKey(request.getPath())) {
                    System.out.println("Во вложенной Map есть handler по ключу: " + request.getPath());
                    var first = firstLevel.get(request.getMethod());
                    var handler = (Handler) first.get(request.getPath());
                    handler.handle(request, out);
                    System.out.println("Успешно направлен ответ на запрос!");
                } else  {
                    returnError(out);
                    System.out.println("На запрос направлен ответ об ошибке!");
                }
            } else {
                returnError(out);
                System.out.println("На запрос направлен ответ об ошибке!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Request createRequest(BufferedReader in) throws IOException {
        var requestLine = in.readLine();
        var parts = requestLine.split(" ");
        if (parts.length >= 3) {
            return new Request(parts[0], parts[1], parts[2], parts.length == 4 ? parts[3] : null);
        } else {
            return null;
        }
    }

    private void returnError(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

}
