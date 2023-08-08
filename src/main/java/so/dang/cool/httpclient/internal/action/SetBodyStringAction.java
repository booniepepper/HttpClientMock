package so.dang.cool.httpclient.internal.action;

import so.dang.cool.httpclient.Action;
import so.dang.cool.httpclient.MockedServerResponse;

import java.nio.charset.Charset;

public final class SetBodyStringAction implements Action {

    private final String content;
    private final Charset charset;

    public SetBodyStringAction(String content, Charset charset) {
        this.content = content;
        this.charset = charset;
    }

    @Override
    public void enrichResponse(MockedServerResponse.Builder responseBuilder) {
        responseBuilder.setBodyBytes(charset.encode(content));
    }
}
