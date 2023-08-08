package so.dang.cool.httpclient.internal.rule;

import so.dang.cool.httpclient.internal.UrlConditions;
import so.dang.cool.httpclient.Action;
import so.dang.cool.httpclient.internal.action.ActionBundle;
import so.dang.cool.httpclient.Condition;
import so.dang.cool.httpclient.internal.condition.MethodCondition;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.*;

public final class RuleBuilder {

    private final Deque<ActionBundle> actionBundles = new LinkedList<>();
    private final List<Condition> conditions = new ArrayList<>();
    private final UrlConditions urlConditions;

    public RuleBuilder(String method, String host, String url) {
        url = url.startsWith("/") ? host + url : url;
        addCondition(new MethodCondition(method));
        urlConditions = UrlConditions.parse(url);
    }

    public RuleBuilder(String method) {
        addCondition(new MethodCondition(method));
        urlConditions = new UrlConditions();
    }

    public void addAction(Action action) {
        var bundle = actionBundles.peekLast();
        if (bundle == null) {
            bundle = new ActionBundle();
            actionBundles.add(bundle);
        }
        bundle.add(action);
    }

    public void addActionBundle(Action action) {
        final var bundle = new ActionBundle();
        bundle.add(action);
        actionBundles.add(bundle);
    }

    public void addCondition(Condition condition) {
        conditions.add(condition);
    }


    public void setParameterCondition(String name, Matcher<String> matcher) {
        urlConditions.getParameterConditions().addParam(name, Matchers.containsInAnyOrder(List.of(matcher)));
    }

    public void setReferenceCondition(Matcher<String> matcher) {
        urlConditions.setReferenceCondition(matcher);
    }

    public void addHostCondition(String host) {
       urlConditions.setHostCondition(Matchers.equalTo(host));
    }

    public void setPathCondition(Matcher<String> matcher) {
        urlConditions.setPathCondition(matcher);
    }

    public Rule build() {
        return new Rule(urlConditions, conditions, actionBundles);
    }
}
