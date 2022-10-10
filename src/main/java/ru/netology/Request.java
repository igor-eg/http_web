
package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Request {
    private final String method;
    private final String path;
    private final String protocol;
    private final String body;
    private final List<NameValuePair> params;

    public Request(String method, String path, String protocol, String body) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.body = body;
        List<NameValuePair> params1;
        try {
            params1 = URLEncodedUtils.parse(new URI(path), String.valueOf(StandardCharsets.UTF_8));
        } catch (URISyntaxException exception) {
            params1 = null;
            exception.printStackTrace();
        }
        this.params = params1;
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

    public Collection<String> getQueryParam(String name) {
        List<String> selectedParam = new ArrayList<>();
        for (NameValuePair param : params) {
            if (name.equals(param.getName())) {
                selectedParam.add(param.getValue());
            }
        }
        return selectedParam;
    }

    public Collection<NameValuePair> getQueryParams() {
        return params;
    }

    public String getPathWithoutParams() {
        if (path.contains("?")) {
            String result = path.substring(0, path.indexOf('?'));
            return result;
        }
        return path;
    }

    public static Request createRequest(BufferedReader in) throws IOException {
        var requestLine = in.readLine();
        var parts = requestLine.split(" ");
        if (parts.length >= 3) {
            return new Request(parts[0], parts[1], parts[2], parts.length == 4 ? parts[3] : null);
        } else {
            return null;
        }
    }

}
