package so.dang.cool.httpclient.internal.condition;

import so.dang.cool.httpclient.Condition;

import java.net.http.HttpRequest;

public final class MethodCondition implements Condition {

    private final String method;

    public MethodCondition(String method) {
        this.method = method;
    }

    @Override
    public boolean matches(HttpRequest request) {
        return request.method().equals(method);
    }

    @Override
    public String getDebugMessage() {
        return "HTTP method is " + method;
    }
}
