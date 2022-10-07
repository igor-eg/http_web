package ru.netology;

public class Request {
    private String method;
    private String path;
    private String protocol;
    private String body;

    public Request(String method, String path, String protocol, String body) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getBody() {
        return body;
    }

}

