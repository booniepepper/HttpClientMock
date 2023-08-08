package so.dang.cool.httpclient.internal.action;

import so.dang.cool.httpclient.Action;
import so.dang.cool.httpclient.MockedServerResponse;

import java.io.IOException;

public final class ThrowExceptionAction implements Action {

    private final IOException exception;

    public ThrowExceptionAction(IOException exception) {
        this.exception = exception;
    }

    @Override
    public void enrichResponse(MockedServerResponse.Builder responseBuilder) throws IOException {
        throw exception;
    }
}
