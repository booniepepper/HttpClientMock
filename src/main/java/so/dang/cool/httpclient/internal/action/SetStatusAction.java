package so.dang.cool.httpclient.internal.action;

import so.dang.cool.httpclient.Action;
import so.dang.cool.httpclient.MockedServerResponse;

public final class SetStatusAction implements Action {

    private final int status;

    public SetStatusAction(int status) {
        this.status = status;
    }

    @Override
    public void enrichResponse(MockedServerResponse.Builder responseBuilder) {
        responseBuilder.setStatusCode(status);
    }
}
