package ru.netology;

import java.io.IOException;

@FunctionalInterface
public interface Handler<Request, BufferedOutputStream> {
    void handle(Request request, BufferedOutputStream responseStream) throws IOException;
}
