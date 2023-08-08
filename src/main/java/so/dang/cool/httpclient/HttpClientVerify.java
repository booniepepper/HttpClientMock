package so.dang.cool.httpclient;

import so.dang.cool.httpclient.internal.rule.RuleBuilder;
import so.dang.cool.httpclient.internal.HttpMethods;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Objects;

public final class HttpClientVerify {

    private final String defaultHost;
    private final List<HttpRequest> requests;

    HttpClientVerify(String defaultHost, List<HttpRequest> requests) {
        this.requests = requests;
        this.defaultHost = defaultHost;
    }

    private HttpClientVerifyBuilder newRule(String method) {
        RuleBuilder r = new RuleBuilder(method);
        return new HttpClientVerifyBuilder(r, requests);
    }

    private HttpClientVerifyBuilder newRule(String method, String url) {
        RuleBuilder r = new RuleBuilder(method, defaultHost, url);
        return new HttpClientVerifyBuilder(r, requests);
    }

    public HttpClientVerifyBuilder post(String url) {
        Objects.requireNonNull(url, "URL must be not null");
        return newRule(HttpMethods.POST, url);
    }

    public HttpClientVerifyBuilder get(String url) {
        Objects.requireNonNull(url, "URL must be not null");
        return newRule(HttpMethods.GET, url);
    }

    public HttpClientVerifyBuilder put(String url) {
        Objects.requireNonNull(url, "URL must be not null");
        return newRule(HttpMethods.PUT, url);
    }

    public HttpClientVerifyBuilder delete(String url) {
        Objects.requireNonNull(url, "URL must be not null");
        return newRule(HttpMethods.DELETE, url);
    }

    public HttpClientVerifyBuilder head(String url) {
        Objects.requireNonNull(url, "URL must be not null");
        return newRule(HttpMethods.HEAD, url);
    }

    public HttpClientVerifyBuilder options(String url) {
        Objects.requireNonNull(url, "URL must be not null");
        return newRule(HttpMethods.OPTIONS, url);
    }

    public HttpClientVerifyBuilder patch(String url) {
        Objects.requireNonNull(url, "URL must be not null");
        return newRule(HttpMethods.PATCH, url);
    }

    public HttpClientVerifyBuilder post() {
        return newRule(HttpMethods.POST);
    }

    public HttpClientVerifyBuilder get() {
        return newRule(HttpMethods.GET);
    }

    public HttpClientVerifyBuilder put() {
        return newRule(HttpMethods.PUT);
    }

    public HttpClientVerifyBuilder delete() {
        return newRule(HttpMethods.DELETE);
    }

    public HttpClientVerifyBuilder head() {
        return newRule(HttpMethods.HEAD);
    }

    public HttpClientVerifyBuilder options() {
        return newRule(HttpMethods.OPTIONS);
    }

    public HttpClientVerifyBuilder patch() {
        return newRule(HttpMethods.PATCH);
    }

}
