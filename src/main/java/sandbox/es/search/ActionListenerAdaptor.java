package sandbox.es.search;

import java.util.function.Consumer;

import org.elasticsearch.action.ActionListener;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

public class ActionListenerAdaptor<T> implements ActionListener<T> {
    private final Sinks.One<T> sinksOne;

    public ActionListenerAdaptor() {
        sinksOne = Sinks.one();
    }

    public static <T> Mono<T> execute(Consumer<ActionListener<T>> func) {
        final ActionListenerAdaptor<T> listener = new ActionListenerAdaptor<>();
        return listener.toMono()
                       .doOnSubscribe(d -> func.accept(listener));
    }

    public Mono<T> toMono() {
        return sinksOne.asMono();
    }

    @Override
    public void onResponse(T result) {
        if (result == null) {
            sinksOne.tryEmitError(new NullPointerException());
        } else {
            sinksOne.tryEmitValue(result);
        }
    }

    @Override
    public void onFailure(Exception e) {
        sinksOne.tryEmitError(e);
    }
}
