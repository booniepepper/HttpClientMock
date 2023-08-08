package so.dang.cool.httpclient.internal.rule;

import so.dang.cool.httpclient.MockedServerResponse;
import so.dang.cool.httpclient.internal.UrlConditions;
import so.dang.cool.httpclient.Action;
import so.dang.cool.httpclient.internal.action.ActionBundle;
import so.dang.cool.httpclient.Condition;
import so.dang.cool.httpclient.internal.debug.Debugger;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Queue;

public final class Rule {

    private final UrlConditions urlConditions;
    private final List<Condition> conditions;
    private final Queue<ActionBundle> actionBundles;

    public Rule(UrlConditions urlConditions, List<Condition> conditions, Queue<ActionBundle> actionBundles) {
        this.urlConditions = urlConditions;
        this.conditions = conditions;
        this.actionBundles = actionBundles;
    }

    public boolean matches(HttpRequest request) {
        return urlConditions.matches(request.uri()) &&
                conditions.stream().allMatch(c -> c.matches(request));
    }

    public MockedServerResponse produceResponse() throws IOException {
        final var responseBuilder = new MockedServerResponse.Builder();

        final var actionBundle = actionBundles.size() > 1 ? actionBundles.poll() : actionBundles.peek();
        for (Action a : actionBundle) {
            a.enrichResponse(responseBuilder);
        }

        return responseBuilder.build();
    }

    public void debug(HttpRequest request, Debugger debugger) {
        for (Condition condition : conditions) {
            debugCondition(condition, request, debugger);
        }
        urlConditions.debug(request, debugger);
    }

    private void debugCondition(Condition condition, HttpRequest request, Debugger debugger) {
        debugger.message(condition.matches(request), condition.getDebugMessage());
    }
}
