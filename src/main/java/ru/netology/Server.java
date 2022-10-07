package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final static int THREADS = 64; // Задаем количество потоков в пуле потоков
    // Map для хранения пар: ключ - метод, значение - другая Map
    // Вложенная Map для хранения пар: ключ - путь, значение - Handler
    private ConcurrentHashMap<String, ConcurrentHashMap> handlers = new ConcurrentHashMap<>();
    // Создаем пул потоков фиксированного размера
    private final ExecutorService es = Executors.newFixedThreadPool(THREADS);

    public void listen(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                es.submit(new Response(socket, handlers));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHandler(String method, String fullPath, Handler handler) {
        if (!handlers.containsKey(method)) {
            System.out.println("Успешно создана вложенная Map с ключом/метод: " + method);
            handlers.put(method, creatingNestedCollection(fullPath, handler));
        } else {
            //если метод есть(ключ), то добавляем новый обработчик с новым путем и обработчиком
            correctionOfTheCollection(handlers.get(method), fullPath, handler);
        }
    }

    private ConcurrentHashMap<String, Handler> creatingNestedCollection(String fullPath, Handler handler) {
        // Map для хранения пар: ключ - путь, значение - Handler
        ConcurrentHashMap<String, Handler> nestedCollection = new ConcurrentHashMap<>();
        nestedCollection.put(returnLastPartOfPath(fullPath), handler);
        System.out.println("Во вложенную Map успешно добавлен handler по ключу/путь: " + returnLastPartOfPath(fullPath));
        return nestedCollection;
    }

    private void correctionOfTheCollection(ConcurrentHashMap<String, Handler> correctedCollection, String fullPath, Handler handler) {
        String lastPartOfPath = returnLastPartOfPath(fullPath);
        if (!correctedCollection.containsKey(lastPartOfPath)) {
            correctedCollection.put(lastPartOfPath, handler);
            System.out.println("Во вложенную Map успешно добавлен handler по ключу: " + lastPartOfPath);
        } else {
            System.out.println("Ошибка - во вложенной Map уже существует handler по ключу: " + lastPartOfPath);
        }
    }

    private String returnLastPartOfPath(String fullPath) {
        var parts = fullPath.split("/");
        String lastPartOfPath = "/" + parts[parts.length - 1];
        System.out.println("");
        return lastPartOfPath;
    }

}