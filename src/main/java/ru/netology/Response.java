package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Response implements Runnable {
    final Socket socket;
    final ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> methods;

    Response(Socket socket, ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> methods) {
        this.socket = socket;
        this.methods = methods;
    }

    @Override
    public void run() {
        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            final var request = Request.createRequest(in);
            System.out.println("Зарегистрирован запрос: " + request.getMethod() + " "
                    + request.getPathWithoutParams() + " " + request.getQueryParams() + " " + request.getProtocol());
            if (methods.containsKey(request.getMethod())) {
                System.out.println("Есть вложенная Map с ключом: " + request.getMethod());
                if (methods.get(request.getMethod()).containsKey(request.getPathWithoutParams())) {
                    System.out.println("Во вложенной Map есть handler по ключу: " + request.getPathWithoutParams());
                    var first = methods.get(request.getMethod());
                    var handler = (Handler) first.get(request.getPathWithoutParams());
                    handler.handle(request, out);
                    System.out.println("Успешно направлен ответ на запрос!");
                } else {
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