package so.dang.cool.httpclient;

import java.io.IOException;

public interface Action {
    void enrichResponse(MockedServerResponse.Builder responseBuilder) throws IOException;
}
